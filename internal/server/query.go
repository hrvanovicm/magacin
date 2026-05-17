package server

import (
	"hrvanovicm/magacin/infra/app"
)

func ListServers(r app.Request) ([]Server, error) {
	var servers []Server
	err := r.DB.WithContext(r.Ctx).
		Find(&servers).
		Error

	if err != nil {
		return nil, err
	}

	if servers == nil {
		return []Server{}, nil
	}

	return servers, nil
}

func GetLocalConfig(r app.Request) (*LocalConfig, error) {
	var cfg LocalConfig

	err := r.DB.WithContext(r.Ctx).
		First(&cfg).
		Error

	if err != nil {
		return nil, err
	}

	return &cfg, nil
}
