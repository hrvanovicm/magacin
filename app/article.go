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

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

type ListArticlesRequest = article.ListQuery

func (a *WailsApp) ListArticles(req ListArticlesRequest) ([]article.Article, error) {
	articles, err := article.List(a.getRequest(), req)

	if err != nil {
		a.report(err)
		return articles, err
	}

	return articles, nil
}

type ListArticlesPagedRequest = article.ListPagedQuery

func (a *WailsApp) ListArticlesPaged(req ListArticlesPagedRequest) (paged.PagedResult[article.Article], error) {
	articles, err := article.ListPaged(a.getRequest(), req)

	if err != nil {
		a.report(err)
		return articles, err
	}

	return articles, nil
}

type GetArticleRequest = article.GetQuery

func (a *WailsApp) GetArticle(req GetArticleRequest) (*article.Article, error) {
	art, err := article.Get(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return art, err
	}

	return art, nil
}

type SaveArticleRequest = article.SaveCommand

func (a *WailsApp) SaveArticle(req SaveArticleRequest) (uint, error) {
	return article.Save(a.getRequest(), req);
}

type DeleteArticleRequest = article.DeleteCommand

func (a *WailsApp) DeleteArticle(req DeleteArticleRequest) error {
	if err := article.Delete(a.getRequest(), req); err != nil {
		a.report(err)
		return err
	}

	return nil
}

type GetArticleLogsRequest = article.GetLogsQuery

func (a *WailsApp) GetArticleLogs(req GetArticleLogsRequest) ([]activitylog.Entry, error) {
	logs, err := article.GetLogs(a.getRequest(), req)
	if err != nil {
		a.report(err)
		return nil, err
	}
	return logs, nil
}

type GetArticleAnalyticsRequest = report.GetAnalyticsByArticleQuery

func (a *WailsApp) GetArticleAnalytics(req GetArticleAnalyticsRequest) ([]report.ArticleAnalyticsResult, error) {
	logs, err := report.GetAnalyticsByArticle(a.getRequest(), req)

	if err != nil {
		a.report(err)
		return nil, err
	}

	return logs, nil
}

func (a *WailsApp) ExportArticles(req ListArticlesRequest) error {
	path, err := runtime.SaveFileDialog(a.getRequest().Ctx, runtime.SaveDialogOptions{
		Title:           "Izvoz robe",
		DefaultFilename: "roba.xlsx",
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
		data, err = article.GetExport(a.getRequest(), req)
	default:
		if !strings.HasSuffix(path, ".xlsx") {
			path += ".xlsx"
		}
		data, err = article.GetExport(a.getRequest(), req)
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
