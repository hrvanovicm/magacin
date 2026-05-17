package app

import (
	"hrvanovicm/magacin/internal/um"
)

type ListUnitMeasurementsRequest = um.ListQuery

func (a *WailsApp) ListUnitMeasurements(req ListUnitMeasurementsRequest) ([]um.UnitMeasure, error) {
	res, err := um.List(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type SaveUnitMeasureRequest = um.SaveCommand

func (a *WailsApp) SaveUnitMeasure(req SaveUnitMeasureRequest) (uint, error) {
	res, err := um.Save(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type DeleteUnitMeasureRequest = um.DeleteCommand

func (a *WailsApp) DeleteUnitMeasure(req DeleteUnitMeasureRequest) error {
	err := um.Delete(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
}

type SaveUnitMeasureConversionRequest = um.SaveConversionCommand

func (a *WailsApp) SaveUnitMeasureConversion(req SaveUnitMeasureConversionRequest) (int64, error) {
	res, err := um.SaveConversion(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type DeleteUnitMeasureConversionRequest = um.DeleteConversionCommand

func (a *WailsApp) DeleteUnitMeasureConversion(req DeleteUnitMeasureConversionRequest) error {
	err := um.DeleteConversion(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
}

type ListUnitMeasureConversionsRequest = um.ListConversionsQuery

func (a *WailsApp) ListUnitMeasureConversions(req ListUnitMeasureConversionsRequest) ([]um.Conversion, error) {
	res, err := um.ListConversions(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}
