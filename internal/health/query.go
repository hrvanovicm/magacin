package health

import (
	"hrvanovicm/magacin/core"

	"hrvanovicm/magacin/internal/server"
)

const (
	DefaultServerName = "Magacin"
)

type CheckHealthResult struct {
	Up   bool   `json:"up"`
	Name string `json:"name"`
}

func CheckHealth(r core.Request) CheckHealthResult {
	defaultName := DefaultServerName
	if cfg, err := server.GetLocalConfig(r); err == nil && cfg.Name != "" {
		defaultName = cfg.Name
	}

	return CheckHealthResult{Up: true, Name: defaultName}
}
