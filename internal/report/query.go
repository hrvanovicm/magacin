package report

import (
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/infra/paged"
	"hrvanovicm/magacin/internal/article"
	"time"

	"gorm.io/gorm"

	"hrvanovicm/magacin/internal/activitylog"
)

var ValidListTypes = []Type{TypeReceipt, TypeShipment, TypeWorkOrder}

func ListPublishLocations(r app.Request) ([]string, error) {
	var locations = make([]string, 0)

	err := r.DB.WithContext(r.Ctx).
		Model(&Report{}).
		Where("signed_at_location IS NOT NULL").
		Distinct().
		Pluck("signed_at_location", &locations).
		Error

	return locations, err
}

func ListSignUsers(r app.Request) ([]string, error) {
	var users = make([]string, 0)

	err := r.DB.WithContext(r.Ctx).
		Model(&Report{}).
		Where("signed_by IS NOT NULL").
		Distinct().
		Pluck("signed_by", &users).
		Error

	return users, err
}

func GetNextReportCodeForType(r app.Request, reportType Type) (string, error) {
	var lastCode string

	err := r.DB.WithContext(r.Ctx).
		Model(&Report{}).
		Where("type = ?", reportType).
		Select("COALESCE(MAX(code), '')").
		Row().
		Scan(&lastCode)

	if err != nil {
		return "", err
	}

	return incrementCode(lastCode)
}

type ListQuery struct {
	Search      *string `json:"search"`
	OrderBy     *string `json:"order_by"`
	Company     *string `json:"company"`
	DateFrom    *string `json:"date_from"`
	DateTo      *string `json:"date_to"`
	Location    *string `json:"location"`
	SignedBy    *string `json:"signed_by"`
	Types       []Type  `json:"types"`
	ArticleName *string `json:"article_name"`
}

func List(r app.Request, qry ListQuery) ([]Report, error) {
	query := r.DB.WithContext(r.Ctx)

	spec := Specification{
		Search:      qry.Search,
		OrderBy:     qry.OrderBy,
		Company:     qry.Company,
		DateFrom:    qry.DateFrom,
		DateTo:      qry.DateTo,
		Location:    qry.Location,
		SignedBy:    qry.SignedBy,
		Types:       qry.Types,
		ArticleName: qry.ArticleName,
	}

	executeFilter(query, spec)

	var reports = make([]Report, 0)

	err := query.
		Preload("Receipt").
		Preload("Receipt.SupplierCompany").
		Preload("Shipment").
		Preload("Shipment.ReceiptCompany").
		Preload("Articles.Article.UnitMeasure").
		Preload("Articles.Recipes.RawMaterial.UnitMeasure").
		Find(&reports).
		Error

	return reports, err
}

type ListPagedQuery struct {
	ListQuery
	paged.Paged
}

func ListPaged(r app.Request, qry ListPagedQuery) (paged.PagedResult[Report], error) {
	query := r.DB.WithContext(r.Ctx).Model(&Report{})

	spec := Specification{
		Search:      qry.Search,
		OrderBy:     qry.OrderBy,
		Company:     qry.Company,
		DateFrom:    qry.DateFrom,
		DateTo:      qry.DateTo,
		Location:    qry.Location,
		SignedBy:    qry.SignedBy,
		Types:       qry.Types,
		ArticleName: qry.ArticleName,
	}

	executeFilter(query, spec)

	var total int64
	if err := query.Count(&total).Error; err != nil {
		return paged.NewDefaultPagedResult[Report](), err
	}

	var reports = make([]Report, 0)

	err := query.
		Preload("Receipt").
		Preload("Receipt.SupplierCompany").
		Preload("Shipment").
		Preload("Shipment.ReceiptCompany").
		Preload("Articles.Article.UnitMeasure").
		Preload("Articles.Recipes.RawMaterial.UnitMeasure").
		Limit(qry.Limit).
		Offset(qry.Paged.Offset()).
		Find(&reports).Error

	if err != nil {
		return paged.NewDefaultPagedResult[Report](), err
	}

	res := paged.PagedResult[Report]{
		Content: reports,
		Total:   total,
		Page:    qry.Page,
		Limit:   qry.Limit,
	}

	return res, nil
}

type GetQuery struct {
	ID uint
}

