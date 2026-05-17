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
	res, err := server.ListServers(a.getRequest())
	if err != nil {
		a.report(err)
	}
	return res, err
}

func (a *WailsApp) GetLocalServerConfig() (*server.LocalConfig, error) {
	res, err := server.GetLocalConfig(a.getRequest())
	if err != nil {
		a.report(err)
	}
	return res, err
}

type SaveServerLocalConfigRequest = server.SaveConfigCommand

func (a *WailsApp) SaveServerLocalConfig(req SaveServerLocalConfigRequest) error {
	err := server.SaveConfig(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
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
	path, err := runtime.OpenFileDialog(a.ctx, runtime.OpenDialogOptions{
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
	err := server.UpdateLastUsedUsername(a.getRequest(), cmd)
	if err != nil {
		a.report(err)
	}
	return err
}

type UpsertServerLastUsedRequest = server.UpsertServerLastUsedCommand

func (a *WailsApp) UpsertServerLastUsed(req UpsertServerLastUsedRequest) error {
	err := server.UpsertServerLastUsed(a.getRequest(), req)
	if err != nil {
		a.report(err)
	}
	return err
}

func (a *WailsApp) ScanLocalNetwork() ([]server.DiscoveredServer, error) {
	ctx, cancel := context.WithTimeout(a.getRequest().Ctx, 15*time.Second)
	defer cancel()
	return server.ScanLocalNetwork(ctx)
}

func (a *WailsApp) ScanAndSaveServers() ([]server.Server, error) {
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

func (a *WailsApp) SaveServer(s server.Server) (int64, error) {
	err := server.SaveServer(a.getRequest(), s)
	if err != nil {
		a.report(err)
		return 0, err
	}
	return int64(s.ID), nil
}

func (a *WailsApp) DeleteServer(id int64) error {
	err := a.getRequest().DB.Delete(&server.Server{}, id).Error
	if err != nil {
		a.report(err)
	}
	return err
}
