package um

import "hrvanovicm/magacin/core"

type SaveCommand = UnitMeasure

func Save(r core.Request, cmd SaveCommand) error {
	err := r.DB.WithContext(r.Ctx).Save(&cmd).Error
	if err != nil {
		return err
	}

	return nil
}

type DeleteCommand struct {
	ID uint
}

func Delete(r core.Request, cmd DeleteCommand) error {
	err := r.DB.WithContext(r.Ctx).
		Delete(&UnitMeasure{}, cmd.ID).
		Error

	if err != nil {
		return err
	}

	return nil
}
