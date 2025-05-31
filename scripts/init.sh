#!/bin/bash

# This script initializes the repo

# Explode on errors 💥
set -euo pipefail

CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

TARGET_ENV_FILE="$ROOT/app-env.properties"
SAMPLE_ENV_FILE="$ROOT/app-env.sample.properties"
SECRET_FORECAST_API_KEY="FORECAST_API_KEY"

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

    # Check the TARGET_ENV_FILE to see if FORECAST_API_KEY is set
    local api_key_set=false

    if grep -q "^$SECRET_FORECAST_API_KEY=.\+" "$TARGET_ENV_FILE"; then
        api_key_set=true
    fi

    if [[ "$api_key_set" == false ]]; then
        echo
        echo "🔑 The FORECAST_API_KEY is not set or is empty."
        echo "Please enter your forecast API key (or press Enter to skip):"
        read -r forecast_key

        if [[ -n "$forecast_key" ]]; then
            # Replace the empty FORECAST_API_KEY with the actual key
            if grep -q "^$SECRET_FORECAST_API_KEY=" "$TARGET_ENV_FILE"; then
                # Key exists but is empty, replace it
                sed -i.bak "s/^$SECRET_FORECAST_API_KEY=.*$/$SECRET_FORECAST_API_KEY=$forecast_key/" "$TARGET_ENV_FILE"
            else
                # Key doesn't exist, append it
                echo "$SECRET_FORECAST_API_KEY=$forecast_key" >>"$TARGET_ENV_FILE"
            fi
            rm -f "$TARGET_ENV_FILE.bak"
            echo "✅ FORECAST_API_KEY has been set in '$TARGET_ENV_FILE'"
        else
            echo "⚠️ Skipping FORECAST_API_KEY setup. You can set it manually later in '$TARGET_ENV_FILE'"
        fi
    fi
}

copy_env
