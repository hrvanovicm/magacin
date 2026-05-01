package server

type LocalConfig struct {
	Name        string `gorm:"column:name" json:"name"`
	IsPublic    bool   `gorm:"column:is_public" json:"is_public"`
	CompanyName string `gorm:"column:company_name" json:"company_name"`
}

func (LocalConfig) TableName() string {
	return "main.server_config"
}

type Server struct {
	ID               uint    `gorm:"column:id" json:"id"`
	Name             string  `gorm:"column:name" json:"name"`
	Address          string  `gorm:"column:address" json:"address"`
	LastUsedUsername *string `gorm:"column:last_used_username" json:"lastUsedUsername"`
}

func (Server) TableName() string {
	return "servers"
}
