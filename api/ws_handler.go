package api

import (
	"log"

	"github.com/gin-gonic/gin"
)

func (h *Handler) HandleWS(c *gin.Context) {
	conn, err := upgrader.Upgrade(c.Writer, c.Request, nil)
	if err != nil {
		log.Println("ws upgrade error:", err)
		return
	}

	username := c.Query("username")
	if username == "" {
		username = "Unknown"
	}

	h.hub.Register(conn, username)
	defer func() {
		h.hub.Unregister(conn)
		conn.Close()
	}()

	for {
		_, _, err := conn.ReadMessage()
		if err != nil {
			break
		}
	}
}
