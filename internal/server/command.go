package server

import (
	"hrvanovicm/magacin/infra/app"

	"gorm.io/gorm"
)

type SaveConfigCommand = LocalConfig

func SaveConfig(r app.Request, cmd SaveConfigCommand) error {
	return r.DB.WithContext(r.Ctx).
		Session(&gorm.Session{AllowGlobalUpdate: true}).
		Save(&cmd).
		Error
}

type SaveServerCommand = Server

func SaveServer(r app.Request, cmd SaveServerCommand) error {
	return r.DB.WithContext(r.Ctx).Save(&cmd).Error
}

type UpdateLastUsedUsernameCommand struct {
	Address  string
	Username string
}

func UpdateLastUsedUsername(r app.Request, cmd UpdateLastUsedUsernameCommand) error {
	return r.DB.WithContext(r.Ctx).
		Model(&Server{}).
		Where("address = ?", cmd.Address).
		Update("last_used_username", cmd.Username).
		Error
}

type UpsertServerLastUsedCommand struct {
	ServerName    string
	ServerAddress string
	Username      string
}

func UpsertServerLastUsed(r app.Request, cmd UpsertServerLastUsedCommand) error {
	var s Server

	err := r.DB.WithContext(r.Ctx).
		Where("address = ?", cmd.ServerAddress).
		FirstOrCreate(&s, Server{Name: cmd.ServerName, Address: cmd.ServerAddress}).
		Error
	if err != nil {
		return err
	}

	return r.DB.WithContext(r.Ctx).
		Model(&s).
		Update("last_used_username", cmd.Username).
		Error
}

func BatchDeleteServers(r app.Request) error {
	return r.DB.WithContext(r.Ctx).
		Session(&gorm.Session{AllowGlobalUpdate: true}).
		Delete(&Server{}).
		Error
}
