package app

import (
	"context"
	"errors"
	"hrvanovicm/magacin/core"
	"hrvanovicm/magacin/dbmanager"
	"hrvanovicm/magacin/internal/ws"
	"hrvanovicm/magacin/migrations"
	"log"

	"os"
	"path/filepath"
	"time"

	_ "github.com/golang-migrate/migrate/v4/source/file"
	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"gorm.io/gorm/logger"
)

const (
	REQUEST_TIMEOUT = 5 * time.Second
)

type WailsApp struct {
	db                   *dbmanager.Manager
	gormDB               *gorm.DB
	ctx                  context.Context
	logger               *zap.Logger
	currentActorID       uint
	currentActorUsername string
	hub                  *ws.Hub
}

func NewApp() *WailsApp {
	return &WailsApp{}
}

func (a *WailsApp) Startup(ctx context.Context) {
	a.ctx = ctx

	a.InitSetup()

	dbPath := getDatabaseFullPath()
	a.db = dbmanager.NewDB(dbPath)
	if err := a.db.Connect(); err != nil {
		a.report(err)
		panic(err)
	}

	loggerCfg := zap.Config{
		Level:            zap.NewAtomicLevelAt(zapcore.DebugLevel),
		Development:      true,
		Encoding:         "console",
		OutputPaths:      []string{"stdout"},
		ErrorOutputPaths: []string{"stderr"},
		EncoderConfig: zapcore.EncoderConfig{
			MessageKey:     "msg",
			LevelKey:       "",
			TimeKey:        "",
			NameKey:        "",
			CallerKey:      "",
			FunctionKey:    "",
			EncodeLevel:    zapcore.CapitalLevelEncoder,
			EncodeTime:     nil,
			EncodeDuration: nil,
		},
	}

	if logger, err := loggerCfg.Build(); err != nil {
		panic(err)
	} else {
		a.logger = logger
	}

	migrations.RunMigrations(a.db)

	dsn := getDatabaseFullPath() + "?_journal_mode=WAL&_busy_timeout=5000"
	newLogger := logger.New(
		log.New(os.Stdout, "\r\n", log.LstdFlags), // io writer
		logger.Config{
			SlowThreshold:             time.Second, 
			LogLevel:                  logger.Info, 
			IgnoreRecordNotFoundError: true,   
			ParameterizedQueries:      false,  
			Colorful:                  true, 
		},
	)

	gormDB, err := gorm.Open(sqlite.Open(dsn), &gorm.Config{
		Logger: newLogger,
	})
	if err != nil {
		panic(err)
	}
	a.gormDB = gormDB

	a.hub = ws.NewHub()
	// handler := api.NewHandler(a.gormDB, a.hub)
	// router := handler.SetupRouter()
	// srv := &http.Server{Addr: ":8080", Handler: router}
	// go func() {
	// 	if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
	// 		a.report(err)
	// 	}
	// }()
}

func (a *WailsApp) Shutdown(ctx context.Context) {
	_ = a.db.Close()

	if a.logger != nil {
		a.logger.Sync()
	}
}

func (a *WailsApp) report(err error) {
	if a.logger != nil {
		a.logger.Error("error", zap.Error(err))
	}
}

func (a *WailsApp) database() *gorm.DB {
	return a.gormDB
}

func (a *WailsApp) GetOnlineUsers() []string {
	if a.hub == nil {
		return []string{}
	}

	return a.hub.GetUsers()
}

func (a *WailsApp) InitSetup() {
	dbPath := getDatabaseFullPath()

	if _, err := os.Stat(dbPath); errors.Is(err, os.ErrNotExist) {
		if err := os.MkdirAll(filepath.Dir(dbPath), os.ModePerm); err != nil {
			panic(err)
		}

		_, err := os.Create(dbPath)
		if err != nil {
			panic(err)
		}
	}
}

func getDatabaseFullPath() string {
	homeDir, err := os.UserHomeDir()
	if err != nil {
		panic(err)
	}

	return filepath.Join(homeDir, "hrvanovicm", "magacin", "magacin.db")
}

func (a *WailsApp) getRequest() core.Request {
	return core.Request{
		DB:  a.gormDB,
		Ctx: context.Background(),
	}
}

func getStorageDir() string {
	homeDir, err := os.UserHomeDir()
	if err != nil {
		panic(err)
	}
	dir := filepath.Join(homeDir, "hrvanovicm", "magacin", "storage")
	if err := os.MkdirAll(dir, os.ModePerm); err != nil {
		panic(err)
	}
	return dir
}
