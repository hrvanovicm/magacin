package article

import (
	"fmt"
	"hrvanovicm/magacin/infra/app"

	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

type SaveCommand = Article

func Save(r app.Request, cmd SaveCommand) (uint, error) {
	if err := r.DB.WithContext(r.Ctx).
		Omit("UnitMeasure", "Recipes").
		Save(&cmd).Error; err != nil {
		return 0, fmt.Errorf("failed to save article: %w", err)
	}

	if err := r.DB.WithContext(r.Ctx).
		Where("article_id = ?", cmd.ID).
		Delete(&Recipe{}).Error; err != nil {
		return 0, fmt.Errorf("failed to delete recipes: %w", err)
	}

	for _, rec := range cmd.Recipes {
		rawMaterialID := rec.RawMaterialID
		if rawMaterialID == 0 {
			rawMaterialID = rec.RawMaterial.ID
		}
		recipe := Recipe{
			ArticleID:     cmd.ID,
			RawMaterialID: rawMaterialID,
			Amount:        rec.Amount,
		}
		if err := r.DB.WithContext(r.Ctx).Omit("RawMaterial").Create(&recipe).Error; err != nil {
			return 0, fmt.Errorf("failed to save recipe: %w", err)
		}
	}

	return uint(cmd.ID), nil
}

type DeleteCommand struct {
	ID uint
}

func Delete(r app.Request, cmd DeleteCommand) error {
	if err := r.DB.WithContext(r.Ctx).Model(&Article{}).
		Where("id = ?", cmd.ID).
		Delete(&Article{}).Error; err != nil {
		return err
	}

	return nil
}

type IncreaseStockCommand struct {
	Amount float32
}

func IncreaseStock(r app.Request, arc *Article, cmd IncreaseStockCommand) error {
	return r.DB.WithContext(r.Ctx).
		Model(arc).
		Where("id = ?", arc.ID).
		Clauses(clause.Returning{Columns: []clause.Column{{Name: "in_stock_amount"}}}).
		Update("in_stock_amount", gorm.Expr("in_stock_amount + ?", cmd.Amount)).
		Error
}

type DecreaseStockCommand struct {
	Amount float32
}

func DecreaseStock(r app.Request, arc *Article, cmd DecreaseStockCommand) error {
	return r.DB.WithContext(r.Ctx).
		Model(arc).
		Where("id = ?", arc.ID).
		Clauses(clause.Returning{Columns: []clause.Column{{Name: "in_stock_amount"}}}).
		Update("in_stock_amount", gorm.Expr("in_stock_amount - ?", cmd.Amount)).
		Error
}

type ExportCommand = ListQuery

func categoryLabel(category string) string {
	switch category {
	case CategoryProduct:
		return "Proizvod"
	case CategoryCommercial:
		return "Komercijala"
	case CategoryRawMaterial:
		return "Sirovine"
	default:
		return category
	}
}
