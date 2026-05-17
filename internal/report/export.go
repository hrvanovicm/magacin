package report

import (
	"fmt"
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/infra/export"
	"hrvanovicm/magacin/internal/server"
	"hrvanovicm/magacin/internal/um"
	"time"
)

const (
	FormatXLSX = "xlsx"
)

type ListExportQuery struct {
	ListQuery
	Format string
}

func ListExport(r app.Request, qry ListExportQuery) ([]byte, error) {
	reports, err := List(r, qry.ListQuery)
	if err != nil {
		return nil, fmt.Errorf("export: list failed: %w", err)
	}

	defaultName := ""
	if cfg, err := server.GetLocalConfig(r); err == nil && cfg.CompanyName != "" {
		defaultName = cfg.Name
	}

	return export.GenerateXL("Izvjestaji", func(b *export.XLBuilder) {
		b.WriteTitle("Izvještaji")
		b.WriteRow(defaultName)
		b.WriteRow(fmt.Sprintf("Datum: %s", time.Now().Format("2006-01-02")))
		b.WriteHeader("Rb.", "Šifra", "Tip", "Firma", "Datum", "Lokacija", "Potpisao")
		for i, rep := range reports {
			b.WriteRow(i+1, deref(rep.Code), getLabel(rep), getCompany(rep), derefDate(rep.Date), deref(rep.PlaceOfPublish), deref(rep.SignedByName))
		}
	})
}

type GetExportQuery struct {
	GetQuery
	Format string
}

func GetExport(r app.Request, qry GetQuery) ([]byte, error) {
	rep, err := Get(r, qry)
	if err != nil {
		return nil, err
	}

	defaultName := ""
	if cfg, err := server.GetLocalConfig(r); err == nil && cfg.CompanyName != "" {
		defaultName = cfg.Name
	}

	label := "PRIMKA"
	if rep.Type == TypeShipment {
		label = "OTPREMNICA"
	}

	return export.GenerateXL(label, func(b *export.XLBuilder) {
		b.WriteTitle(label)
		b.WriteRow(defaultName)
		b.WriteRow("Broj:", deref(rep.Code))
		b.WriteRow("Datum:", derefDate(rep.Date))
		b.WriteRow("Lokacija:", deref(rep.PlaceOfPublish))

		b.WriteSection("ARTIKLI")
		b.WriteHeader("Rb.", "Artikal", "Šifra", "Količina", "Mj. jedinica")
		for i, a := range rep.Articles {
			b.WriteRow(i+1, a.Article.Name, deref(a.Article.Code), a.Amount, getUmName(a.Article.UnitMeasure))
		}
	})
}

func ExportWorkOrderXLSX(r app.Request, qry GetQuery) ([]byte, error) {
	rep, err := Get(r, qry)
	if err != nil {
		return nil, err
	}

	return export.GenerateXL("Radni nalog", func(b *export.XLBuilder) {
		b.WriteTitle("RADNI NALOG")
		b.WriteRow("Broj:", deref(rep.Code))
		b.WriteRow("Datum:", derefDate(rep.Date))
		b.WriteRow("Lokacija:", deref(rep.PlaceOfPublish))

		b.WriteSection("PLAN PROIZVODNJE")
		b.WriteHeader("Rb.", "Artikal", "Šifra", "Količina", "Mj. jedinica")
		for i, a := range rep.Articles {
			b.WriteRow(i+1, a.Article.Name, deref(a.Article.Code), a.Amount, getUmName(a.Article.UnitMeasure))
		}

		b.WriteSpace()
		b.WriteSection("UTROŠAK SIROVINA")
		b.WriteHeader("Rb.", "Sirovina", "Šifra", "Artikal", "Normativ", "Utrošeno", "Mj. jedinica")
		idx := 1
		for _, a := range rep.Articles {
			for _, recipe := range a.Recipes {
				b.WriteRow(idx, recipe.RawMaterial.Name, deref(recipe.RawMaterial.Code), a.Article.Name, recipe.Amount, recipe.Amount*a.Amount, getUmName(recipe.RawMaterial.UnitMeasure))
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

func derefDate(s *string) string {
	if s == nil {
		return ""
	}

	t, err := time.Parse(time.RFC3339, *s)
	if err != nil {
		return ""
	}

	return t.Format("02.01.2006.")
}

func getLabel(r Report) string {
	if r.Type == TypeShipment {
		return "Otpremnica"
	}
	return "Prijemnica"
}

func getCompany(r Report) string {
	if r.Type == TypeReceipt {
		return r.Receipt.SupplierCompany.Name
	}
	return r.Shipment.ReceiptCompany.Name
}

func getUmName(um *um.UnitMeasure) string {
	if um == nil {
		return ""
	}
	return um.Name
}
