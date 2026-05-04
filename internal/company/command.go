package company

import (
	"hrvanovicm/magacin/infra/app"
)

type SaveCommand = Company

func Save(r app.Request, cmd SaveCommand) (uint, error) {
	if err := r.DB.WithContext(r.Ctx).Save(&cmd).Error; err != nil {
		return 0, err
	}

	return cmd.ID, nil
}

type DeleteCommand struct {
	ID uint
}

func Delete(r app.Request, cmd DeleteCommand) error {
	if err := r.DB.WithContext(r.Ctx).Delete(&Company{}, cmd.ID).Error; err != nil {
		return err
	}

	return nil
}
