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
	Username      string  `gorm:"column:username" json:"username" log:"korisničkog imena"`
	PasswordHash  string  `gorm:"column:password_hash" json:"-"`
	ServerAddress *string `gorm:"column:server_address" json:"server_address" log:"adrese servera"`
	Role          *string `gorm:"column:role" json:"role" log:"uloge"`
}

func (Account) TableName() string {
	return "main.accounts"
}
