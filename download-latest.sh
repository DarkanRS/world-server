#!/bin/bash

# Set the GitLab API endpoint and the project name
GITLAB_API_URL="https://gitlab.com/api/v4"
PROJECT_ID="42378996"

# Get the web path for the project ID for downloading the package file
WEB_PATH=$(curl -s "${GITLAB_API_URL}/projects/${PROJECT_ID}" | jq -r '.web_url')

# Retrieve the package repository ID using the GitLab API
PACKAGE_REPOSITORY_ID=$(curl -s "${GITLAB_API_URL}/projects/${PROJECT_ID}/packages?order_by=created_at&sort=desc" | jq -r '.[].id' | head -n 1)

# Retrieve the latest package repository release using the GitLab API
LATEST_RELEASE=$(curl -s "${GITLAB_API_URL}/projects/${PROJECT_ID}/packages/${PACKAGE_REPOSITORY_ID}/package_files?order_by=created_at&sort=desc" | jq -r '. | map(select(.file_name | endswith("-all.jar"))) | max_by(.created_at) | .id')

# Extract the package name and download URL from the latest release
PACKAGE_DOWNLOAD_URL="${WEB_PATH}/-/package_files/${LATEST_RELEASE}/download"

# Download the package
curl -L "${PACKAGE_DOWNLOAD_URL}" > world-server.jar

echo "Successfully downloaded the latest package release (${PACKAGE_NAME})"