package activitylog

import (
	"context"
	"fmt"
	"reflect"

	"gorm.io/gorm"
)

type ChangeLogger struct {
	l           *Logger
	ctx         context.Context
	db          *gorm.DB
	subjectID   int64
	subjectType string
}

func (l *Logger) For(ctx context.Context, db *gorm.DB, id int64, subjectType string) *ChangeLogger {
	return &ChangeLogger{l: l, ctx: ctx, db: db, subjectID: id, subjectType: subjectType}
}

func (l *Logger) ForStruct(ctx context.Context, db *gorm.DB, id int64, subject any) *ChangeLogger {
	t := reflect.TypeOf(subject)
	if t.Kind() == reflect.Ptr {
		t = t.Elem()
	}
	subjectType := t.Name()
	return &ChangeLogger{l: l, ctx: ctx, db: db, subjectID: id, subjectType: subjectType}
}

func (cl *ChangeLogger) Log(desc string) {
	cl.l.Log(cl.ctx, cl.db, cl.subjectID, cl.subjectType, desc)
}

func (cl *ChangeLogger) LogField(label, old, new string) {
	if old == new {
		return
	}
	cl.Log(fmt.Sprintf("%s izmijenio %s sa \"%s\" na \"%s\"", cl.l.Username(), label, old, new))
}

func (cl *ChangeLogger) LogCreate() {
	cl.Log(fmt.Sprintf("%s kreirao zapis", cl.l.Username()))
}

func (cl *ChangeLogger) LogDelete() {
	cl.Log(fmt.Sprintf("%s obrisao zapis", cl.l.Username()))
}

func (cl *ChangeLogger) LogDiff(old, new any) {
	diffs := Diff(cl.l.Username(), old, new)
	for _, desc := range diffs {
		cl.Log(desc)
	}
}
