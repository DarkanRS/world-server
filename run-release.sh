#!/bin/bash

while :
do
  git pull origin master
  # Set the GitLab API endpoint and the project name
  GITLAB_API_URL="https://gitlab.com/api/v4"
  PROJECT_ID="42378996"

  # Retrieve the package repository ID using the GitLab API
  PACKAGE_DOWNLOAD_URL=$(curl -s "${GITLAB_API_URL}/projects/${PROJECT_ID}/releases?order_by=created_at&sort=desc" | jq -r 'max_by(.released_at) | .assets.links | map(select(.name | endswith("-jar"))) | .[0].direct_asset_url')

  # Download the package
  curl -L "${PACKAGE_DOWNLOAD_URL}" > world-server.jar

  echo "Successfully downloaded the latest package release (${PACKAGE_NAME})"
  java --enable-preview $DARKAN_JAVA_VM_ARGS -jar world-server.jar com.rs.Launcher >> mainlog.txt

  echo "You have 5 seconds to stop the server using Ctrl-C. Server will restart otherwise"
  sleep 5
done
