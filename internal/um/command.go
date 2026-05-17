package um

import (
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/internal/activitylog"
)

type SaveCommand = UnitMeasure

func Save(r app.Request, cmd SaveCommand) (uint, error) {
	var old *UnitMeasure
	if cmd.ID > 0 {
		var existing UnitMeasure
		if err := r.DB.WithContext(r.Ctx).First(&existing, cmd.ID).Error; err == nil {
			old = &existing
		}
	}

	err := r.DB.WithContext(r.Ctx).Save(&cmd).Error
	if err != nil {
		return 0, err
	}

	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(cmd.ID), UnitMeasure{})
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
	err := r.DB.WithContext(r.Ctx).
		Delete(&UnitMeasure{}, cmd.ID).
		Error

	if err == nil {
		cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(cmd.ID), UnitMeasure{})
		cl.LogDelete()
	}

	return err
}

type SaveConversionCommand = Conversion

func SaveConversion(r app.Request, cmd SaveConversionCommand) (int64, error) {
	err := r.DB.WithContext(r.Ctx).Save(&cmd).Error
	if err != nil {
		return 0, err
	}
	return cmd.ID, nil
}

type DeleteConversionCommand struct {
	ID int64
}

func DeleteConversion(r app.Request, cmd DeleteConversionCommand) error {
	return r.DB.WithContext(r.Ctx).Delete(&Conversion{}, cmd.ID).Error
}
