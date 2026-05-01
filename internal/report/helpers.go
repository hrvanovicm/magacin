package report

import (
	"fmt"
	"regexp"
	"strconv"
	"strings"
	"time"
)

func incrementCode(lastCode string) (string, error) {
	year := time.Now().Year() % 100
	if lastCode == "" {
		return fmt.Sprintf("%02d/1", year), nil
	}

	re := regexp.MustCompile(`\D+`)
	parts := re.Split(lastCode, -1)
	if len(parts) == 0 {
		return fmt.Sprintf("%02d/1", year), nil
	}

	lastNumStr := parts[len(parts)-1]
	if lastNumStr == "" {
		return fmt.Sprintf("%02d/1", year), nil
	}
	lastNum, err := strconv.Atoi(lastNumStr)
	if err != nil {
		return fmt.Sprintf("%02d/1", year), nil
	}
	lastNum++

	separators := re.FindAllString(lastCode, -1)
	var newCode strings.Builder
	for i := 0; i < len(parts)-1; i++ {
		newCode.WriteString(parts[i])
		if i < len(separators) {
			newCode.WriteString(separators[i])
		}
	}
	newCode.WriteString(fmt.Sprintf("%0*d", len(lastNumStr), lastNum))

	return newCode.String(), nil
}

func derefStr(s *string) string {
	if s == nil {
		return ""
	}
	return *s
}
