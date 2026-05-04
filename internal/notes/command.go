package notes

import (
	"hrvanovicm/magacin/infra/app"
)

type SaveCommand = Note

func Save(r app.Request, cmd SaveCommand) error {
	return r.DB.WithContext(r.Ctx).Save(&cmd).Error
}
