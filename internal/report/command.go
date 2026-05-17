package report

import (
	"errors"
	"fmt"
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/internal/activitylog"
	"hrvanovicm/magacin/internal/article"

	"hrvanovicm/magacin/internal/company"

	"gorm.io/gorm"
)

type SaveCommand = Report

func Save(r app.Request, cmd SaveCommand) (uint, error) {
	isNew := cmd.ID == 0

	var old *Report
	if !isNew {
		var o Report
		if err := r.DB.WithContext(r.Ctx).
			Preload("Receipt").
			Preload("Receipt.SupplierCompany").
			Preload("Shipment").
			Preload("Shipment.ReceiptCompany").
			Preload("Articles").
			Preload("Articles.Article").
			Preload("Articles.Recipes").
			Preload("Articles.Recipes.RawMaterial").
			First(&o, cmd.ID).Error; err == nil {
			old = &o
		}
	}

	if err := ResetStockAmounts(r, old); err != nil {
		return 0, err
	}
	if err := UpdateStockAmounts(r, cmd); err != nil {
		return 0, err
	}

	if cmd.Type == TypeReceipt && cmd.Receipt.SupplierCompany.Name != "" {
		ensureCompanyExists(r.DB, cmd.Receipt.SupplierCompany.Name)
		cmd.Receipt.SupplierCompanyName = &cmd.Receipt.SupplierCompany.Name
	} else if cmd.Type == TypeShipment && cmd.Shipment.ReceiptCompany.Name != "" {
		ensureCompanyExists(r.DB, cmd.Shipment.ReceiptCompany.Name)
		cmd.Shipment.ReceiptCompanyName = &cmd.Shipment.ReceiptCompany.Name
	}

	if !isNew {
		if err := r.DB.WithContext(r.Ctx).Where("report_id = ?", cmd.ID).Delete(&Receipt{}).Error; err != nil {
			return 0, err
		}
		if err := r.DB.WithContext(r.Ctx).Where("report_id = ?", cmd.ID).Delete(&Shipment{}).Error; err != nil {
			return 0, err
		}
		if err := r.DB.WithContext(r.Ctx).Where("report_id = ?", cmd.ID).Delete(&HasRecipe{}).Error; err != nil {
			return 0, err
		}
		if err := r.DB.WithContext(r.Ctx).Where("report_id = ?", cmd.ID).Delete(&HasArticle{}).Error; err != nil {
			return 0, err
		}
	}

	err := r.DB.WithContext(r.Ctx).
		Session(&gorm.Session{FullSaveAssociations: true}).
		Omit("Articles.Article", "Articles.Recipes.RawMaterial", "Receipt.SupplierCompany", "Shipment.ReceiptCompany").
		Save(&cmd).Error
	if err != nil {
		return 0, err
	}

	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, cmd.ID, Report{})
	if old == nil {
		cl.LogCreate()
	} else {
		cl.LogDiff(old, &cmd)
		cl.LogDiff(old.Receipt, cmd.Receipt)
		cl.LogDiff(old.Shipment, cmd.Shipment)
	}
	logArticleChanges(cl, r.User.Username, old, &cmd)

	return uint(cmd.ID), nil
}

func ResetStockAmounts(r app.Request, rep *Report) error {
	if rep == nil {
		return nil
	}
	for _, art := range rep.Articles {
		if rep.Type == TypeWorkOrder {
			for _, rec := range art.Recipes {
				if err := article.IncreaseStock(r, &rec.RawMaterial, article.IncreaseStockCommand{Amount: rec.Amount}); err != nil {
					return err
				}
			}
		}

		if rep.Type == TypeReceipt || rep.Type == TypeWorkOrder {
			if err := article.DecreaseStock(r, &art.Article, article.DecreaseStockCommand{Amount: art.Amount}); err != nil {
				return err
			}
		} else if rep.Type == TypeShipment {
			if err := article.IncreaseStock(r, &art.Article, article.IncreaseStockCommand{Amount: art.Amount}); err != nil {
				return err
			}
		}
	}
	return nil
}

func UpdateStockAmounts(r app.Request, rep Report) error {
	for _, art := range rep.Articles {
		if rep.Type == TypeWorkOrder {
			for _, rec := range art.Recipes {
				if err := article.DecreaseStock(r, &rec.RawMaterial, article.DecreaseStockCommand{Amount: rec.Amount}); err != nil {
					return err
				}
			}
		}

		if rep.Type == TypeReceipt || rep.Type == TypeWorkOrder {
			if err := article.IncreaseStock(r, &art.Article, article.IncreaseStockCommand{Amount: art.Amount}); err != nil {
				return err
			}
		} else if rep.Type == TypeShipment {
			if err := article.DecreaseStock(r, &art.Article, article.DecreaseStockCommand{Amount: art.Amount}); err != nil {
				return err
			}
		}
	}
	return nil
}

