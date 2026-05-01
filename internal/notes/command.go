package notes

import (
	"hrvanovicm/magacin/core"
)

type SaveCommand = Note

func Save(r core.Request, cmd SaveCommand) error {
	return r.DB.WithContext(r.Ctx).Save(&cmd).Error
}
