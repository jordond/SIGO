# Explode on errors 💥
set -euo pipefail

# Folders
CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

KJS_BUILD_COMMAND="$ROOT/gradlew :apps:api:worker:compileProductionExecutableKotlinJs"
KJS_OUTPUT_FILE="$ROOT/apps/api/worker/build/compileSync/js/main/productionExecutable/kotlin/index.mjs"

print_usage() {
    echo "Usage: ./sigot api:worker <command> [options]"
    echo
    echo "Commands:"
    echo "  init                    Get the worker setup ready for dev or deploy"
    echo "  build                   Compile the worker code to Javascript"
    echo "  dev                     Compile source and watch for changes, and run the worker in dev mode"
    echo "  deploy [env]            Deploy the worker to Cloudflare with specified env (default=prod)"
    echo "    - prod,staging,dev"
    echo "  wrangler [command]      Wrapper around Cloudflare's Wrangler"

    exit "${1:-0}"
}

# Show help if requested or no arguments provided
if [[ $# -eq 0 || "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    print_usage
fi

initialize() {
    echo "🔍 Checking if npm is installed..."

    if ! command -v npm &>/dev/null; then
        echo "❌ npm not found. Attempting to use nvm..."
        if ! command -v nvm &>/dev/null; then
            echo "❌ nvm not found. Please install Node.js and npm first."
            exit 1
        fi

        echo "🔄 Running 'nvm use latest'..."
        if ! nvm use latest >/dev/null; then
            echo "⚠️ nvm use latest failed. Trying to install latest Node.js version..."
            if ! nvm install latest >/dev/null; then
                echo "❌ Failed to install Node.js via nvm. Please install Node.js manually."
                exit 1
            fi
        fi

        if ! command -v npm &>/dev/null; then
            echo "❌ npm still not available after nvm setup."
            exit 1
        fi
    fi

    echo "✅ npm found: $(npm --version)"

    WORKER_DIR="$ROOT/apps/api/worker"
    cd "$WORKER_DIR"
    echo "📦 Installing npm dependencies..."
    if ! npm install >/dev/null; then
        echo "❌ npm install failed"
        exit 1
    fi

    cd "$ROOT"
    echo "✅ API worker initialization complete!"
}

build() {
    local clean_flag=false

    while [[ $# -gt 0 ]]; do
        case "$1" in
        --clean)
            clean_flag=true
            shift
            ;;
        *)
            echo "⚠️ Warning: Unknown build option '$1'"
            ;;
        esac
    done

    # 0. If --clean is passed run ./gradlew clean first
    if [[ "$clean_flag" == true ]]; then
        echo "🧹 Running gradle clean..."
        if ! ./gradlew clean >/dev/null; then
            echo "❌ Gradle clean failed"
            exit 1
        fi
        echo "✅ Clean complete"
    fi

    echo "🔨 Compiling Kotlin/JS worker..."
    if ! $KJS_BUILD_COMMAND >/dev/null; then
        echo "❌ Kotlin/JS compilation failed"
        exit 1
    fi

    if [[ ! -f "$KJS_OUTPUT_FILE" ]]; then
        echo "❌ Expected output file not found: $KJS_OUTPUT_FILE"
        exit 1
    fi

    echo "✅ Build complete! Output: ${KJS_OUTPUT_FILE#"$ROOT/"}"
}

# Parse command line arguments
COMMAND=""
ENV="prod"
WRANGLER_ARGS=()
BUILD_ARGS=()

while [[ $# -gt 0 ]]; do
    case "$1" in
    init)
        COMMAND="init"
        shift
        ;;
    build)
        COMMAND="build"
        shift
        # Collect build-specific arguments
        while [[ $# -gt 0 ]] && { [[ "$1" != -* ]] || [[ "$1" == "--clean" ]]; }; do
            BUILD_ARGS+=("$1")
            shift
        done
        ;;
    dev)
        COMMAND="dev"
        shift
        ;;
    deploy)
        COMMAND="deploy"
        shift
        # Check if next argument is an environment
        if [[ $# -gt 0 && "$1" =~ ^(prod|staging|dev)$ ]]; then
            ENV="$1"
            shift
        fi
        ;;
    wrangler)
        COMMAND="wrangler"
        shift
        # Collect all remaining arguments for wrangler
        WRANGLER_ARGS=("$@")
        break
        ;;
    -h | --help)
        print_usage
        ;;
    *)
        echo "Error: Unknown command '$1'"
        print_usage 1
        ;;
    esac
done

# Validate that a command was provided
if [[ -z "$COMMAND" ]]; then
    echo "Error: Command required"
    print_usage 1
fi

# Execute the appropriate command
case "$COMMAND" in
build)
    echo "🔨 Building API worker..."
    if [[ ${#BUILD_ARGS[@]} -eq 0 ]]; then
        build
    else
        build "${BUILD_ARGS[@]}"
    fi
    ;;
init)
    echo "🚀 Initializing API worker..."
    initialize
    ;;
dev)
    echo "🔥 Starting API worker in development mode..."
    # TODO: Add dev mode logic
    ;;
deploy)
    echo "🚀 Deploying API worker to environment: $ENV"
    # TODO: Add deployment logic
    ;;
wrangler)
    echo "🔧 Running Wrangler with arguments: ${WRANGLER_ARGS[*]}"
    # TODO: Add wrangler wrapper logic
    ;;
esac
