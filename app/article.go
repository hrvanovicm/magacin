package app

import (
	"fmt"
	"hrvanovicm/magacin/infra/paged"
	"hrvanovicm/magacin/internal/activitylog"
	"hrvanovicm/magacin/internal/article"
	"hrvanovicm/magacin/internal/report"
	"os"
	"path/filepath"
	"strings"
	"time"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type ListArticlesRequest = article.ListQuery

func (a *WailsApp) ListArticles(req ListArticlesRequest) ([]article.Article, error) {
	res, err := article.List(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type ListArticlesPagedRequest = article.ListPagedQuery

func (a *WailsApp) ListArticlesPaged(req ListArticlesPagedRequest) (paged.PagedResult[article.Article], error) {
	res, err := article.ListPaged(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type GetArticleRequest = article.GetQuery

func (a *WailsApp) GetArticle(req GetArticleRequest) (*article.Article, error) {
	res, err := article.Get(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type SaveArticleRequest = article.SaveCommand

func (a *WailsApp) SaveArticle(req SaveArticleRequest) (uint, error) {
	return article.Save(a.getRequest(), req)
}

type DeleteArticleRequest = article.DeleteCommand

func (a *WailsApp) DeleteArticle(req DeleteArticleRequest) error {
	err := article.Delete(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
}

type GetArticleLogsRequest = article.GetLogsQuery

func (a *WailsApp) GetArticleLogs(req GetArticleLogsRequest) ([]activitylog.Entry, error) {
	res, err := article.GetLogs(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type GetArticleAnalyticsRequest = report.GetAnalyticsByArticleQuery

func (a *WailsApp) GetArticleAnalytics(req GetArticleAnalyticsRequest) ([]report.ArticleAnalyticsResult, error) {
	res, err := report.GetAnalyticsByArticle(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

func (a *WailsApp) ExportArticles(req ListArticlesRequest) error {
	currentDate := time.Now().Format("2006-01-02")
	path, err := runtime.SaveFileDialog(a.ctx, runtime.SaveDialogOptions{
		Title:           "Izvoz robe",
		DefaultFilename: fmt.Sprintf("roba-%s.xlsx", currentDate),
		Filters: []runtime.FileFilter{
			{DisplayName: "Excel (*.xlsx)", Pattern: "*.xlsx"},
		},
	})

	if err != nil {
		return fmt.Errorf("export dialog: %w", err)
	}

	if ".xlsx" != strings.ToLower(filepath.Ext(path)) {
		return fmt.Errorf("invalid file extension")
	}

	data, err := article.GetExport(a.getRequest(), req)
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

type SaveArticleConversionRequest = article.SaveConversionCommand

func (a *WailsApp) SaveArticleConversion(req SaveArticleConversionRequest) (int64, error) {
	res, err := article.SaveConversion(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return res, err
}

type DeleteArticleConversionRequest = article.DeleteConversionCommand

func (a *WailsApp) DeleteArticleConversion(req DeleteArticleConversionRequest) error {
	err := article.DeleteConversion(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
}
