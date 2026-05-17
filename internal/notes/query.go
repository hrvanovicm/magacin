package notes

import (
	"errors"
	"hrvanovicm/magacin/infra/app"

	"gorm.io/gorm"
)

type GetQuery struct {
	SubjectType string
	SubjectID   int64
}

func Get(r app.Request, qry GetQuery) (*Note, error) {
	var n Note

	err := r.DB.WithContext(r.Ctx).
		Where("subject_type = ? AND subject_id = ?", qry.SubjectType, qry.SubjectID).
		First(&n).
		Error

	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil, nil
		}
		return nil, err
	}

	return &n, nil
}
