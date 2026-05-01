package report

import (
	"errors"
	"fmt"
	"hrvanovicm/magacin/core"
	"hrvanovicm/magacin/internal/article"

	"gorm.io/gorm"
)

type SaveCommand = Report

func Save(r core.Request, cmd SaveCommand) error {
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
			First(&o, cmd.ID).Error; err == nil {
			old = &o
		}
	}

	if err := refreshStockAmounts(r, old, cmd); err != nil {
		return err
	}

	err := r.DB.WithContext(r.Ctx).
		Session(&gorm.Session{FullSaveAssociations: true}).
		Omit("Articles.Article", "Articles.Recipes.RawMaterial").
		Save(&cmd).Error
	if err != nil {
		return err
	}

	return nil
}

func refreshStockAmounts(r core.Request, old *Report, new Report) error {
	if old != nil {
		for _, art := range old.Articles {
			fmt.Println("refreshStockAmounts", art.ArticleID)
			if old.Type == TypeWorkOrder {
				for _, rec := range art.Recipes {
					if err := article.IncreaseStock(r, &rec.RawMaterial, article.IncreaseStockCommand{Amount: art.Amount * rec.Amount}); err != nil {
						return err
					}
				}
			}

			if old.Type == TypeReceipt || old.Type == TypeWorkOrder {
				fmt.Println("decreasing for ", art.Article)
				if err := article.DecreaseStock(r, &art.Article, article.DecreaseStockCommand{Amount: art.Amount}); err != nil {
					return err
				}
			} else if old.Type == TypeShipment {
				if err := article.IncreaseStock(r, &art.Article, article.IncreaseStockCommand{Amount: art.Amount}); err != nil {
					return err
				}
			}
		}
	}

	for _, art := range new.Articles {
		if new.Type == TypeWorkOrder {
			for _, rec := range art.Recipes {
				if err := article.DecreaseStock(r, &rec.RawMaterial, article.DecreaseStockCommand{Amount: art.Amount * rec.Amount}); err != nil {
					return err
				}
			}
		}

		if new.Type == TypeReceipt || new.Type == TypeWorkOrder {
			if err := article.IncreaseStock(r, &art.Article, article.IncreaseStockCommand{Amount: art.Amount}); err != nil {
				return err
			}
		} else if new.Type == TypeShipment {
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

func Delete(r core.Request, cmd DeleteCommand) error {
	var rep Report

	err := r.DB.WithContext(r.Ctx).
		First(&rep, cmd.ID).
		Error

	if err != nil && !errors.Is(err, gorm.ErrRecordNotFound) {
		return err
	}

	return r.DB.Delete(&rep).Error
}
