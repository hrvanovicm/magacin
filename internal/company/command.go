package company

import (
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/internal/activitylog"
)

type SaveCommand = Company

func Save(r app.Request, cmd SaveCommand) (uint, error) {
	var old *Company
	if cmd.ID > 0 {
		var existing Company
		if err := r.DB.WithContext(r.Ctx).First(&existing, cmd.ID).Error; err == nil {
			old = &existing
		}
	}

	if err := r.DB.WithContext(r.Ctx).Save(&cmd).Error; err != nil {
		return 0, err
	}

	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(cmd.ID), Company{})
	if old == nil {
		cl.LogCreate()
	} else {
		cl.LogDiff(old, &cmd)
	}

	return cmd.ID, nil
}

type DeleteCommand struct {
	ID uint
}

func Delete(r app.Request, cmd DeleteCommand) error {
	err := r.DB.WithContext(r.Ctx).Delete(&Company{}, cmd.ID).Error
	if err == nil {
		cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(cmd.ID), Company{})
		cl.LogDelete()
	}

	return err
}
