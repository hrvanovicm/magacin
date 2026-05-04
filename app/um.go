package app

import (
	"hrvanovicm/magacin/internal/um"
)

type ListUnitMeasurementsRequest = um.ListQuery

func (a *WailsApp) ListUnitMeasurements(req ListUnitMeasurementsRequest) ([]um.UnitMeasure, error) {
	unitMeasures, err := um.List(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return unitMeasures, err
	}

	return unitMeasures, nil
}

type SaveUnitMeasureRequest = um.SaveCommand

func (a *WailsApp) SaveUnitMeasure(req SaveUnitMeasureRequest) (uint, error) {
	umID, err := um.Save(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return 0, err
	}

	return umID, nil
}

type DeleteUnitMeasureRequest = um.DeleteCommand

func (a *WailsApp) DeleteUnitMeasure(req DeleteUnitMeasureRequest) error {
	if err := um.Delete(a.getRequest(), req); err != nil {
		a.report(err)
		return err
	}

	return nil
}
