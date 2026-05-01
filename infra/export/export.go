package export

import (
	"bytes"
	"fmt"

	"github.com/xuri/excelize/v2"
)

type XLBuilder struct {
	file         *excelize.File
	currentSheet string
	currentRow   int
}

func GenerateXL(sheetName string, fillFn func(*XLBuilder)) ([]byte, error) {
	f := excelize.NewFile()
	defer f.Close()

	f.SetSheetName("Sheet1", sheetName)

	b := &XLBuilder{file: f, currentSheet: sheetName, currentRow: 1}

	b.currentRow = 3

	fillFn(b)

	var buf bytes.Buffer
	if err := f.Write(&buf); err != nil {
		return nil, err
	}

	return buf.Bytes(), nil
}

func (b *XLBuilder) WriteRow(val ...any) {
	for i, v := range val {
		cell, _ := excelize.CoordinatesToCellName(i+1, b.currentRow)
		b.file.SetCellValue(b.currentSheet, cell, v)
	}

	b.currentRow++
}

func (b *XLBuilder) WriteSpace() {
	b.currentRow++
}

func (b *XLBuilder) WriteHeader(cols ...any) {
	style, _ := b.file.NewStyle(&excelize.Style{
		Font: &excelize.Font{Bold: true, Color: "FFFFFF"},
		Fill: excelize.Fill{Type: "pattern", Color: []string{"4472C4"}, Pattern: 1},
	})

	start, _ := excelize.CoordinatesToCellName(1, b.currentRow)
	end, _ := excelize.CoordinatesToCellName(len(cols), b.currentRow)
	b.WriteRow(cols...)
	b.file.SetCellStyle(b.currentSheet, start, end, style)
}

func (b *XLBuilder) WriteTitle(text string) {
	style, _ := b.file.NewStyle(&excelize.Style{Font: &excelize.Font{Bold: true, Size: 16}})
	b.file.SetCellValue(b.currentSheet, fmt.Sprintf("A%d", b.currentRow), text)
	b.file.SetCellStyle(b.currentSheet, fmt.Sprintf("A%d", b.currentRow), fmt.Sprintf("E%d", b.currentRow), style)
	b.currentRow += 2
}

func (b *XLBuilder) WriteSection(name string) {
	style, _ := b.file.NewStyle(&excelize.Style{Fill: excelize.Fill{Type: "pattern", Color: []string{"D9E1F2"}, Pattern: 1}, Font: &excelize.Font{Bold: true}})
	b.file.SetCellValue(b.currentSheet, fmt.Sprintf("A%d", b.currentRow), name)
	b.file.SetCellStyle(b.currentSheet, fmt.Sprintf("A%d", b.currentRow), fmt.Sprintf("G%d", b.currentRow), style)
	b.currentRow++
}
