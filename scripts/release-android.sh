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
AUTO_YES=false
DRY_RUN=false
OUTPUT_DIR="$ROOT/release"
COMBINED_COMMIT=false # When true, skip commit/push/tag (release-app.sh handles it)

print_usage() {
    echo "Usage: release:app:android [options]"
    echo ""
    echo "Options:"
    echo "  -v, --version <version>   Set exact version (e.g. 1.0.1)"
    echo "  -s, --semver <level>      Bump version: major, minor, patch, none"
    echo "  -o, --output <dir>        Output directory for AAB (default: ./release)"
    echo "  -y, --yes                 Skip confirmation prompts"
    echo "  -n, --dry-run             Show what would happen without making changes"
    echo "      --no-tag              Skip creating git tags"
    echo "      --no-commit           Skip creating git commit"
    echo "      --no-push             Skip pushing to remote"
    echo "      --no-git              Skip both tagging and committing"
    echo "      --no-clean            Skip clean before building"
    echo "      --no-branch-check     Skip branch verification"
    echo "      --combined-commit     Internal: skip commit/push/tag (handled by parent script)"
    echo "  -h, --help                Show this help"
    echo ""
    echo "Either --version or --semver is required."
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
    -y | --yes)
        AUTO_YES=true
        shift
        ;;
    -n | --dry-run)
        DRY_RUN=true
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

# Require version argument early (before any git checks)
if [[ -z "$VERSION" && -z "$SEMVER" ]]; then
    print_usage
    exit 1
fi

if [[ -n "$VERSION" && -n "$SEMVER" ]]; then
    echo "Error: Cannot use both --version and --semver"
    exit 1
fi

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

if [[ -n "$VERSION" ]]; then
    # Validate version format
    if ! [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "Error: Version must be in format X.Y.Z (got '$VERSION')"
        exit 1
    fi
    new_version="$VERSION"
else
    new_version=$(bump_version "$current_version" "$SEMVER")
fi

new_code=$((current_code + 1))

# --- Summary ---

if [[ "$COMBINED_COMMIT" == false ]]; then
    echo ""
    echo "━━━ Release Summary ━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "  Platform:     Android"
    echo "  Version:      $current_version → $new_version"
    echo "  Build code:   $current_code → $new_code"
    echo "  Output:       $OUTPUT_DIR/android-${new_version}-${new_code}.aab"
    echo "  Clean build:  $([[ "$NO_CLEAN" == false ]] && echo "yes" || echo "no")"
    echo "  Git commit:   $([[ "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
    echo "  Git tag:      $([[ "$NO_TAG" == false && "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
    echo "  Git push:     $([[ "$NO_PUSH" == false && "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    check_dry_run
    confirm_or_exit "Proceed with release?"
fi

# --- Step 2: Update version in libs.versions.toml ---

echo "📝 Updating version in gradle/libs.versions.toml..."
write_toml_value "app-android-version" "$new_version"
write_toml_value "app-android-code" "$new_code"
echo "   app-android-version = \"$new_version\""
echo "   app-android-code = \"$new_code\""
echo "✅ Updated gradle/libs.versions.toml"

# Restore TOML on build failure
trap 'echo "⚠️  Build failed — reverting version bump"; git -C "$ROOT" checkout -- "$TOML"' ERR

# --- Step 3: Build ---

if [[ "$NO_CLEAN" == false ]]; then
    echo "🧹 Cleaning build artifacts..."
    gradle_quiet :apps:android:clean
    echo "✅ Clean complete"
fi

echo "🔨 Building release bundle (this may take a while)..."
gradle_quiet :apps:android:bundleRelease
echo "✅ Build complete"

# --- Step 4: Copy AAB ---

AAB_SOURCE="$ROOT/apps/android/build/outputs/bundle/release/android-release.aab"

if [[ ! -f "$AAB_SOURCE" ]]; then
    echo "Error: AAB not found at $AAB_SOURCE"
    exit 1
fi

mkdir -p "$OUTPUT_DIR"
AAB_DEST="$OUTPUT_DIR/android-${new_version}-${new_code}.aab"
echo "📦 Copying AAB to $AAB_DEST..."
cp "$AAB_SOURCE" "$AAB_DEST"
echo "✅ AAB artifact ready"

# Build succeeded — remove the rollback trap
trap - ERR

# --- Step 5: Commit, tag, and push ---

if [[ "$NO_COMMIT" == false ]]; then
    echo "💾 Staging and committing version bump..."
    git -C "$ROOT" add "$TOML"
    git -C "$ROOT" commit -m "bump for android release ${new_version} (${new_code}) [skip-ci]"
    echo "✅ Committed version bump"

    if [[ "$NO_TAG" == false ]]; then
        echo "🏷️  Creating tags..."
        for tag in "android/build/${new_code}" "release/android/${new_version}"; do
            if git -C "$ROOT" tag -l "$tag" | grep -q .; then
                echo "   $tag (overwriting)"
            else
                echo "   $tag"
            fi
        done
        git -C "$ROOT" tag -f "android/build/${new_code}"
        git -C "$ROOT" tag -f "release/android/${new_version}"
        echo "✅ Tags created"
    fi

    if [[ "$NO_PUSH" == false ]]; then
        echo "🚀 Pushing commit to remote..."
        git -C "$ROOT" push
        if [[ "$NO_TAG" == false ]]; then
            echo "🚀 Pushing tags to remote..."
            git -C "$ROOT" push --force origin "android/build/${new_code}" "release/android/${new_version}"
        fi
        echo "✅ Pushed to remote"
    fi
fi

echo ""
echo "🎉 Android release $new_version ($new_code) complete!"
echo "   AAB: $AAB_DEST"
