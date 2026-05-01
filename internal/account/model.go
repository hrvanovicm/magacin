package account

const (
	RoleAdmin     = "ADMIN"
	RoleModerator = "MODERATOR"
	RoleGuest     = "GUEST"
)

type Specification struct {
	Search  *string
	OrderBy *string
}

type Account struct {
	ID            uint    `gorm:"column:id;primaryKey" json:"id"`
	Username      string  `gorm:"column:username" json:"username"`
	PasswordHash  string  `gorm:"column:password_hash" json:"-"`
	ServerAddress *string `gorm:"column:server_address" json:"server_address"`
	Role          *string `gorm:"column:role" json:"role"`
}

func (Account) TableName() string {
	return "main.accounts"
}
