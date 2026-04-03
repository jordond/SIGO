#!/bin/bash

# Release script for the API worker (Cloudflare Workers)
# Bumps version, commits, tags, and optionally deploys.
# Usage: ./sigo release:api [--version 1.0.1 | --semver patch] [flags]

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
NO_BRANCH_CHECK=false
NO_DEPLOY=false
NO_CLEAN=false
AUTO_YES=false
DRY_RUN=false
DEPLOY_ENV="prod"
DEPLOY_ALL=false

print_usage() {
    echo "Usage: release:api [options]"
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
    echo "      --no-deploy           Skip deploying to Cloudflare"
    echo "      --no-clean            Skip clean before building"
    echo "      --no-branch-check     Skip branch verification"
    echo "      --deploy-env <env>    Deploy environment (default: prod)"
    echo "      --deploy-all          Deploy to all environments (prod, staging, dev)"
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
    --no-deploy)
        NO_DEPLOY=true
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
    --deploy-env)
        [[ $# -lt 2 ]] && {
            echo "Error: --deploy-env requires a value"
            exit 1
        }
        DEPLOY_ENV="$2"
        shift 2
        ;;
    --deploy-all)
        DEPLOY_ALL=true
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

# Require version argument
if [[ -z "$VERSION" && -z "$SEMVER" ]]; then
    print_usage
    exit 1
fi

if [[ -n "$VERSION" && -n "$SEMVER" ]]; then
    echo "Error: Cannot use both --version and --semver"
    exit 1
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

current_version=$(read_toml_value "api-server-version")

echo "📋 Current API version: $current_version"

if [[ -n "$VERSION" ]]; then
    if ! [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        echo "Error: Version must be in format X.Y.Z (got '$VERSION')"
        exit 1
    fi
    new_version="$VERSION"
else
    new_version=$(bump_version "$current_version" "$SEMVER")
fi

# --- Summary ---

deploy_summary="skip"
if [[ "$NO_DEPLOY" == false ]]; then
    if [[ "$DEPLOY_ALL" == true ]]; then
        deploy_summary="all (prod, staging, dev)"
    else
        deploy_summary="$DEPLOY_ENV"
    fi
fi

echo ""
echo "━━━ Release Summary ━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "  Platform:     API Worker"
echo "  Version:      $current_version → $new_version"
echo "  Clean build:  $([[ "$NO_CLEAN" == false ]] && echo "yes" || echo "no")"
echo "  Git commit:   $([[ "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
echo "  Git tag:      $([[ "$NO_TAG" == false && "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
echo "  Git push:     $([[ "$NO_PUSH" == false && "$NO_COMMIT" == false ]] && echo "yes" || echo "skip")"
echo "  Deploy:       $deploy_summary"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
check_dry_run
confirm_or_exit "Proceed with release?"

# --- Step 2: Update version in libs.versions.toml ---

echo "📝 Updating version in gradle/libs.versions.toml..."
write_toml_value "api-server-version" "$new_version"
echo "   api-server-version = \"$new_version\""
echo "✅ Updated gradle/libs.versions.toml"

# Restore TOML on build failure
trap 'echo "⚠️  Build failed — reverting version bump"; git -C "$ROOT" checkout -- "$TOML"' ERR

# --- Step 3: Build ---

echo "🔨 Building API worker..."
if [[ "$NO_CLEAN" == false ]]; then
    "$ROOT"/scripts/api.sh build --clean
else
    "$ROOT"/scripts/api.sh build
fi
echo "✅ Build complete"

# Build succeeded — remove the rollback trap
trap - ERR

# --- Step 4: Commit, tag, and push ---

if [[ "$NO_COMMIT" == false ]]; then
    echo "💾 Staging and committing version bump..."
    git -C "$ROOT" add "$TOML"
    git -C "$ROOT" commit -m "bump for api release ${new_version} [skip-ci]"
    echo "✅ Committed version bump"

    if [[ "$NO_TAG" == false ]]; then
        echo "🏷️  Creating tag..."
        tag="release/api/${new_version}"
        if git -C "$ROOT" tag -l "$tag" | grep -q .; then
            echo "   $tag (overwriting)"
        else
            echo "   $tag"
        fi
        git -C "$ROOT" tag -f "$tag"
        echo "✅ Tag created"
    fi

    if [[ "$NO_PUSH" == false ]]; then
        echo "🚀 Pushing commit to remote..."
        git -C "$ROOT" push
        if [[ "$NO_TAG" == false ]]; then
            echo "🚀 Pushing tag to remote..."
            git -C "$ROOT" push --force origin "release/api/${new_version}"
        fi
        echo "✅ Pushed to remote"
    fi
fi

# --- Step 5: Deploy ---

if [[ "$NO_DEPLOY" == false ]]; then
    echo ""
    confirm_or_exit "Deploy API worker to ${deploy_summary}?"

    if [[ "$DEPLOY_ALL" == true ]]; then
        "$ROOT"/scripts/api-worker.sh deploy --all --skip-build
    else
        "$ROOT"/scripts/api-worker.sh deploy --env "$DEPLOY_ENV" --skip-build
    fi
    echo "✅ Deploy complete"
fi

echo ""
echo "🎉 API release $new_version complete!"
