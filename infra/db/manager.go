package db

import (
	"github.com/jmoiron/sqlx"
	_ "github.com/mattn/go-sqlite3"
)

type Manager struct {
	connection string
	Conn       *sqlx.DB
}

func NewDB(connection string) *Manager {
	return &Manager{
		connection: connection,
	}
}

func (db *Manager) Connect() error {
	conn, err := sqlx.Connect("sqlite3", db.connection)

	if err != nil {
		return err
	}

	db.Conn = conn
	return nil
}

func (db *Manager) Close() error {
	return db.Conn.Close()
}
