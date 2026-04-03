#!/bin/bash

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." >/dev/null 2>&1 && pwd)"
WEBSITE_DIR="$ROOT/website"

print_usage() {
    echo "Usage: ./sigo release:website [options]"
    echo ""
    echo "Install dependencies, build, and deploy the website to Cloudflare."
    echo ""
    echo "Options:"
    echo "    --env <env>   Target environment: prod (default) or staging"
    echo "    --dry-run     Run wrangler deploy in dry-run mode"
    echo "    --help        Show this help message"
}

ENV="prod"
DRY_RUN=false

while [[ $# -gt 0 ]]; do
    case "$1" in
    --help)
        print_usage
        exit 0
        ;;
    --env)
        [[ $# -lt 2 ]] && {
            echo "Error: --env requires a value"
            exit 1
        }
        ENV="$2"
        shift 2
        ;;
    --dry-run)
        DRY_RUN=true
        shift
        ;;
    *)
        echo "Unknown option: $1"
        exit 1
        ;;
    esac
done

if [[ "$ENV" != "prod" && "$ENV" != "staging" ]]; then
    echo "Error: --env must be 'prod' or 'staging', got '$ENV'"
    exit 1
fi

if [[ "$DRY_RUN" == true ]]; then
    echo "🌐 Dry-run deploying website to Cloudflare ($ENV)..."
else
    echo "🌐 Deploying website to Cloudflare ($ENV)..."
fi

DEPLOY_SCRIPT="deploy"
[[ "$ENV" == "staging" ]] && DEPLOY_SCRIPT="deploy:staging"

echo "📦 Installing dependencies..."
(cd "$WEBSITE_DIR" && pnpm install)

echo "🚀 Building and deploying..."
if [[ "$DRY_RUN" == true ]]; then
    (cd "$WEBSITE_DIR" && pnpm run build)
    (cd "$WEBSITE_DIR" && pnpm exec wrangler deploy --env "$ENV" --config ./wrangler.jsonc --dry-run)
    echo "✅ Dry-run complete for $ENV"
else
    (cd "$WEBSITE_DIR" && pnpm run "$DEPLOY_SCRIPT")
    echo "✅ Website deployed to $ENV successfully"
fi
