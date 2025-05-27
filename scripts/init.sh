#!/bin/bash

# This script initializes the repo

# Explode on errors 💥
set -euo pipefail

CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

TARGET_ENV_FILE="$ROOT/app-env.properties"
SAMPLE_ENV_FILE="$ROOT/app-env.sample.properties"

function copy_env() {
    if [ -f "$TARGET_ENV_FILE" ]; then
        read -r -p "The file '$TARGET_ENV_FILE' already exists. Do you want to overwrite it? (y/N) " response
        case "$response" in
        [yY][eE][sS] | [yY])
            echo "Overwriting '$TARGET_ENV_FILE'..."
            ;;
        *)
            echo "Skipping overwrite. '$TARGET_ENV_FILE' remains unchanged."
            return 0
            ;;
        esac
    fi

    # Creating a copy of the env properties
    echo "Creating '$TARGET_ENV_FILE' from '$SAMPLE_ENV_FILE'..."
    cp -v "$SAMPLE_ENV_FILE" "$TARGET_ENV_FILE"
}

copy_env
