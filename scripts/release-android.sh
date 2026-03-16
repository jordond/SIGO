#!/bin/bash

# Release script for building Android AAB artifacts
# Usage: ./sigo release:app:android [--version 1.0.1 | --semver patch] [flags]

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." >/dev/null 2>&1 && pwd)"
TOML="$ROOT/gradle/libs.versions.toml"

source "$ROOT/scripts/release-common.sh"

# Defaults
SEMVER=""
VERSION=""
NO_TAG=false
NO_COMMIT=false
NO_PUSH=false
NO_CLEAN=false
NO_BRANCH_CHECK=false
OUTPUT_DIR="$ROOT/release"
COMBINED_COMMIT=false # When true, skip commit/push/tag (release-app.sh handles it)

print_usage() {
    echo "Usage: release:app:android [options]"
    echo ""
    echo "Options:"
    echo "  -v, --version <version>   Set exact version (e.g. 1.0.1)"
    echo "  -s, --semver <level>      Bump version: major, minor, patch, none (default: patch)"
    echo "  -o, --output <dir>        Output directory for AAB (default: ./release)"
    echo "      --no-tag              Skip creating git tags"
    echo "      --no-commit           Skip creating git commit"
    echo "      --no-push             Skip pushing to remote"
    echo "      --no-git              Skip both tagging and committing"
    echo "      --no-clean            Skip clean before building"
    echo "      --no-branch-check     Skip branch verification"
    echo "      --combined-commit     Internal: skip commit/push/tag (handled by parent script)"
    echo "  -h, --help                Show this help"
}

# Parse arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
    -v | --version)
        [[ $# -lt 2 ]] && {
            echo "Error: --version requires a value"
            exit 1
        }
        VERSION="$2"
        shift 2
        ;;
    -s | --semver)
        [[ $# -lt 2 ]] && {
            echo "Error: --semver requires a value"
            exit 1
        }
        SEMVER="$2"
        shift 2
        ;;
    -o | --output)
        [[ $# -lt 2 ]] && {
            echo "Error: --output requires a value"
            exit 1
        }
        OUTPUT_DIR="$2"
        shift 2
        ;;
    --no-tag)
        NO_TAG=true
        shift
        ;;
    --no-commit)
        NO_COMMIT=true
        shift
        ;;
    --no-push)
        NO_PUSH=true
        shift
        ;;
    --no-git)
        NO_TAG=true
        NO_COMMIT=true
        shift
        ;;
    --no-clean)
        NO_CLEAN=true
        shift
        ;;
    --no-branch-check)
        NO_BRANCH_CHECK=true
        shift
        ;;
    --combined-commit)
        COMBINED_COMMIT=true
        shift
        ;;
    -h | --help)
        print_usage
        exit 0
        ;;
    *)
        echo "Unknown option: $1"
        print_usage
        exit 1
        ;;
    esac
done

# Combined mode: parent script handles all git operations
if [[ "$COMBINED_COMMIT" == true ]]; then
    NO_TAG=true
    NO_COMMIT=true
fi

# --- Step 0: Branch check ---

if [[ "$NO_BRANCH_CHECK" == false ]]; then
    branch=$(git -C "$ROOT" rev-parse --abbrev-ref HEAD)
    if [[ "$branch" != "main" && ! "$branch" =~ ^release/ ]]; then
        echo "Error: Must be on 'main' or a 'release/*' branch (currently on '$branch')"
        echo "Use --no-branch-check to skip this check"
        exit 1
    fi
    echo "✅ Branch check passed: $branch"
fi

# --- Step 0.5: Clean working tree check ---

if [[ "$NO_COMMIT" == false ]]; then
    check_clean_worktree
fi

# --- Step 1: Resolve version ---

current_version=$(read_toml_value "app-android-version")
current_code=$(read_toml_value "app-android-code")

echo "📋 Current Android version: $current_version ($current_code)"

if [[ -n "$VERSION" && -n "$SEMVER" ]]; then
    echo "Error: Cannot use both --version and --semver"
    exit 1
fi

if [[ -n "$VERSION" ]]; then
    # Validate version format
    if ! [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "Error: Version must be in format X.Y.Z (got '$VERSION')"
        exit 1
    fi
    new_version="$VERSION"
elif [[ -n "$SEMVER" ]]; then
    new_version=$(bump_version "$current_version" "$SEMVER")
else
    # Default to patch
    new_version=$(bump_version "$current_version" "patch")
fi

new_code=$((current_code + 1))

echo "🔄 New Android version: $new_version ($new_code)"

# --- Step 2: Update version in libs.versions.toml ---

write_toml_value "app-android-version" "$new_version"
write_toml_value "app-android-code" "$new_code"

echo "✅ Updated gradle/libs.versions.toml"

# Restore TOML on build failure
trap 'echo "⚠️  Build failed — reverting version bump"; git -C "$ROOT" checkout -- "$TOML"' ERR

# --- Step 3: Build ---

if [[ "$NO_CLEAN" == false ]]; then
    echo "🧹 Cleaning..."
    "$ROOT"/gradlew :apps:android:clean
fi

echo "🔨 Building release bundle..."
"$ROOT"/gradlew :apps:android:bundleRelease

# --- Step 4: Copy AAB ---

AAB_SOURCE="$ROOT/apps/android/build/outputs/bundle/release/android-release.aab"

if [[ ! -f "$AAB_SOURCE" ]]; then
    echo "Error: AAB not found at $AAB_SOURCE"
    exit 1
fi

mkdir -p "$OUTPUT_DIR"
AAB_DEST="$OUTPUT_DIR/android-${new_version}-${new_code}.aab"
cp "$AAB_SOURCE" "$AAB_DEST"

echo "✅ Copied AAB to $AAB_DEST"

# Build succeeded — remove the rollback trap
trap - ERR

# --- Step 5: Commit, tag, and push ---

if [[ "$NO_COMMIT" == false ]]; then
    echo "💾 Committing..."
    git -C "$ROOT" add "$TOML"
    git -C "$ROOT" commit -m "bump for android release ${new_version} (${new_code}) [skip-ci]"
    echo "✅ Committed version bump"

    if [[ "$NO_TAG" == false ]]; then
        echo "🏷️  Creating tags..."
        for tag in "android/build/${new_code}" "release/android/${new_version}"; do
            if git -C "$ROOT" tag -l "$tag" | grep -q .; then
                echo "Error: Tag '$tag' already exists"
                exit 1
            fi
        done
        git -C "$ROOT" tag "android/build/${new_code}"
        git -C "$ROOT" tag "release/android/${new_version}"
        echo "✅ Created tags: android/build/${new_code}, release/android/${new_version}"
    fi

    if [[ "$NO_PUSH" == false ]]; then
        echo "🚀 Pushing..."
        git -C "$ROOT" push
        if [[ "$NO_TAG" == false ]]; then
            git -C "$ROOT" push origin "android/build/${new_code}" "release/android/${new_version}"
        fi
        echo "✅ Pushed commit and tags"
    fi
fi

echo ""
echo "🎉 Android release $new_version ($new_code) complete!"
echo "   AAB: $AAB_DEST"
