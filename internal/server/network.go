package server

import (
	"context"
	"encoding/json"
	"fmt"
	"net"
	"net/http"
	"sync"
	"time"
)

const ServerPort = 8080

type DiscoveredServer struct {
	Address string `json:"address"`
	Name    string `json:"name"`
}

type healthResponse struct {
	Status string `json:"status"`
	Name   string `json:"name"`
}

func ScanLocalNetwork(ctx context.Context) ([]DiscoveredServer, error) {
	hosts := collectSubnetHosts()
	if len(hosts) == 0 {
		return nil, nil
	}

	var mu sync.Mutex
	var results []DiscoveredServer
	seenNames := make(map[string]bool)
	var wg sync.WaitGroup
	sem := make(chan struct{}, 150)

	dialer := &net.Dialer{Timeout: 80 * time.Millisecond}
	client := &http.Client{
		Timeout: 200 * time.Millisecond,
		Transport: &http.Transport{
			DisableKeepAlives: true,
		},
	}

	for _, host := range hosts {
		if ctx.Err() != nil {
			break
		}

		wg.Add(1)
		go func(h string) {
			defer wg.Done()
			sem <- struct{}{}
			defer func() { <-sem }()

			addr := fmt.Sprintf("%s:%d", h, ServerPort)
			conn, err := dialer.DialContext(ctx, "tcp", addr)
			if err != nil {
				return
			}
			conn.Close()

			url := fmt.Sprintf("http://%s:%d/health", h, ServerPort)
			req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
			if err != nil {
				return
			}

			resp, err := client.Do(req)
			if err != nil {
				return
			}
			defer resp.Body.Close()

			if resp.StatusCode != http.StatusOK {
				return
			}

			var body healthResponse
			if err := json.NewDecoder(resp.Body).Decode(&body); err != nil {
				return
			}
			if body.Status != "ok" {
				return
			}

			mu.Lock()
			if !seenNames[body.Name] {
				seenNames[body.Name] = true
				results = append(results, DiscoveredServer{
					Address: fmt.Sprintf("http://%s:%d", h, ServerPort),
					Name:    body.Name,
				})
			}
			mu.Unlock()
		}(host)
	}

	wg.Wait()
	return results, nil
}

func GetLocalServer(ctx context.Context) *DiscoveredServer {
	client := &http.Client{Timeout: 200 * time.Millisecond}
	url := fmt.Sprintf("http://localhost:%d/health", ServerPort)
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
	if err != nil {
		return nil
	}
	resp, err := client.Do(req)
	if err != nil {
		return nil
	}
	defer resp.Body.Close()
	if resp.StatusCode != http.StatusOK {
		return nil
	}
	var body healthResponse
	if err := json.NewDecoder(resp.Body).Decode(&body); err != nil || body.Status != "ok" {
		return nil
	}
	return &DiscoveredServer{
		Address: fmt.Sprintf("http://localhost:%d", ServerPort),
		Name:    body.Name,
	}
}

func collectSubnetHosts() []string {
	ifaces, err := net.Interfaces()
	if err != nil {
		return nil
	}

	seen := make(map[string]bool)
	var hosts []string

	for _, iface := range ifaces {
		if iface.Flags&net.FlagUp == 0 || iface.Flags&net.FlagLoopback != 0 {
			continue
		}

		addrs, err := iface.Addrs()
		if err != nil {
			continue
		}

		for _, addr := range addrs {
			ipNet, ok := addr.(*net.IPNet)
			if !ok {
				continue
			}
			ip4 := ipNet.IP.To4()
			if ip4 == nil {
				continue
			}

			prefix := fmt.Sprintf("%d.%d.%d", ip4[0], ip4[1], ip4[2])
			for i := 1; i <= 254; i++ {
				host := fmt.Sprintf("%s.%d", prefix, i)
				if !seen[host] {
					seen[host] = true
					hosts = append(hosts, host)
				}
			}
		}
	}

	return hosts
}
