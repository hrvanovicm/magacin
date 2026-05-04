package app

import (
	"fmt"
	"hrvanovicm/magacin/infra/paged"
	"hrvanovicm/magacin/internal/activitylog"
	"hrvanovicm/magacin/internal/report"
	"os"
	"path/filepath"
	"strings"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

func (a *WailsApp) ListReportTypes() []report.Type {
	return report.ValidListTypes
}

type ListReportsRequest = report.ListQuery

func (a *WailsApp) ListReports(req ListReportsRequest) ([]report.Report, error) {
	reports, err := report.List(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return reports, err
	}

	return reports, nil
}

type ListReportsPagedRequest = report.ListPagedQuery

func (a *WailsApp) ListReportsPaged(req ListReportsPagedRequest) (paged.PagedResult[report.Report], error) {
	reports, err := report.ListPaged(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return reports, err
	}

	return reports, nil
}

func (a *WailsApp) ListSignUsers() ([]string, error) {
	users, err := report.ListSignUsers(a.getRequest())
	if err != nil {
		return nil, err
	}

	return users, nil
}

func (a *WailsApp) ListReportPublicLocations() ([]string, error) {
	locations, err := report.ListPublishLocations(a.getRequest())
	if err != nil {
		a.report(err)
		return locations, err
	}

	return locations, nil
}

func (a *WailsApp) GetNextReportCodeForType(reportType report.Type) (string, error) {
	reportCode, err := report.GetNextReportCodeForType(a.getRequest(), reportType)
	if err != nil {
		a.report(err)
		return reportCode, err
	}
	return reportCode, nil
}

type SaveReportRequest = report.SaveCommand

func (a *WailsApp) SaveReport(req SaveReportRequest) (uint, error) {
	repID, err := report.Save(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return 0, err
	}

	return repID, nil
}

type DeleteReportRequest = report.DeleteCommand

func (a *WailsApp) DeleteReport(req DeleteReportRequest) error {
	if err := report.Delete(a.getRequest(), req); err != nil {
		a.report(err)
		return err
	}

	return nil
}

type GetReportLogsRequest = report.GetLogsQuery

func (a *WailsApp) GetReportLogs(req GetReportLogsRequest) ([]activitylog.Entry, error) {
	logs, err := report.GetLogs(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return nil, err
	}
	return logs, nil
}

func (a *WailsApp) ExportReport(id int64) error {
	path, err := runtime.SaveFileDialog(a.getRequest().Ctx, runtime.SaveDialogOptions{
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
	path, err := runtime.SaveFileDialog(a.getRequest().Ctx, runtime.SaveDialogOptions{
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
	path, err := runtime.SaveFileDialog(a.getRequest().Ctx, runtime.SaveDialogOptions{
		Title:           "Izvoz izvještaja",
		DefaultFilename: "izvjestaji.xlsx",
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
