package core

import (
	"context"

	"gorm.io/gorm"
)

type User struct {
	ID       uint
	Username string
	Role     *string
}

type Request struct {
	DB            *gorm.DB
	Ctx           context.Context
	ServerAddress string
	User          User
}
