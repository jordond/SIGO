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
