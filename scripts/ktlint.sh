#!/bin/bash

# Run ktlint.sh on staged or all Kotlin files
# Usage: ktlint.sh [--staged | --all] [ktlint.sh args]
# Example: ktlint.sh --staged -F

# Explode on errors 💥
set -euo pipefail

CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

KTLINT="$ROOT/.app/ktlint"

# Check if $ROOT/.app/ktlint exists
if [[ ! -f "$KTLINT" ]]; then
    echo "Error: $KTLINT not found, you need to run 'sigo init ktlint'"
    exit 1
fi

# Get first argument which can be --staged or --all
MODE=${1:-}
shift || true

# Function to run ktlint on staged files
run_ktlint_staged() {
    echo "Linting staged files..."
    local changed_files
    changed_files=$(git diff --name-only --cached --relative | grep '\.kt[s\"]\?$' || true)
    if [[ -z "$changed_files" ]]; then
        exit 0
    fi

    echo "${changed_files[@]}" | xargs "$KTLINT" --relative --reporter=plain "$@" 2>&1
}

# Function to run ktlint.sh on all files
run_ktlint_all() {
    echo "Linting all files..."
    "$KTLINT" --reporter=plain '**/*.kt' '**/*.kts' '!**/build/**' '!**/generated/**' "$@"
}

if [[ -z "$MODE" ]]; then
    run_ktlint_all "$@"
    exit 0
else
    case "$MODE" in
    --help)
        echo "usage: ktlint [--staged | --all] [ktlint args]"
        echo ""
        echo "Default is to run ktlint on all Kotlin files"
        echo ""
        echo "You can automatically fix files by using the -F flag:"
        printf "\t ktlint --staged -F"
        exit 0
        ;;
    --staged)
        run_ktlint_staged "$@"
        ;;
    --all)
        run_ktlint_all "$@"
        ;;
    *)
        echo "Invalid argument: $MODE"
        echo "usage: ktlint [--staged | --all] [ktlint args]"
        exit 1
        ;;
    esac
fi
