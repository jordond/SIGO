#!/bin/bash

# Release script for iOS version bumping and tagging
# Usage: ./sigo release:app:ios [--version 1.0.1 | --semver patch] [flags]

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." >/dev/null 2>&1 && pwd)"
TOML="$ROOT/gradle/libs.versions.toml"
PBXPROJ="$ROOT/apps/ios/iosApp.xcodeproj/project.pbxproj"

source "$ROOT/scripts/release-common.sh"

# Defaults
SEMVER=""
VERSION=""
NO_TAG=false
NO_COMMIT=false
NO_PUSH=false
NO_BRANCH_CHECK=false
AUTO_YES=false
DRY_RUN=false
COMBINED_COMMIT=false

print_usage() {
    echo "Usage: release:app:ios [options]"
    echo ""
    echo "Options:"
    echo "  -v, --version <version>   Set exact version (e.g. 1.0.1)"
    echo "  -s, --semver <level>      Bump version: major, minor, patch, none"
    echo "  -y, --yes                 Skip confirmation prompts"
    echo "  -n, --dry-run             Show what would happen without making changes"
    echo "      --no-tag              Skip creating git tags"
    echo "      --no-commit           Skip creating git commit"
    echo "      --no-push             Skip pushing to remote"
    echo "      --no-git              Skip both tagging and committing"
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

current_version=$(read_toml_value "app-ios-version")

echo "📋 Current iOS version: $current_version"

if [[ -n "$VERSION" ]]; then
    if ! [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "Error: Version must be in format X.Y.Z (got '$VERSION')"
        exit 1
    fi
    new_version="$VERSION"
else
    new_version=$(bump_version "$current_version" "$SEMVER")
fi

# Increment build number from pbxproj
build_values=$(grep "CURRENT_PROJECT_VERSION = " "$PBXPROJ" | sed 's/.*= *\([0-9]*\).*/\1/' | sort -u)
if [[ $(echo "$build_values" | wc -l) -gt 1 ]]; then
    echo "Error: Inconsistent CURRENT_PROJECT_VERSION values in pbxproj: $(echo $build_values | tr '\n' ' ')"
    exit 1
fi
current_build=$(echo "$build_values" | head -1)
new_build=$((current_build + 1))

# --- Summary ---

if [[ "$COMBINED_COMMIT" == false ]]; then
    echo ""
    echo "━━━ Release Summary ━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo "  Platform:     iOS"
    echo "  Version:      $current_version → $new_version"
    echo "  Build number: $current_build → $new_build"
    echo "  Git commit:   $([[ "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
    echo "  Git tag:      $([[ "$NO_TAG" == false && "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
    echo "  Git push:     $([[ "$NO_PUSH" == false && "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""
    check_dry_run
    confirm_or_exit "Proceed with release?"
fi

# --- Step 2: Update versions ---

echo "📝 Updating version in gradle/libs.versions.toml..."
write_toml_value "app-ios-version" "$new_version"
echo "   app-ios-version = \"$new_version\""
echo "✅ Updated gradle/libs.versions.toml"

echo "📝 Updating Xcode project..."
# Update Xcode project - MARKETING_VERSION and CURRENT_PROJECT_VERSION
# Anchored to tab-indented build settings lines to avoid matching comments
sed -i '' "s/^\([[:blank:]]*\)MARKETING_VERSION = .*;/\1MARKETING_VERSION = ${new_version};/" "$PBXPROJ"
sed -i '' "s/^\([[:blank:]]*\)CURRENT_PROJECT_VERSION = [0-9]*;/\1CURRENT_PROJECT_VERSION = ${new_build};/" "$PBXPROJ"
echo "   MARKETING_VERSION = $new_version"
echo "   CURRENT_PROJECT_VERSION = $new_build"
echo "✅ Updated Xcode project"

# --- Step 3: Commit, tag, and push ---

if [[ "$NO_COMMIT" == false ]]; then
    echo "💾 Staging and committing version bump..."
    git -C "$ROOT" add "$TOML" "$PBXPROJ"
    git -C "$ROOT" commit -m "bump for ios release ${new_version} (${new_build}) [skip-ci]"
    echo "✅ Committed version bump"

    if [[ "$NO_TAG" == false ]]; then
        echo "🏷️  Creating tags..."
        for tag in "ios/build/${new_build}" "release/ios/${new_version}"; do
            if git -C "$ROOT" tag -l "$tag" | grep -q .; then
                echo "Error: Tag '$tag' already exists"
                exit 1
            fi
            echo "   $tag"
        done
        git -C "$ROOT" tag "ios/build/${new_build}"
        git -C "$ROOT" tag "release/ios/${new_version}"
        echo "✅ Tags created"
    fi

    if [[ "$NO_PUSH" == false ]]; then
        echo "🚀 Pushing commit to remote..."
        git -C "$ROOT" push
        if [[ "$NO_TAG" == false ]]; then
            echo "🚀 Pushing tags to remote..."
            git -C "$ROOT" push origin "ios/build/${new_build}" "release/ios/${new_version}"
        fi
        echo "✅ Pushed to remote"
    fi
fi

# --- Step 4: Instruct user ---

echo ""
echo "🎉 iOS version bumped to $new_version ($new_build)"
echo ""
echo "📱 Next steps to release:"
echo "   1. Open Xcode:  ./sigo xcode"
echo "   2. Select 'iosApp' target"
echo "   3. Product → Archive"
echo "   4. Once archived, click 'Distribute App'"
echo "   5. Select 'App Store Connect' and follow the prompts"
