#!/bin/bash

set -euo pipefail

# Shared helpers for release scripts
# Source this file: source "$ROOT/scripts/release-common.sh"

# Requires: $TOML to be set before sourcing

# Read a value from libs.versions.toml
# Usage: read_toml_value "key-name"
read_toml_value() {
    local key="$1"
    grep "^${key} = " "$TOML" | sed 's/.*= *"\(.*\)"/\1/'
}

# Write a value to libs.versions.toml
write_toml_value() {
    local key value
    key=$(printf '%s\n' "$1" | sed 's/[.[\/*^$]/\\&/g')
    value=$(printf '%s\n' "$2" | sed 's/[&/\\]/\\&/g')
    sed -i '' "s/^${key} = \".*\"/${key} = \"${value}\"/" "$TOML"
}

# Bump a semver version string
# Usage: bump_version "1.0.0" "patch" => "1.0.1"
bump_version() {
    local current="$1"
    local level="$2"

    IFS='.' read -r major minor patch <<<"$current"

    case "$level" in
    major)
        major=$((major + 1))
        minor=0
        patch=0
        ;;
    minor)
        minor=$((minor + 1))
        patch=0
        ;;
    patch)
        patch=$((patch + 1))
        ;;
    none) ;;
    *)
        echo "Error: Invalid semver level '$level'. Use: major, minor, patch, none"
        exit 1
        ;;
    esac

    echo "${major}.${minor}.${patch}"
}

# Check that the working tree has no uncommitted changes
# Usage: check_clean_worktree
check_clean_worktree() {
    if ! git -C "$ROOT" diff --quiet HEAD; then
        echo "Error: Working tree has uncommitted changes. Commit or stash first."
        exit 1
    fi
}

# Exit cleanly if --dry-run is active
# Usage: check_dry_run (call after printing the summary)
check_dry_run() {
    if [[ "${DRY_RUN:-false}" == true ]]; then
        echo "Dry run — no changes made."
        exit 0
    fi
}

# Prompt for confirmation (unless --yes was passed)
# Usage: confirm_or_exit "Are you sure?"
confirm_or_exit() {
    local prompt="${1:-Continue?}"
    if [[ "${AUTO_YES:-false}" == true ]]; then
        return 0
    fi
    read -r -p "$prompt [y/N] " answer
    case "$answer" in
    [yY] | [yY][eE][sS]) return 0 ;;
    *)
        echo "Aborted."
        exit 0
        ;;
    esac
}

# Run gradle quietly (only show stderr / errors)
# Usage: gradle_quiet :task:name
gradle_quiet() {
    "$ROOT"/gradlew "$@" >/dev/null 2>&1 || {
        echo "Error: Gradle task failed. Re-running with output for diagnostics..."
        "$ROOT"/gradlew "$@"
        return 1
    }
}