type DeleteCommand struct {
	ID uint
}

func Delete(r app.Request, cmd DeleteCommand) error {
	var rep Report

	err := r.DB.WithContext(r.Ctx).
		Preload("Articles").
		Preload("Articles.Article").
		Preload("Articles.Recipes").
		Preload("Articles.Recipes.RawMaterial").
		First(&rep, cmd.ID).
		Error

	if err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			return nil
		}
		return err
	}

	if err := ResetStockAmounts(r, &rep); err != nil {
		return err
	}

	if err := r.DB.WithContext(r.Ctx).Where("report_id = ?", cmd.ID).Delete(&Receipt{}).Error; err != nil {
		return err
	}
	if err := r.DB.WithContext(r.Ctx).Where("report_id = ?", cmd.ID).Delete(&Shipment{}).Error; err != nil {
		return err
	}
	if err := r.DB.WithContext(r.Ctx).Where("report_id = ?", cmd.ID).Delete(&HasRecipe{}).Error; err != nil {
		return err
	}
	if err := r.DB.WithContext(r.Ctx).Where("report_id = ?", cmd.ID).Delete(&HasArticle{}).Error; err != nil {
		return err
	}

	err = r.DB.WithContext(r.Ctx).Delete(&rep).Error
	if err == nil {
		cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(cmd.ID), Report{})
		cl.LogDelete()
	}

	return err
}

func logArticleChanges(cl *activitylog.ChangeLogger, username string, old *Report, cmd *Report) {
	if old == nil {
		for _, a := range cmd.Articles {
			cl.Log(fmt.Sprintf("%s dodao artikal '%s' (količina: %v)", username, a.Article.Name, a.Amount))
			for _, rec := range a.Recipes {
				cl.Log(fmt.Sprintf("%s dodao sirovinu '%s' (količina: %v) za artikal '%s'", username, rec.RawMaterial.Name, rec.Amount, a.Article.Name))
			}
		}
		return
	}

	oldMap := make(map[int]HasArticle)
	for _, a := range old.Articles {
		oldMap[a.ArticleID] = a
	}

	for _, a := range cmd.Articles {
		if oldArt, exists := oldMap[a.ArticleID]; exists {
			if oldArt.Amount != a.Amount {
				cl.Log(fmt.Sprintf("%s izmijenio količinu artikla '%s' sa %v na %v", username, a.Article.Name, oldArt.Amount, a.Amount))
			}

			oldRecMap := make(map[int]HasRecipe)
			for _, rec := range oldArt.Recipes {
				oldRecMap[rec.RawMaterialID] = rec
			}
			for _, rec := range a.Recipes {
				if oldRec, existsRec := oldRecMap[rec.RawMaterialID]; existsRec {
					if oldRec.Amount != rec.Amount {
						cl.Log(fmt.Sprintf("%s izmijenio količinu sirovine '%s' (artikal: '%s') sa %v na %v", username, rec.RawMaterial.Name, a.Article.Name, oldRec.Amount, rec.Amount))
					}
					delete(oldRecMap, rec.RawMaterialID)
				} else {
					cl.Log(fmt.Sprintf("%s dodao sirovinu '%s' (količina: %v) za artikal '%s'", username, rec.RawMaterial.Name, rec.Amount, a.Article.Name))
				}
			}
			for _, rec := range oldRecMap {
				cl.Log(fmt.Sprintf("%s uklonio sirovinu '%s' iz artikla '%s'", username, rec.RawMaterial.Name, a.Article.Name))
			}

			delete(oldMap, a.ArticleID)
		} else {
			cl.Log(fmt.Sprintf("%s dodao artikal '%s' (količina: %v)", username, a.Article.Name, a.Amount))
			for _, rec := range a.Recipes {
				cl.Log(fmt.Sprintf("%s dodao sirovinu '%s' (količina: %v) za artikal '%s'", username, rec.RawMaterial.Name, rec.Amount, a.Article.Name))
			}
		}
	}

	for _, a := range oldMap {
		cl.Log(fmt.Sprintf("%s uklonio artikal '%s'", username, a.Article.Name))
	}
}

func ensureCompanyExists(db *gorm.DB, name string) {
	if name == "" {
		return
	}
	var c company.Company
	if err := db.Where("name = ?", name).First(&c).Error; err != nil {
		if errors.Is(err, gorm.ErrRecordNotFound) {
			db.Create(&company.Company{Name: name})
		}
	}
}