func Get(r app.Request, qry GetQuery) (*Report, error) {
	var acc Report

	err := r.DB.WithContext(r.Ctx).
		Preload("Receipt").
		Preload("Receipt.SupplierCompany").
		Preload("Shipment").
		Preload("Shipment.ReceiptCompany").
		Preload("Articles.Article.UnitMeasure").
		Preload("Articles.Recipes.RawMaterial.UnitMeasure").
		First(&acc, qry.ID).
		Error

	if err != nil {
		return nil, err
	}

	return &acc, nil
}

type ArticleAnalyticsResult struct {
	Key   string  `json:"key"`   // npr: "2023-05"
	Label string  `json:"label"` // npr: "May"
	In    float32 `json:"in"`
	Out   float32 `json:"out"`
}

type GetAnalyticsByArticleQuery struct {
	ArticleID uint
}

func GetAnalyticsByArticle(r app.Request, qry GetAnalyticsByArticleQuery) ([]ArticleAnalyticsResult, error) {
	now := time.Now()
	months := make([]ArticleAnalyticsResult, 12)
	byKey := make(map[string]*ArticleAnalyticsResult)

	for i := 0; i < 12; i++ {
		d := now.AddDate(0, -(11 - i), 0)
		key := d.Format("2006-01")

		entry := ArticleAnalyticsResult{
			Key:   key,
			Label: d.Month().String(),
			In:    0,
			Out:   0,
		}
		months[i] = entry
		byKey[key] = &months[i]
	}

	art, err := article.Get(r, article.GetQuery{ID: qry.ArticleID})
	if err != nil {
		return []ArticleAnalyticsResult{}, err
	}

	reports, err := List(r, ListQuery{
		ArticleName: &art.Name,
	})
	if err != nil {
		return []ArticleAnalyticsResult{}, err
	}

	for _, rep := range reports {
		if rep.Date == nil || len(*rep.Date) < 7 {
			continue
		}

		key := (*rep.Date)[:7]
		entry, exists := byKey[key]
		if !exists {
			continue
		}

		var inAmount float32
		var outAmount float32
		for _, ha := range rep.Articles {
			if uint(ha.ArticleID) == qry.ArticleID {
				switch rep.Type {
				case TypeReceipt, TypeWorkOrder:
					inAmount += ha.Amount
				case TypeShipment:
					outAmount += ha.Amount
				}
			}

			if rep.Type == TypeWorkOrder {
				for _, rec := range ha.Recipes {
					if uint(rec.RawMaterialID) == qry.ArticleID {
						outAmount += rec.Amount
					}
				}
			}
		}

		entry.In += inAmount
		entry.Out += outAmount
	}

	return months, nil
}

type GetLogsQuery struct {
	ID int64
}

func GetLogs(r app.Request, qry GetLogsQuery) ([]activitylog.Entry, error) {
	return activitylog.GetLogs(r, activitylog.GetLogsQuery{
		SubjectID:   qry.ID,
		SubjectType: activitylog.SubjectReport,
	})
}

func executeFilter(query *gorm.DB, spec Specification) {
	if spec.Search != nil && *spec.Search != "" {
		query = query.Where("code LIKE ?", "%"+*spec.Search+"%")
	}
	if spec.Company != nil && *spec.Company != "" {
		company := "%" + *spec.Company + "%"
		query = query.Where(
			"id IN (SELECT report_id FROM main.receipts WHERE supplier_company_name LIKE ?)"+
				" OR id IN (SELECT report_id FROM main.shipments WHERE receipt_company_name LIKE ?)",
			company, company,
		)
	}
	if spec.DateFrom != nil && *spec.DateFrom != "" {
		query = query.Where("signed_at >= ?", *spec.DateFrom)
	}
	if spec.DateTo != nil && *spec.DateTo != "" {
		query = query.Where("signed_at <= ?", *spec.DateTo)
	}
	if spec.Location != nil && *spec.Location != "" {
		query = query.Where("signed_at_location LIKE ?", "%"+*spec.Location+"%")
	}
	if spec.SignedBy != nil && *spec.SignedBy != "" {
		query = query.Where("signed_by LIKE ?", "%"+*spec.SignedBy+"%")
	}
	if len(spec.Types) > 0 {
		query = query.Where("type IN ?", spec.Types)
	}
	if spec.ArticleName != nil && *spec.ArticleName != "" {
		query = query.Where("id IN (SELECT report_id FROM main.report_has_articles rha JOIN main.articles a ON a.id = rha.article_id WHERE a.name LIKE ?)", "%"+*spec.ArticleName+"%")
	}
	if spec.OrderBy != nil && *spec.OrderBy != "" {
		query = query.Order(*spec.OrderBy)
	}
}
