package article

import (
	"fmt"
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/internal/activitylog"

	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

type SaveCommand = Article

func Save(r app.Request, cmd SaveCommand) (uint, error) {
	var old *Article
	if cmd.ID > 0 {
		var existing Article
		if err := r.DB.WithContext(r.Ctx).First(&existing, cmd.ID).Error; err == nil {
			old = &existing
		}
	}

	if err := r.DB.WithContext(r.Ctx).
		Omit("UnitMeasure", "Recipes").
		Save(&cmd).Error; err != nil {
		return 0, fmt.Errorf("failed to save article: %w", err)
	}

	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, cmd.ID, Article{})
	if old == nil {
		cl.LogCreate()
	} else {
		cl.LogDiff(old, &cmd)
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
	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(cmd.ID), Article{})

	if err := r.DB.WithContext(r.Ctx).Where("article_id = ?", cmd.ID).Delete(&Recipe{}).Error; err != nil {
		return err
	}
	if err := r.DB.WithContext(r.Ctx).Where("article_id = ?", cmd.ID).Delete(&Conversion{}).Error; err != nil {
		return err
	}

	if err := r.DB.WithContext(r.Ctx).Model(&Article{}).
		Where("id = ?", cmd.ID).
		Delete(&Article{}).Error; err != nil {
		return err
	}

	cl.LogDelete()

	return nil
}

type IncreaseStockCommand struct {
	Amount float32
}

func IncreaseStock(r app.Request, arc *Article, cmd IncreaseStockCommand) error {
	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, arc.ID, Article{})
	
	err := r.DB.WithContext(r.Ctx).
		Model(arc).
		Where("id = ?", arc.ID).
		Clauses(clause.Returning{Columns: []clause.Column{{Name: "in_stock_amount"}}}).
		Update("in_stock_amount", gorm.Expr("in_stock_amount + ?", cmd.Amount)).
		Error

	if err == nil {
		cl.Log(fmt.Sprintf("%s promijenio količinu na stanju za +%.2f", r.User.Username, cmd.Amount))
	}
	return err
}

type DecreaseStockCommand struct {
	Amount float32
}

func DecreaseStock(r app.Request, arc *Article, cmd DecreaseStockCommand) error {
	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, arc.ID, Article{})

	err := r.DB.WithContext(r.Ctx).
		Model(arc).
		Where("id = ?", arc.ID).
		Clauses(clause.Returning{Columns: []clause.Column{{Name: "in_stock_amount"}}}).
		Update("in_stock_amount", gorm.Expr("in_stock_amount - ?", cmd.Amount)).
		Error

	if err == nil {
		cl.Log(fmt.Sprintf("%s promijenio količinu na stanju za -%.2f", r.User.Username, cmd.Amount))
	}
	return err
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

type SaveConversionCommand = Conversion

func SaveConversion(r app.Request, cmd SaveConversionCommand) (int64, error) {
	err := r.DB.WithContext(r.Ctx).Save(&cmd).Error
	if err != nil {
		return 0, err
	}
	return cmd.ID, nil
}

type DeleteConversionCommand struct {
	ID int64
}

func DeleteConversion(r app.Request, cmd DeleteConversionCommand) error {
	return r.DB.WithContext(r.Ctx).Delete(&Conversion{}, cmd.ID).Error
}
