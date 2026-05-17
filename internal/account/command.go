package account

import (
	"fmt"
	"hrvanovicm/magacin/infra/app"
	"hrvanovicm/magacin/internal/activitylog"
	"time"
)

type SignInCommand struct {
	Username string `json:"username"`
	Password string `json:"password"`
}

type SignInResult struct {
	Account        Account
	Token          string
	TokenExpiresAt time.Duration
}

func SignIn(r app.Request, cmd SignInCommand) (*SignInResult, error) {
	var acc Account

	err := r.DB.WithContext(r.Ctx).
		Where("username = ?", cmd.Username).
		First(&acc).
		Error

	if err != nil {
		return &SignInResult{}, err
	}

	acc.ServerAddress = &r.ServerAddress
	if err := r.DB.WithContext(r.Ctx).Save(&acc).Error; err != nil {
		return &SignInResult{}, err
	}

	passValid := ValidatePassword(acc.PasswordHash, cmd.Password)
	if !passValid {
		return &SignInResult{}, fmt.Errorf("wrong credentials")
	}

	claims := JWTClaims{
		UserID:   acc.ID,
		Username: acc.Username,
		UserRole: acc.Role,
	}

	token, err := CreateJWT(claims)
	if err != nil {
		return &SignInResult{}, err
	}

	res := SignInResult{
		Account:        acc,
		Token:          token,
		TokenExpiresAt: JWTExpiry,
	}

	return &res, nil
}

type SaveCommand struct {
	ID          uint    `json:"id"`
	Username    string  `json:"username"`
	Role        *string `json:"role"`
	RawPassword *string `json:"password,omitempty"`
}

func Save(r app.Request, cmd SaveCommand) (uint, error) {
	var old *Account
	if cmd.ID > 0 {
		var existing Account
		if err := r.DB.WithContext(r.Ctx).First(&existing, cmd.ID).Error; err == nil {
			old = &existing
		}
	}

	a := Account{
		ID:       cmd.ID,
		Username: cmd.Username,
		Role:     cmd.Role,
	}

	if cmd.RawPassword != nil && *cmd.RawPassword != "" {
		hash, err := HashPassword(*cmd.RawPassword)
		if err != nil {
			return 0, err
		}

		a.PasswordHash = hash
	} else if old != nil {
		a.PasswordHash = old.PasswordHash
	}

	if err := r.DB.WithContext(r.Ctx).Save(&a).Error; err != nil {
		return 0, err
	}

	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(a.ID), Account{})
	if old == nil {
		cl.LogCreate()
	} else {
		cl.LogDiff(old, &a)
	}

	return a.ID, nil
}

type ChangePasswordCommand struct {
	ID              uint    `json:"id"`
	CurrentPassword *string `json:"current_password"`
	NewPassword     string  `json:"new_password"`
}

func ChangePassword(r app.Request, cmd ChangePasswordCommand) error {
	acc, err := Get(r, GetQuery{ID: cmd.ID})
	if err != nil {
		return err
	}

	if cmd.CurrentPassword != nil { // TODO: Vurnability
		passValid := ValidatePassword(acc.PasswordHash, *cmd.CurrentPassword)
		if !passValid {
			return fmt.Errorf("wrong credentials")
		}
	}

	hash, err := HashPassword(cmd.NewPassword)
	if err != nil {
		return err
	}

	acc.PasswordHash = hash

	if err := r.DB.WithContext(r.Ctx).Save(acc).Error; err != nil {
		return err
	}

	cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(acc.ID), Account{})
	cl.Log("Izmjena lozinke")

	return nil
}

type DeleteCommand struct {
	ID uint
}

func Delete(r app.Request, cmd DeleteCommand) error {
	err := r.DB.WithContext(r.Ctx).Delete(&Account{}, cmd.ID).Error
	if err == nil {
		cl := activitylog.NewLogger(r.User.ID, r.User.Username).ForStruct(r.Ctx, r.DB, int64(cmd.ID), Account{})
		cl.LogDelete()
	}

	return err
}
