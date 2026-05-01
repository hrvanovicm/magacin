package ws

import (
	"encoding/json"
	"fmt"
	"sync"

	"github.com/gorilla/websocket"
)

type Message struct {
	Users []string `json:"users"`
}

type Hub struct {
	mu      sync.Mutex
	clients map[*websocket.Conn]string
}

func NewHub() *Hub {
	return &Hub{
		clients: make(map[*websocket.Conn]string),
	}
}

func (h *Hub) Register(conn *websocket.Conn, username string) {
	h.mu.Lock()
	h.clients[conn] = username
	h.mu.Unlock()
	h.broadcast()
}

func (h *Hub) Unregister(conn *websocket.Conn) {
	h.mu.Lock()
	delete(h.clients, conn)
	h.mu.Unlock()
	h.broadcast()
}

func (h *Hub) GetUsers() []string {
	h.mu.Lock()
	defer h.mu.Unlock()

	users := h.prepareUsers()

	return users
}

func (h *Hub) broadcast() {
	h.mu.Lock()
	defer h.mu.Unlock()

	users := h.prepareUsers()

	data, _ := json.Marshal(Message{Users: users})
	for conn := range h.clients {
		if err := conn.WriteMessage(websocket.TextMessage, data); err != nil {
			fmt.Println(err)
		}
	}
}

func (h *Hub) prepareUsers() []string {
	seen := make(map[string]bool)

	var users []string
	for _, u := range h.clients {
		if !seen[u] {
			seen[u] = true
			users = append(users, u)
		}
	}

	if users == nil {
		users = []string{}
	}

	return users
}
