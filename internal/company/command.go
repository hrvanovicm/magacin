package company

import "hrvanovicm/magacin/core"

type SaveCommand = Company

func Save(r core.Request, cmd SaveCommand) error {
	if err := r.DB.WithContext(r.Ctx).Save(&cmd).Error; err != nil {
		return err
	}

	return nil
}

type DeleteCommand struct {
	ID uint
}

func Delete(r core.Request, cmd DeleteCommand) error {
	if err := r.DB.WithContext(r.Ctx).Delete(&Company{}, cmd.ID).Error; err != nil {
		return err
	}

	return nil
}
