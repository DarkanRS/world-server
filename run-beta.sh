#!/bin/bash

while :
do
  git pull origin dev
  # Set the GitLab API endpoint and the project name
  GITLAB_API_URL="https://gitlab.com/api/v4"
  PROJECT_ID="42378996"
  PROJECT_NAME="world-server"

  # Get the web path for the project ID for downloading the package file
  WEB_PATH=$(curl -s "${GITLAB_API_URL}/projects/${PROJECT_ID}" | jq -r '.web_url')

  # Retrieve the package repository ID using the GitLab API
  PACKAGE_REPOSITORY_ID=$(curl -s "${GITLAB_API_URL}/projects/${PROJECT_ID}/packages?order_by=created_at&sort=desc" | jq -r 'map(select(.name == "rs/darkan/'${PROJECT_NAME}'")) | max_by(.created_at) .id')

  # Retrieve the latest package repository release using the GitLab API
  LATEST_RELEASE=$(curl -s "${GITLAB_API_URL}/projects/${PROJECT_ID}/packages/${PACKAGE_REPOSITORY_ID}/package_files" | jq -r '. | map(select(.file_name | endswith("-all.jar"))) | max_by(.created_at) | .id')

  # Extract the package name and download URL from the latest release
  PACKAGE_DOWNLOAD_URL="${WEB_PATH}/-/package_files/${LATEST_RELEASE}/download"

  # Download the package
  curl -L "${PACKAGE_DOWNLOAD_URL}" > world-server.jar

  echo "Successfully downloaded the latest package release (${PACKAGE_REPOSITORY_ID}/${LATEST_RELEASE})"
  java --enable-preview $DARKAN_JAVA_VM_ARGS -jar world-server.jar com.rs.Launcher >> mainlog.txt

  echo "You have 5 seconds to stop the server using Ctrl-C. Server will restart otherwise"
  sleep 5
done
