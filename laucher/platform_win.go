//go:build windows

package main

import (
	"fmt"
	"io"
	"net/http"
	"os"
	"os/exec"
	"path/filepath"
	"syscall"

	"golang.org/x/sys/windows"
)

func getRunnerName() string { return "runner.exe" }

func getAppDirectory() string {
	programFiles := os.Getenv("ProgramFiles")
	if programFiles == "" {
		programFiles = `C:\Program Files`
	}

	return filepath.Join(programFiles, repoOwner, installDirName)
}

func hasAdminPrivileges() bool {
	var sid *windows.SID
	err := windows.AllocateAndInitializeSid(
		&windows.SECURITY_NT_AUTHORITY, 2,
		windows.SECURITY_BUILTIN_DOMAIN_RID, windows.DOMAIN_ALIAS_RID_ADMINS,
		0, 0, 0, 0, 0, 0, &sid,
	)
	if err != nil {
		return false
	}
	defer windows.FreeSid(sid)
	token := windows.Token(0)
	member, err := token.IsMember(sid)
	return err == nil && member
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

	oldPath := targetPath + ".old"
	if _, err := os.Stat(targetPath); err == nil {
		_ = os.Remove(oldPath)
		if err := os.Rename(targetPath, oldPath); err != nil {
			return err
		}
	}

	if err := os.Rename(tmpFile.Name(), targetPath); err != nil {
		_ = os.Rename(oldPath, targetPath)
		return err
	}
	_ = os.Remove(oldPath)
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
	cmd.SysProcAttr = &syscall.SysProcAttr{
		CreationFlags: windows.CREATE_NEW_PROCESS_GROUP,
	}

	if err := cmd.Start(); err != nil {
		fmt.Printf("Execution failed: %v\n", err)
		pauseAndExit(1)
	}
}

func pauseAndExit(code int) {
	fmt.Println("\nPress Enter to exit...")
	var discard string
	_, _ = fmt.Scanln(&discard)
	os.Exit(code)
}
