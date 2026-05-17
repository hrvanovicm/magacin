package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"path/filepath"
	"strconv"
	"strings"
)

const (
	repoOwner       = "hrvanovicm"
	repoName        = "magacin"
	installDirName  = "magacin"
	versionFileName = "version.txt"
)

type GitHubRelease struct {
	TagName string `json:"tag_name"`
	Assets  []struct {
		Name               string `json:"name"`
		BrowserDownloadURL string `json:"browser_download_url"`
	} `json:"assets"`
}

func main() {
	appDir := getAppDirectory()
	runnerName := getRunnerName()
	runnerPath := filepath.Join(appDir, runnerName)
	versionPath := filepath.Join(appDir, versionFileName)

	fmt.Printf("App Directory: %s\n", appDir)

	if err := os.MkdirAll(appDir, 0755); err != nil {
		fmt.Printf("Failed to create directories: %v\n", err)
		pauseAndExit(1)
	}

	currentVersionStr := "0.0.0"
	if _, err := os.Stat(versionPath); err == nil {
		data, err := os.ReadFile(versionPath)
		if err == nil {
			currentVersionStr = strings.TrimSpace(string(data))
		}
	}
	fmt.Printf("Current version: %s\n", currentVersionStr)

	fmt.Println("Checking for updates...")
	latestRelease, err := fetchLatestGitHubRelease(repoOwner, repoName)
	if err != nil {
		fmt.Printf("Update check failed (booting current binary): %v\n", err)
		runRunner(runnerPath)
		return
	}

	remoteVersionStr := latestRelease.TagName

	if isNewerVersion(currentVersionStr, remoteVersionStr) {
		fmt.Printf("New version found: %s. Upgrading...\n", remoteVersionStr)

		var downloadURL string
		for _, asset := range latestRelease.Assets {
			if asset.Name == runnerName {
				downloadURL = asset.BrowserDownloadURL
				break
			}
		}

		if downloadURL == "" {
			fmt.Printf("Error: Asset matching target '%s' not found in release.\n", runnerName)
		} else {
			err := downloadAndUpdateBinary(downloadURL, runnerPath, versionPath, remoteVersionStr)
			if err != nil {
				fmt.Printf("Upgrade failed: %v\n", err)
			} else {
				fmt.Println("Upgrade successful!")
			}
		}
	} else {
		fmt.Println("Application is up to date.")
	}

	runRunner(runnerPath)
}

func fetchLatestGitHubRelease(owner, repo string) (*GitHubRelease, error) {
	url := fmt.Sprintf("https://api.github.com/repos/%s/%s/releases/latest", owner, repo)

	client := &http.Client{}
	req, err := http.NewRequest("GET", url, nil)
	if err != nil {
		return nil, err
	}

	req.Header.Set("User-Agent", "Go-Launcher-Updater")

	resp, err := client.Do(req)
	if err != nil {
		return nil, err
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("GitHub API status: %s", resp.Status)
	}

	var release GitHubRelease
	if err := json.NewDecoder(resp.Body).Decode(&release); err != nil {
		return nil, err
	}
	return &release, nil
}

func isNewerVersion(local, remote string) bool {
	localParts := parseVersion(local)
	remoteParts := parseVersion(remote)

	maxLen := len(localParts)
	if len(remoteParts) > maxLen {
		maxLen = len(remoteParts)
	}

	for i := 0; i < maxLen; i++ {
		var locVal, remVal int
		if i < len(localParts) {
			locVal = localParts[i]
		}
		if i < len(remoteParts) {
			remVal = remoteParts[i]
		}

		if remVal > locVal {
			return true
		}
		if locVal > remVal {
			return false
		}
	}

	return false
}

func parseVersion(vStr string) []int {
	vStr = strings.TrimPrefix(strings.TrimSpace(vStr), "v")
	if idx := strings.IndexAny(vStr, "-+"); idx != -1 {
		vStr = vStr[:idx]
	}

	segments := strings.Split(vStr, ".")
	var parts []int
	for _, segment := range segments {
		val, err := strconv.Atoi(segment)
		if err != nil {
			val = 0
		}
		parts = append(parts, val)
	}
	return parts
}
