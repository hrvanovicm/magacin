package app

import (
	"hrvanovicm/magacin/internal/health"
)

func (a *WailsApp) CheckHealth() health.CheckHealthResult {
	return health.CheckHealth(a.getRequest())
}
