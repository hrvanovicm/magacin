//go:build linux

package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
)

func getRunnerName() string { return "Magacin" }

func getAppDirectory() string {
	home, err := os.UserHomeDir()
	if err != nil {
		panic(err)
	}

	return filepath.Join(home, repoOwner, installDirName)
}

func hasAdminPrivileges() bool {
	return os.Getuid() == 0
}

func downloadAndUpdateBinary(url, targetPath, versionPath, versionStr string) error {
	dir := filepath.Dir(targetPath)
	tmpFile, err := os.CreateTemp(dir, "runner_download_*.tmp")
	if err != nil {
		return err
	}
	defer os.Remove(tmpFile.Name())
	defer tmpFile.Close()

	resp, err := http.Get(url)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return fmt.Errorf("download response code: %s", resp.Status)
	}

	if _, err := io.Copy(tmpFile, resp.Body); err != nil {
		return err
	}
	tmpFile.Close()

	if err := os.Rename(tmpFile.Name(), targetPath); err != nil {
		return err
	}

	if err := os.Chmod(targetPath, 0755); err != nil {
		return fmt.Errorf("failed setting execution permissions: %w", err)
	}

	_ = os.WriteFile(versionPath, []byte(versionStr), 0644)
	return nil
}

func runRunner(runnerPath string) {
	if _, err := os.Stat(runnerPath); os.IsNotExist(err) {
		fmt.Printf("Binary missing at: %s\n", runnerPath)
		pauseAndExit(1)
	}

	cmd := exec.Command(runnerPath)
	cmd.Dir = filepath.Dir(runnerPath)

	if err := cmd.Start(); err != nil {
		fmt.Printf("Execution failed: %v\n", err)
		pauseAndExit(1)
	}
}

func pauseAndExit(code int) {
	os.Exit(code)
}
