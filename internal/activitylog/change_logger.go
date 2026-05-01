package activitylog

import (
	"context"
	"fmt"

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

func (cl *ChangeLogger) Log(desc string) {
	cl.l.Log(cl.ctx, cl.db, cl.subjectID, cl.subjectType, desc)
}

func (cl *ChangeLogger) LogField(label, old, new string) {
	if old == new {
		return
	}
	cl.Log(fmt.Sprintf("%s promijenio %s sa \"%s\" na \"%s\"", cl.l.Username(), label, old, new))
}
