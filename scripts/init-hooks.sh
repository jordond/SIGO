#!/bin/bash

# This script installs git hooks for the project

# Explode on errors 💥
set -euo pipefail

# Folders
CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"
HOOK_SOURCE="$ROOT/scripts/hooks"
HOOK_OUTPUT="$ROOT/.git/hooks"

# Colors
NC='\033[0m' # No Color
GRAY='\033[0;37m'

# Check if --output flag is passed
OUTPUT_FLAG=false
if [[ "$#" -eq 1 && "$1" == "--output" ]]; then
    OUTPUT_FLAG=true
fi

copy_hooks() {
    echo "Installing git hooks"

    for file in "$HOOK_SOURCE"/*; do
        filename=$(basename "$file")
        hook_file="$HOOK_OUTPUT/$filename"

        echo " "
        echo "Adding $filename to $HOOK_OUTPUT"

        if $OUTPUT_FLAG; then
            printf "%s contents:\n\n" "$file"
            hook_contents=$(cat "$file")
            printf "${GRAY}%s${NC}\n" "$hook_contents"
            echo " "
        fi

        cp "$file" "$hook_file"
        chmod +x "$hook_file"
    done
}

copy_hooks

echo "Git hooks have been installed successfully."
