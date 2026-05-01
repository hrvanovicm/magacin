package app

import (
	"context"
	"encoding/base64"
	"fmt"
	"os"
	"path/filepath"
	"strings"
	"time"

	"hrvanovicm/magacin/internal/server"

	"github.com/wailsapp/wails/v2/pkg/runtime"
)

func (a *WailsApp) ListServers() ([]server.Server, error) {
	servers, err := server.ListServers(a.getRequest())

	if err != nil {
		a.report(err)
		return servers, err
	}

	return servers, nil
}

func (a *WailsApp) GetLocalServerConfig() (*server.LocalConfig, error) {
	cfg, err := server.GetLocalConfig(a.getRequest())
	if err != nil {
		a.report(err)
		return nil, err
	}

	return cfg, nil
}

type SaveServerLocalConfigRequest = server.SaveConfigCommand

func (a *WailsApp) SaveServerLocalConfig(req SaveServerLocalConfigRequest) error {
	if err := server.SaveConfig(a.getRequest(), req); err != nil {
		a.report(err)
		return err
	}

	return nil
}

func (a *WailsApp) GetCompanyLogo() string {
	dir := getStorageDir()
	for _, ext := range []string{"png", "jpg", "jpeg", "gif", "webp"} {
		p := filepath.Join(dir, "logo."+ext)
		data, err := os.ReadFile(p)
		if err == nil {
			mime := "image/" + ext
			if ext == "jpg" {
				mime = "image/jpeg"
			}
			return fmt.Sprintf("data:%s;base64,%s", mime, base64.StdEncoding.EncodeToString(data))
		}
	}
	return ""
}

func (a *WailsApp) UploadCompanyLogo() error {
	path, err := runtime.OpenFileDialog(a.getRequest().Ctx, runtime.OpenDialogOptions{
		Title: "Odaberi logo kompanije",
		Filters: []runtime.FileFilter{
			{DisplayName: "Slike (*.png, *.jpg, *.jpeg)", Pattern: "*.png;*.jpg;*.jpeg"},
		},
	})
	if err != nil {
		return fmt.Errorf("logo dialog: %w", err)
	}
	if path == "" {
		return nil
	}

	data, err := os.ReadFile(path)
	if err != nil {
		return fmt.Errorf("logo read: %w", err)
	}

	ext := strings.ToLower(strings.TrimPrefix(filepath.Ext(path), "."))
	if ext == "" {
		ext = "png"
	}

	dir := getStorageDir()
	for _, e := range []string{"png", "jpg", "jpeg", "gif", "webp"} {
		_ = os.Remove(filepath.Join(dir, "logo."+e))
	}

	dest := filepath.Join(dir, "logo."+ext)
	if err := os.WriteFile(dest, data, 0644); err != nil {
		return fmt.Errorf("logo write: %w", err)
	}

	return nil
}

func (a *WailsApp) RemoveCompanyLogo() error {
	dir := getStorageDir()
	for _, e := range []string{"png", "jpg", "jpeg", "gif", "webp"} {
		_ = os.Remove(filepath.Join(dir, "logo."+e))
	}
	return nil
}

func (a *WailsApp) UpdateServerLastUsedUsername(cmd server.UpdateLastUsedUsernameCommand) error {
	if err := server.UpdateLastUsedUsername(a.getRequest(), cmd); err != nil {
		a.report(err)
		return err
	}
	return nil
}

type UpsertServerLastUsedRequest = server.UpsertServerLastUsedCommand

func (a *WailsApp) UpsertServerLastUsed(req UpsertServerLastUsedRequest) error {
	if err := server.UpsertServerLastUsed(a.getRequest(), req); err != nil {
		a.report(err)
		return err
	}
	return nil
}

func (a *WailsApp) ScanLocalNetwork() ([]server.DiscoveredServer, error) {
	ctx, cancel := context.WithTimeout(a.getRequest().Ctx, 15*time.Second)
	defer cancel()
	return server.ScanLocalNetwork(ctx)
}

func (a *WailsApp) ScanAndSaveServers() ([]server.Server, error) {
	if err := server.BatchDeleteServers(a.getRequest()); err != nil {
		a.report(err)
		return nil, err
	}

	ctx, cancel := context.WithTimeout(a.getRequest().Ctx, 15*time.Second)
	defer cancel()

	discovered, err := server.ScanLocalNetwork(ctx)
	if err != nil {
		a.report(err)
		return nil, err
	}

	seenNames := make(map[string]bool)
	for _, d := range discovered {
		seenNames[d.Name] = true
	}
	if local := server.GetLocalServer(ctx); local != nil && !seenNames[local.Name] {
		discovered = append([]server.DiscoveredServer{*local}, discovered...)
	}

	var saved []server.Server
	for _, d := range discovered {
		s := server.Server{Name: d.Name, Address: d.Address}
		if err := server.SaveServer(a.getRequest(), s); err != nil {
			a.report(err)
			continue
		}
		saved = append(saved, s)
	}

	if saved == nil {
		saved = []server.Server{}
	}
	return saved, nil
}
