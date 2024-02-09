#!/bin/bash

# Define the target directories and the repository URL
TARGET_DIR_CACHE="/darkan/cache"
TARGET_DIR_WS="/darkan/world-server"
REPO_URL_CACHE="https://gitlab.com/darkanrs/cache.git"
REPO_URL_WS="https://gitlab.com/darkanrs/world-server.git"
TEMP_DIR="/tmp/darkan-world-server"

# Function to clone using git init and fetch for cache
clone_repo_cache() {
  echo "Initializing and fetching cache repository into $TARGET_DIR_CACHE..."
  git init "$TARGET_DIR_CACHE"
  cd "$TARGET_DIR_CACHE"
  git remote add origin "$REPO_URL_CACHE"
  git fetch
  git reset origin/master  # Reset in case versioned files existed before
  git checkout -t origin/master
}

# Function to handle world-server data and plugins directory
handle_world_server_dirs() {
  # Check if data and plugins directories are empty
  if [ -z "$(ls -A $TARGET_DIR_WS/data)" ] || [ -z "$(ls -A $TARGET_DIR_WS/plugins)" ]; then
    echo "One or both directories are empty. Cloning world-server to temporary directory for setup..."
    # Clone to a temporary directory
    git clone --depth 1 "$REPO_URL_WS" "$TEMP_DIR"
    # Copy the data and plugins directories if they are empty
    if [ -z "$(ls -A $TARGET_DIR_WS/data)" ]; then
      echo "Copying data directory..."
      cp -r "$TEMP_DIR/data" "$TARGET_DIR_WS/"
    fi
    if [ -z "$(ls -A $TARGET_DIR_WS/plugins)" ]; then
      echo "Copying plugins directory..."
      cp -r "$TEMP_DIR/plugins" "$TARGET_DIR_WS/"
    fi
    # Clean up the temporary directory
    rm -rf "$TEMP_DIR"
  else
    echo "/darkan/world-server/data and /darkan/world-server/plugins directories are already set up."
  fi
}

# Check and clone for cache
if [ ! -d "$TARGET_DIR_CACHE" ] || [ -z "$(ls -A $TARGET_DIR_CACHE)" ]; then
  echo "Cloning the cache repository into $TARGET_DIR_CACHE..."
  git clone --depth 1 "$REPO_URL_CACHE" "$TARGET_DIR_CACHE"
elif [ ! -d "$TARGET_DIR_CACHE/.git" ]; then
  echo "$TARGET_DIR_CACHE exists and is not a git repository. Initializing repository..."
  clone_repo_cache
else
  echo "$TARGET_DIR_CACHE is already a git repository. Pulling latest changes..."
  cd "$TARGET_DIR_CACHE"
  git pull
fi

# Ensure world-server data and plugins directories are properly set up
handle_world_server_dirs

cd /darkan/world-server

# Proceed with the original entrypoint command
exec "$@"