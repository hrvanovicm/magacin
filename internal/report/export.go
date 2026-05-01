package report

import (
	"fmt"
	"hrvanovicm/magacin/core"
	"hrvanovicm/magacin/infra/export"
)

const (
	FormatXLSX = "xlsx"
)

type ListExportQuery struct {
	ListQuery
	Format string
}

func ListExport(r core.Request, qry ListExportQuery) ([]byte, error) {
	reports, err := List(r, qry.ListQuery)
	if err != nil {
		return nil, fmt.Errorf("export: list failed: %w", err)
	}

	return export.GenerateXL("Izvjestaji", func(b *export.XLBuilder) {
		b.WriteHeader("Rb.", "Šifra", "Tip", "Firma", "Datum", "Lokacija", "Potpisao")
		for i, rep := range reports {
			b.WriteRow(i+1, deref(rep.Code), getLabel(rep), getCompany(rep), deref(rep.Date), deref(rep.PlaceOfPublish), deref(rep.SignedByName))
		}
	})
}

type GetExportQuery struct {
	GetQuery
	Format string
}

func GetExport(r core.Request, qry GetQuery) ([]byte, error) {
	rep, err := Get(r, qry)
	if err != nil {
		return nil, err
	}

	label := "PRIMKA"
	if rep.Type == TypeShipment {
		label = "OTPREMNICA"
	}

	return export.GenerateXL(label, func(b *export.XLBuilder) {
		b.WriteTitle(label)
		b.WriteRow("Broj:", deref(rep.Code))
		b.WriteRow("Datum:", deref(rep.Date))
		b.WriteRow("Lokacija:", deref(rep.PlaceOfPublish))

		b.WriteSection("ARTIKLI")
		b.WriteHeader("Rb.", "Artikal", "Šifra", "Količina", "Mj. jedinica")
		for i, a := range rep.Articles {
			b.WriteRow(i+1, a.Article.Name, deref(a.Article.Code), a.Amount, a.Article.UnitMeasure.Name)
		}
	})
}

func ExportWorkOrderXLSX(r core.Request, qry GetQuery) ([]byte, error) {
	rep, err := Get(r, qry)
	if err != nil {
		return nil, err
	}

	return export.GenerateXL("Radni nalog", func(b *export.XLBuilder) {
		b.WriteTitle("RADNI NALOG")
		b.WriteRow("Broj:", deref(rep.Code))
		b.WriteRow("Datum:", deref(rep.Date))
		b.WriteRow("Lokacija:", deref(rep.PlaceOfPublish))

		b.WriteSection("PLAN PROIZVODNJE")
		b.WriteHeader("Rb.", "Artikal", "Šifra", "Količina", "Mj. jedinica")
		for i, a := range rep.Articles {
			b.WriteRow(i+1, a.Article.Name, deref(a.Article.Code), a.Amount, a.Article.UnitMeasure.Name)
		}

		b.WriteSpace()
		b.WriteSection("UTROŠAK SIROVINA")
		b.WriteHeader("Rb.", "Sirovina", "Šifra", "Artikal", "Normativ", "Utrošeno", "Mj. jedinica")
		idx := 1
		for _, a := range rep.Articles {
			for _, recipe := range a.Recipes {
				b.WriteRow(idx, recipe.RawMaterial.Name, deref(recipe.RawMaterial.Code), a.Article.Name, recipe.Amount, recipe.Amount*a.Amount, recipe.RawMaterial.UnitMeasure.Name)
				idx++
			}
		}
	})
}

func deref(s *string) string {
	if s == nil {
		return ""
	}
	return *s
}

func getLabel(r Report) string {
	if r.Type == TypeShipment {
		return "Otpremnica"
	}
	return "Prijem"
}

func getCompany(r Report) string {
	if r.Type == TypeReceipt {
		return r.Receipt.SupplierCompany.Name
	}
	return r.Shipment.ReceiptCompany.Name
}
