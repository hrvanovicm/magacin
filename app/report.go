package app

import (
	"fmt"
	"hrvanovicm/magacin/infra/paged"
	"hrvanovicm/magacin/internal/activitylog"
	"hrvanovicm/magacin/internal/report"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

func (a *WailsApp) ListReportTypes() []report.Type {
	return report.ValidListTypes
}

type ListReportsRequest = report.ListQuery

func (a *WailsApp) ListReports(req ListReportsRequest) ([]report.Report, error) {
	res, err := report.List(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type ListReportsPagedRequest = report.ListPagedQuery

func (a *WailsApp) ListReportsPaged(req ListReportsPagedRequest) (paged.PagedResult[report.Report], error) {
	res, err := report.ListPaged(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

func (a *WailsApp) ListSignUsers() ([]string, error) {
	users, err := report.ListSignUsers(a.getRequest())
	if err != nil {
		return nil, err
	}

	return users, nil
}

func (a *WailsApp) ListReportPublicLocations() ([]string, error) {
	res, err := report.ListPublishLocations(a.getRequest())
	if err != nil {
		a.report(err)
	}
	return res, err
}

func (a *WailsApp) GetNextReportCodeForType(reportType report.Type) (string, error) {
	res, err := report.GetNextReportCodeForType(a.getRequest(), reportType)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type GetReportRequest = report.GetQuery

func (a *WailsApp) GetReport(req GetReportRequest) (*report.Report, error) {
	res, err := report.Get(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type SaveReportRequest = report.SaveCommand

func (a *WailsApp) SaveReport(req SaveReportRequest) (uint, error) {
	res, err := report.Save(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type DeleteReportRequest = report.DeleteCommand

func (a *WailsApp) DeleteReport(req DeleteReportRequest) error {
	err := report.Delete(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
}

type GetReportLogsRequest = report.GetLogsQuery

func (a *WailsApp) GetReportLogs(req GetReportLogsRequest) ([]activitylog.Entry, error) {
	res, err := report.GetLogs(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

func (a *WailsApp) ExportReport(id int64) error {
	path, err := runtime.SaveFileDialog(a.ctx, runtime.SaveDialogOptions{
		Title:           "Izvoz izvještaja",
		DefaultFilename: fmt.Sprintf("izvjestaj-%d.xlsx", id),
		Filters: []runtime.FileFilter{
			{DisplayName: "Excel (*.xlsx)", Pattern: "*.xlsx"},
		},
	})

	if err != nil {
		return fmt.Errorf("export dialog: %w", err)
	}

	if path == "" {
		return nil
	}

	if !strings.HasSuffix(path, ".xlsx") {
		path += ".xlsx"
	}

	data, err := report.GetExport(a.getRequest(), report.GetQuery{ID: uint(id)})
	if err != nil {
		a.report(err)
		return err
	}

	if err := os.WriteFile(path, data, 0644); err != nil {
		a.report(err)
		return fmt.Errorf("export write: %w", err)
	}
	return nil
}

func (a *WailsApp) ExportWorkOrder(id int64) error {
	path, err := runtime.SaveFileDialog(a.ctx, runtime.SaveDialogOptions{
		Title:           "Izvoz radnog naloga",
		DefaultFilename: fmt.Sprintf("radni-nalog-%d.xlsx", id),
		Filters: []runtime.FileFilter{
			{DisplayName: "Excel (*.xlsx)", Pattern: "*.xlsx"},
		},
	})
	if err != nil {
		return fmt.Errorf("export dialog: %w", err)
	}
	if path == "" {
		return nil
	}
	if !strings.HasSuffix(path, ".xlsx") {
		path += ".xlsx"
	}

	data, err := report.ExportWorkOrderXLSX(a.getRequest(), report.GetQuery{ID: uint(id)})
	if err != nil {
		a.report(err)
		return err
	}

	if err := os.WriteFile(path, data, 0644); err != nil {
		a.report(err)
		return fmt.Errorf("export write: %w", err)
	}

	return nil
}

func (a *WailsApp) ExportReports(req ListReportsRequest) error {
	currentDate := time.Now().Format("2006-01-02")
	path, err := runtime.SaveFileDialog(a.ctx, runtime.SaveDialogOptions{
		Title:           "Izvoz izvještaja",
		DefaultFilename: fmt.Sprintf("izvjestaji-%s.xlsx", currentDate),
		Filters: []runtime.FileFilter{
			{DisplayName: "Excel (*.xlsx)", Pattern: "*.xlsx"},
			{DisplayName: "CSV (*.csv)", Pattern: "*.csv"},
		},
	})
	if err != nil {
		return fmt.Errorf("export dialog: %w", err)
	}
	if path == "" {
		return nil
	}

	var data []byte
	switch strings.ToLower(filepath.Ext(path)) {
	case ".csv":
		data, err = report.ListExport(a.getRequest(), report.ListExportQuery{
			ListQuery: req,
			Format:    report.FormatXLSX,
		})
	default:
		if !strings.HasSuffix(path, ".xlsx") {
			path += ".xlsx"
		}
		data, err = report.ListExport(a.getRequest(), report.ListExportQuery{
			ListQuery: req,
			Format:    report.FormatXLSX,
		})
	}
	if err != nil {
		a.report(err)
		return err
	}

	if err := os.WriteFile(path, data, 0644); err != nil {
		a.report(err)
		return fmt.Errorf("export write: %w", err)
	}

	return nil
}
