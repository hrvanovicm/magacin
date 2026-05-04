package um

import (
	"hrvanovicm/magacin/infra/app"
)

type SaveCommand = UnitMeasure

func Save(r app.Request, cmd SaveCommand) (uint, error) {
	err := r.DB.WithContext(r.Ctx).Save(&cmd).Error
	if err != nil {
		return 0, err
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

	if err != nil {
		return err
	}

	return nil
}
