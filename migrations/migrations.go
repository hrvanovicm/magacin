package migrations

import (
	"embed"
	"errors"
	"fmt"
	"hrvanovicm/magacin/infra/db"

	"github.com/golang-migrate/migrate/v4"
	"github.com/golang-migrate/migrate/v4/database/sqlite3"
	_ "github.com/golang-migrate/migrate/v4/source/file"
	"github.com/golang-migrate/migrate/v4/source/iofs"
)

//go:embed scripts/*.sql
var Files embed.FS

func RunMigrations(db *db.Manager) {
	driver, err := sqlite3.WithInstance(db.Conn.DB, &sqlite3.Config{})
	if err != nil {
		panic("cannot create migration sqlite instance")
	}

	d, err := iofs.New(Files, "scripts")
	if err != nil {
		panic(err)
	}

	m, err := migrate.NewWithInstance("iofs", d, "sqlite3", driver)
	if err != nil {
		panic(err)
	}

	// For development use. TODO
	// fmt.Println("Reverting all migrations...")
	// if err := m.Down(); err != nil && !errors.Is(err, migrate.ErrNoChange) {
	// 	fmt.Printf("Error running Down migrations: %v\n", err)
	// } else {
	// 	fmt.Println("Successfully reverted all migrations.")
	// }

	fmt.Println("Running migrations...")
	if err := m.Up(); err != nil && !errors.Is(err, migrate.ErrNoChange) {
		fmt.Println("Error2")
	}
}
