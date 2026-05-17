package account

import (
	"fmt"
	"time"

	"github.com/golang-jwt/jwt"
	"golang.org/x/crypto/bcrypt"
)

var (
	JWTExpiry = 24 * time.Hour
	JWTSecret = "TUNSl78WK0n+GEgBMnJ1qLvNqb2BXrSR1HWTKz1fLzY="
)

func HashPassword(v string) (string, error) {
	hash, err := bcrypt.GenerateFromPassword([]byte(v), bcrypt.DefaultCost)
	if err != nil {
		return "", err
	}

	return string(hash), nil
}

func ValidatePassword(hash, raw string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(hash), []byte(raw))
	return err == nil
}

type JWTClaims struct {
	UserID   uint
	Username string
	UserRole *string
}

func CreateJWT(c JWTClaims) (string, error) {
	claims := jwt.MapClaims{
		"UserID":   c.UserID,
		"Username": c.Username,
		"UserRole": c.UserRole,
		"exp":      time.Now().Add(JWTExpiry).Unix(),
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	signed, err := token.SignedString([]byte(JWTSecret))
	if err != nil {
		return "", err
	}

	return signed, nil
}

func ParseJWT(v string) (JWTClaims, error) {
	token, err := jwt.Parse(v, func(t *jwt.Token) (any, error) {
		if _, ok := t.Method.(*jwt.SigningMethodHMAC); !ok {
			return nil, fmt.Errorf("unexpected signing method")
		}

		return []byte(JWTSecret), nil
	})

	if err != nil {
		return JWTClaims{}, err
	}

	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok || !token.Valid {
		return JWTClaims{}, fmt.Errorf("token is not valid")
	}

	var userRole *string
	if r, ok := claims["UserRole"].(string); ok {
		userRole = &r
	}

	username, _ := claims["Username"].(string)
	result := JWTClaims{
		UserID:   uint(claims["UserID"].(float64)),
		Username: username,
		UserRole: userRole,
	}

	return result, nil
}
