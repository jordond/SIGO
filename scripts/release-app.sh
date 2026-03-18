#!/bin/bash

# Combined release script for Android + iOS
# Usage: ./sigo release:app [--semver patch] [flags]

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." >/dev/null 2>&1 && pwd)"
TOML="$ROOT/gradle/libs.versions.toml"
PBXPROJ="$ROOT/apps/ios/iosApp.xcodeproj/project.pbxproj"

# Collect all args to forward
ARGS=()
NO_COMMIT=false
NO_PUSH=false
NO_TAG=false
AUTO_YES=false
DRY_RUN=false
HAS_VERSION_ARG=false
SEMVER_LEVEL=""

print_usage() {
    echo "Usage: release:app [options]"
    echo ""
    echo "Options:"
    echo "  -s, --semver <level>      Bump version: major, minor, patch, none"
    echo "  -y, --yes                 Skip confirmation prompts"
    echo "  -n, --dry-run             Show what would happen without making changes"
    echo "      --no-tag              Skip creating git tags"
    echo "      --no-commit           Skip creating git commit"
    echo "      --no-push             Skip pushing to remote"
    echo "      --no-git              Skip both tagging and committing"
    echo "      --no-clean            Skip clean before building (Android)"
    echo "      --no-branch-check     Skip branch verification"
    echo "  -o, --output <dir>        Output directory for AAB (Android, default: ./release)"
    echo "  -h, --help                Show this help"
    echo ""
    echo "--semver is required. Use --version with platform-specific scripts instead."
}

# Parse to detect git flags, forward everything
while [[ $# -gt 0 ]]; do
    case "$1" in
    -v | --version)
        echo "Error: --version is not supported for combined releases (platforms may differ)"
        echo "Use --semver instead, or run release:app:android and release:app:ios separately"
        exit 1
        ;;
    -s | --semver)
        HAS_VERSION_ARG=true
        SEMVER_LEVEL="$2"
        ARGS+=("$1" "$2")
        shift 2
        ;;
    --no-commit)
        NO_COMMIT=true
        ARGS+=("$1")
        shift
        ;;
    --no-push)
        NO_PUSH=true
        ARGS+=("$1")
        shift
        ;;
    --no-tag)
        NO_TAG=true
        ARGS+=("$1")
        shift
        ;;
    --no-git)
        NO_COMMIT=true
        NO_TAG=true
        ARGS+=("$1")
        shift
        ;;
    -y | --yes)
        AUTO_YES=true
        ARGS+=("$1")
        shift
        ;;
    -n | --dry-run)
        DRY_RUN=true
        ARGS+=("$1")
        shift
        ;;
    -h | --help)
        print_usage
        exit 0
        ;;
    *)
        ARGS+=("$1")
        shift
        ;;
    esac
done

if [[ "$HAS_VERSION_ARG" == false ]]; then
    echo "Error: --semver is required"
    echo ""
    print_usage
    exit 1
fi

source "$ROOT/scripts/release-common.sh"

if [[ "$NO_COMMIT" == false ]]; then
    check_clean_worktree
fi

# --- Pre-flight: read current versions for summary ---

current_android_version=$(read_toml_value "app-android-version")
current_android_code=$(read_toml_value "app-android-code")
current_ios_version=$(read_toml_value "app-ios-version")

new_android_version=$(bump_version "$current_android_version" "$SEMVER_LEVEL")
new_android_code=$((current_android_code + 1))
new_ios_version=$(bump_version "$current_ios_version" "$SEMVER_LEVEL")

echo ""
echo "━━━ Combined Release Summary ━━━━━━━━━━━━━━━━━━"
echo "  Android version: $current_android_version → $new_android_version (code $current_android_code → $new_android_code)"
echo "  iOS version:     $current_ios_version → $new_ios_version"
echo "  Git commit:      $([[ "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
echo "  Git tag:         $([[ "$NO_TAG" == false && "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
echo "  Git push:        $([[ "$NO_PUSH" == false && "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
check_dry_run
confirm_or_exit "Proceed with combined release?"

echo "📦 Starting combined Android + iOS release..."
echo ""

# Run both with --combined-commit so they skip individual commit/push (and --yes to skip sub-prompts)
echo "━━━ Android ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
"$ROOT"/scripts/release-android.sh "${ARGS[@]}" --combined-commit --yes
echo ""

echo "━━━ iOS ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
# Filter out Android-only flags that the iOS script does not accept
IOS_ARGS=()
skip_next=false
for arg in "${ARGS[@]}"; do
    if [[ "$skip_next" == true ]]; then
        skip_next=false
        continue
    fi
    case "$arg" in
    -o | --output)
        skip_next=true
        continue
        ;;
    --no-clean)
        continue
        ;;
    *)
        IOS_ARGS+=("$arg")
        ;;
    esac
done
"$ROOT"/scripts/release-ios.sh "${IOS_ARGS[@]}" --combined-commit --yes
echo ""

# --- Combined commit ---

if [[ "$NO_COMMIT" == false ]]; then
    # Read the new versions for the commit message
    android_version=$(grep "^app-android-version = " "$TOML" | sed 's/.*= *"\(.*\)"/\1/')
    android_code=$(grep "^app-android-code = " "$TOML" | sed 's/.*= *"\(.*\)"/\1/')
    ios_version=$(grep "^app-ios-version = " "$TOML" | sed 's/.*= *"\(.*\)"/\1/')
    ios_build=$(grep "CURRENT_PROJECT_VERSION = " "$PBXPROJ" | sed 's/.*= *\([0-9]*\).*/\1/' | sort -u | head -1)

    echo "━━━ Git ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

    echo "💾 Staging and committing version bumps..."
    git -C "$ROOT" add "$TOML" "$PBXPROJ"
    git -C "$ROOT" commit -m "bump for release android ${android_version} (${android_code}), ios ${ios_version} (${ios_build}) [skip-ci]"
    echo "✅ Committed version bump"

    if [[ "$NO_TAG" == false ]]; then
        echo "🏷️  Creating tags..."
        tags=("android/build/${android_code}" "release/android/${android_version}" "ios/build/${ios_build}" "release/ios/${ios_version}")
        for tag in "${tags[@]}"; do
            if git -C "$ROOT" tag -l "$tag" | grep -q .; then
                echo "Error: Tag '$tag' already exists"
                exit 1
            fi
            echo "   $tag"
        done
        for tag in "${tags[@]}"; do
            git -C "$ROOT" tag "$tag"
        done
        echo "✅ Tags created"
    fi

    if [[ "$NO_PUSH" == false ]]; then
        echo "🚀 Pushing commit to remote..."
        git -C "$ROOT" push
        if [[ "$NO_TAG" == false ]]; then
            echo "🚀 Pushing tags to remote..."
            git -C "$ROOT" push origin "${tags[@]}"
        fi
        echo "✅ Pushed to remote"
    fi
fi

echo ""
echo "🎉 Combined release complete!"
