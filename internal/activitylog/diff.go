package activitylog

import (
	"fmt"
	"reflect"
)

func Diff(username string, old, new any) []string {
	var diffs []string

	valOld := reflect.ValueOf(old)
	valNew := reflect.ValueOf(new)

	if valOld.Kind() == reflect.Ptr {
		if valOld.IsNil() {
			valOld = reflect.New(valNew.Type().Elem()).Elem()
		} else {
			valOld = valOld.Elem()
		}
	}
	if valNew.Kind() == reflect.Ptr {
		if valNew.IsNil() {
			return diffs
		}
		valNew = valNew.Elem()
	}

	if valOld.Type() != valNew.Type() || valOld.Kind() != reflect.Struct {
		return diffs
	}

	typ := valOld.Type()

	for i := 0; i < valOld.NumField(); i++ {
		field := typ.Field(i)
		logTag := field.Tag.Get("log")

		if logTag == "" || logTag == "-" {
			continue
		}

		oldVal := valOld.Field(i).Interface()
		newVal := valNew.Field(i).Interface()

		oldStr := formatValue(oldVal)
		newStr := formatValue(newVal)

		if oldStr != newStr {
			diffs = append(diffs, fmt.Sprintf("%s izmijenio %s sa '%s' na '%s'", username, logTag, oldStr, newStr))
		}
	}

	return diffs
}

func formatValue(val any) string {
	if val == nil {
		return ""
	}

	v := reflect.ValueOf(val)
	if v.Kind() == reflect.Ptr {
		if v.IsNil() {
			return ""
		}
		v = v.Elem()
	}

	switch v.Kind() {
	case reflect.Float32, reflect.Float64:
		return fmt.Sprintf("%.2f", v.Float())
	default:
		return fmt.Sprintf("%v", v.Interface())
	}
}
