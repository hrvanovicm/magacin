package dbmanager

type PagedResult[T any] struct {
	Content []T   `json:"content"`
	Total   int64 `json:"total"`
	Page    int   `json:"page"`
	Limit   int   `json:"limit"`
}

func NewDefaultPagedResult[T any]() PagedResult[T] {
	return PagedResult[T]{
		Content: []T{},
		Total:   0,
		Page:    1,
		Limit:   30,
	}
}

type Paged struct {
	Page  int `json:"page"`
	Limit int `json:"limit"`
}

func (pg Paged) Offset() int {
	return (pg.Page - 1) * pg.Limit
}
