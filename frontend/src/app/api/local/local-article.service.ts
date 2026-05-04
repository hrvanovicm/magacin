import * as wailsApp from '../../../../wailsjs/go/app/WailsApp';
import {
  ActivityLog,
  ActivityLogGetRequest,
  Article,
  ArticleDeleteRequest,
  ArticleExportRequest,
  ArticleGetRequest,
  ArticleListPagedRequest,
  ArticleListRequest,
  ArticleService,
  ArticleSaveRequest,
  Paged, ArticleAnalyticsRequest, ArticleAnalyticsResult,
} from '../index';

export class LocalArticleService implements ArticleService {
  async list(req: ArticleListRequest): Promise<Article[]> {
    return await wailsApp.ListArticles(req);
  }

  async listPaged(req: ArticleListPagedRequest): Promise<Paged<Article>> {
    return await wailsApp.ListArticlesPaged(req) as Paged<Article>;
  }

  async get(req: ArticleGetRequest): Promise<Article | undefined> {
    return await wailsApp.GetArticle(req);
  }

  async save(req: ArticleSaveRequest): Promise<number> {
    return await wailsApp.SaveArticle(req);
  }

  async delete(req: ArticleDeleteRequest): Promise<void> {
    return await wailsApp.DeleteArticle(req);
  }

  async export(req: Partial<ArticleExportRequest>): Promise<void> {
    return await wailsApp.ExportArticles(req as ArticleExportRequest);
  }

  async getLogs(req: ActivityLogGetRequest): Promise<ActivityLog[]> {
    return await wailsApp.GetArticleLogs(req as any) as ActivityLog[];
  }

  async getAnalytics(req: ArticleAnalyticsRequest): Promise<ArticleAnalyticsResult[]> {
    return await wailsApp.GetArticleAnalytics(req) as ArticleAnalyticsResult[];
  }
}
