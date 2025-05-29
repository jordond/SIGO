# Explode on errors 💥
set -euo pipefail

# Folders
CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

WRANGLER_COMMAND="$ROOT/apps/api/worker/node_modules/.bin/wrangler"
KJS_BUILD_COMMAND="$ROOT/gradlew :apps:api:worker:compileProductionExecutableKotlinJs"
KJS_OUTPUT_FILE="$ROOT/apps/api/worker/build/compileSync/js/main/productionExecutable/kotlin/index.mjs"

print_usage() {
    echo "Usage: ./sigot api:worker <command> [options]"
    echo
    echo "Commands:"
    echo "  init                    Get the worker setup ready for dev or deploy"
    echo "  build [options]         Compile the worker code to Javascript"
    echo "    --clean                   Clean the build directory before building"
    echo "  dev                     Compile source and watch for changes, and run the worker in dev mode"
    echo "  deploy [env]            Deploy the worker to Cloudflare with specified env (default=empty)"
    echo "    -staging,dev"
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

    echo "🔍 Checking if Wrangler is installed..."
    if ! $WRANGLER_COMMAND --version >/dev/null; then
        echo "❌ Wrangler not found. Something went wrong..."
        exit 1
    fi

    echo "🎉 API worker initialization complete!"
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

dev() {
    # Trap to handle cleanup on exit/interrupt
    cleanup() {
        echo
        if [[ -n "${GRADLE_PID:-}" ]]; then
            echo "🔨 Stopping Kotlin/JS build watcher..."
            # Kill the entire process group to ensure gradle and its children are terminated
            kill -TERM "$GRADLE_PID" >/dev/null 2>&1 || true
            sleep 3
            # Force kill if still running
            kill -KILL "$GRADLE_PID" >/dev/null 2>&1 || true

            GRADLE_PID=""
            echo "✅ Kotlin/JS build watcher stopped"
            echo "👋 Goodbye!"
        fi
        exit 0
    }

    trap cleanup EXIT INT TERM

    echo "🔨 Starting Kotlin/JS build watcher..."
    $KJS_BUILD_COMMAND --quiet --continuous &
    GRADLE_PID=$!
    echo "🔧 Gradle/JS build watcher started with PID $GRADLE_PID"

    echo "⏳ Delaying for 5 seconds to let gradle start..."
    sleep 5

    echo "🚀 Starting Wrangler dev server..."
    echo "ℹ️ Press Ctrl+C to stop both processes"
    echo

    # 3. When wrangler exits (for any reason), cleanup will be called
    "$WRANGLER_COMMAND" dev
    cleanup
}

# Parse command line arguments
COMMAND=""
ENV=""
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
        echo "❌ Error: Unknown command '$1'"
        print_usage 1
        ;;
    esac
done

# Validate that a command was provided
if [[ -z "$COMMAND" ]]; then
    echo "❌ Error: Command required"
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
    dev
    ;;
deploy)
    echo "🚀 Deploying API worker..."
    if [[ -n "$ENV" ]]; then
        echo "🌍 Deploying to environment: $ENV"
    fi

    if [[ -n "$ENV" ]]; then
        "$WRANGLER_COMMAND" deploy --env "$ENV"
    else
        "$WRANGLER_COMMAND" deploy
    fi
    ;;
wrangler)
    if [[ ${#WRANGLER_ARGS[@]} -eq 0 ]]; then
        echo "🔧 Running Wrangler..."
        "$WRANGLER_COMMAND"
    else
        echo "🔧 Running Wrangler with arguments: ${WRANGLER_ARGS[*]}"
        "$WRANGLER_COMMAND" "${WRANGLER_ARGS[@]}"
    fi
    ;;
esac
