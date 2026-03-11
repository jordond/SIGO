# Explode on errors 💥
set -euo pipefail

# Folders
CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

APP_ENV_PROPERTIES="$ROOT/app-env.properties"
SECRET_FORECAST_API_KEY="FORECAST_API_KEY"
WRANGLER_VARS="$ROOT/.dev.vars"
WRANGLER_VARS_SAMPLE="$ROOT/.dev.vars.sample"
WRANGLER_COMMAND="$ROOT/apps/api/worker/node_modules/.bin/wrangler"
API_SCRIPT="$ROOT/scripts/api.sh"
KJS_OUTPUT_FILE="$ROOT/apps/api/worker/build/compileSync/js/main/productionExecutable/kotlin/index.mjs"

print_usage() {
    echo "Usage: ./sigo api:worker <command> [options]"
    echo
    echo "Commands:"
    echo "  init                    Get the worker setup ready for dev or deploy"
    echo "  secret <check|set>      Check or set secrets for the worker"
    echo "  build [options]         Compile the worker code to Javascript"
    echo "    --clean                   Clean the build directory before building"
    echo "  dev                     Compile source and watch for changes, and run the worker in dev mode"
    echo "  deploy [options]        Deploy the worker to Cloudflare (default=prod)"
    echo "    --env <env>               Environment to deploy to (prod, staging, dev)"
    echo "    --no-clean                Skip cleaning before building"
    echo "    --all                     Deploy to all environments (prod, staging, dev)"
    echo "  update-wrangler         Update Wrangler to the latest version"
    echo "  wrangler [command]      Wrapper around Cloudflare's Wrangler"

    exit "${1:-0}"
}

# Show help if requested or no arguments provided
if [[ $# -eq 0 || "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    print_usage
fi

check_prerequisites() {
    if ! command -v node &>/dev/null || ! command -v npm &>/dev/null; then
        echo "❌ npm not found. Please install Node.js and npm first."
        exit 1
    fi

    if ! $WRANGLER_COMMAND --version >/dev/null; then
        echo "❌ Wrangler not found. Please run ./sigo init api:worker"
        exit 1
    fi

    # Check if WRANGLER_VARS exists and 'FORECAST_API_KEY' is set
    local api_key_set=false

    if [[ -f "$WRANGLER_VARS" ]]; then
        # Check if FORECAST_API_KEY is set and not empty
        if grep -q "^FORECAST_API_KEY=.\+" "$WRANGLER_VARS"; then
            api_key_set=true
        fi
    fi

    if [[ "$api_key_set" == false ]]; then
        echo "🔍 Setting up Wrangler environment variables..."

        # Check if APP_ENV_PROPERTIES exists and has FORECAST_API_KEY
        if [[ -f "$APP_ENV_PROPERTIES" ]] && grep -q "^$SECRET_FORECAST_API_KEY=.\+" "$APP_ENV_PROPERTIES"; then
            # Extract the API key from app-env.properties
            echo "🔍 Extracting $$SECRET_FORECAST_API_KEY from $APP_ENV_PROPERTIES..."

            local forecast_key
            forecast_key=$(grep "^$SECRET_FORECAST_API_KEY=" "$APP_ENV_PROPERTIES" | cut -d'=' -f2)

            if [[ -n "$forecast_key" ]]; then
                # Create .dev.vars from sample and set the API key
                if [[ -f "$WRANGLER_VARS" ]]; then
                    # Check if FORECAST_API_KEY line exists but is empty
                    if grep -q "^$SECRET_FORECAST_API_KEY=$" "$WRANGLER_VARS"; then
                        # Replace the empty line with the actual key
                        if [[ "$OSTYPE" == "darwin"* ]]; then
                            sed -i '' "s/^$SECRET_FORECAST_API_KEY=$/$SECRET_FORECAST_API_KEY=$forecast_key/" "$WRANGLER_VARS"
                        else
                            sed -i "s/^$SECRET_FORECAST_API_KEY=$/$SECRET_FORECAST_API_KEY=$forecast_key/" "$WRANGLER_VARS"
                        fi
                        echo "✅ Updated $SECRET_FORECAST_API_KEY in .dev.vars"
                    else
                        # Append the key if it doesn't exist at all
                        echo "$SECRET_FORECAST_API_KEY=$forecast_key" >>"$WRANGLER_VARS"
                        echo "✅ Appended $SECRET_FORECAST_API_KEY to .dev.vars"
                    fi
                else
                    cp "$WRANGLER_VARS_SAMPLE" "$WRANGLER_VARS"
                    # Replace the empty FORECAST_API_KEY with the actual key
                    if [[ "$OSTYPE" == "darwin"* ]]; then
                        sed -i '' "s/^$SECRET_FORECAST_API_KEY=$/$SECRET_FORECAST_API_KEY=$forecast_key/" "$WRANGLER_VARS"
                    else
                        sed -i "s/^$SECRET_FORECAST_API_KEY=$/$SECRET_FORECAST_API_KEY=$forecast_key/" "$WRANGLER_VARS"
                    fi
                    echo "✅ Created .dev.vars with FORECAST_API_KEY"
                fi
            else
                echo "❌ $SECRET_FORECAST_API_KEY found but empty in $APP_ENV_PROPERTIES"
                echo "Run './sigo init' to initialize the secrets first."
                exit 1
            fi
        else
            echo "❌ $SECRET_FORECAST_API_KEY not found in $APP_ENV_PROPERTIES"
            echo "Run './sigo init' to initialize the secrets first."
            exit 1
        fi
    fi

    echo "✅ Prerequisites check passed!"
}

initialize() {
    echo "🔍 Checking if node and npm are installed..."

    if ! command -v node &>/dev/null; then
        echo "❌ node not found. Please install and re-run this script."
        exit 1
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

check_secrets_for_env() {
    local env="$1"

    echo "🔍 Checking Cloudflare secrets for $env..."

    local secret_output
    if [[ "$env" != "prod" ]]; then
        secret_output=$("$WRANGLER_COMMAND" secret list --env "$env" 2>/dev/null || echo "[]")
    else
        secret_output=$("$WRANGLER_COMMAND" secret list 2>/dev/null || echo "[]")
    fi

    # Check if FORECAST_API_KEY exists in the output
    if ! echo "$secret_output" | grep -q "FORECAST_API_KEY"; then
        echo "❌ FORECAST_API_KEY secret not found in Cloudflare for $env"
        echo "💡 Please run the following command to set it:"
        if [[ "$env" != "prod" ]]; then
            echo "   ./sigo api:worker wrangler secret put FORECAST_API_KEY --env $env"
        else
            echo "   ./sigo api:worker wrangler secret put FORECAST_API_KEY"
        fi
        exit 1
    fi

    echo "✅ FORECAST_API_KEY secret found for $env"
}

deploy_to_env() {
    local env="$1"

    if [[ "$env" != "prod" ]]; then
        "$WRANGLER_COMMAND" deploy --env "$env"
    else
        "$WRANGLER_COMMAND" deploy
    fi
}

dev() {
    check_prerequisites

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
    "$ROOT/gradlew" :apps:api:worker:compileProductionExecutableKotlinJs --quiet --continuous &
    GRADLE_PID=$!
    echo "🔧 Gradle/JS build watcher started with PID $GRADLE_PID"

    # Wait for the initial build to complete by checking for the output file
    echo "⏳ Waiting for initial Kotlin/JS build to complete..."
    local wait_count=0
    local max_wait=60 # Maximum wait time in seconds

    while [[ ! -f "$KJS_OUTPUT_FILE" && $wait_count -lt $max_wait ]]; do
        sleep 1
        wait_count=$((wait_count + 1))
        if ((wait_count % 10 == 0)); then
            echo "⏳ Still waiting for build... (${wait_count}s elapsed)"
        fi
    done

    if [[ ! -f "$KJS_OUTPUT_FILE" ]]; then
        echo "❌ Initial build failed or timed out after ${max_wait}s"
        echo "💡 Check the Gradle build output for errors"
        cleanup
        exit 1
    fi

    echo "✅ Initial build complete!"

    echo "🚀 Starting Wrangler dev server..."
    echo "ℹ️ Press Ctrl+C to stop both processes"
    echo

    # 3. When wrangler exits (for any reason), cleanup will be called
    "$WRANGLER_COMMAND" dev
    cleanup
}

deploy() {
    local no_clean_flag="$1"
    local env="$2"
    local all_flag="$3"

    if [[ "$all_flag" == "true" ]]; then
        local environments=("prod" "staging" "dev")
        local first_deploy=true

        for target_env in "${environments[@]}"; do
            echo "🚀 Deploying to $target_env environment..."

            check_secrets_for_env "$target_env"

            # Build: clean only on first deploy if --no-clean is not specified
            if [[ "$first_deploy" == "true" ]]; then
                if [[ "$no_clean_flag" == "true" ]]; then
                    "$API_SCRIPT" build
                else
                    "$API_SCRIPT" build --clean
                fi
                first_deploy=false
            else
                "$API_SCRIPT" build
            fi

            deploy_to_env "$target_env"

            echo "✅ Successfully deployed to $target_env"
            echo
        done

        echo "🎉 All environments deployed successfully!"
        return
    else
        # Single environment deployment
        check_secrets_for_env "$env"

        if [[ "$no_clean_flag" == "true" ]]; then
            "$API_SCRIPT" build
        else
            "$API_SCRIPT" build --clean
        fi

        deploy_to_env "$env"
    fi
}

build() {
    local clean_flag="$1"

    if [[ "$clean_flag" == "true" ]]; then
        "$API_SCRIPT" build --clean
    else
        "$API_SCRIPT" build
    fi
}

# Parse command line arguments
COMMAND=""
ENV="prod"
NO_CLEAN_FLAG="false"
ALL_FLAG="false"
WRANGLER_ARGS=()

while [[ $# -gt 0 ]]; do
    case "$1" in
    init)
        COMMAND="init"
        shift
        ;;
    secret)
        COMMAND="secret"
        shift
        WRANGLER_ARGS=("$@")
        break
        ;;
    build)
        COMMAND="build"
        shift
        # SEnd remaining args to build script
        "$API_SCRIPT" build "$@"
        exit $?
        ;;
    dev)
        COMMAND="dev"
        shift
        ;;
    update-wrangler)
        COMMAND="update-wrangler"
        shift
        ;;
    deploy)
        COMMAND="deploy"
        shift
        # Check for --no-clean flag and environment
        while [[ $# -gt 0 ]]; do
            case "$1" in
            --no-clean)
                NO_CLEAN_FLAG="true"
                shift
                ;;
            --all)
                ALL_FLAG="true"
                shift
                ;;
            --env)
                shift
                if [[ $# -eq 0 ]]; then
                    echo "❌ Error: --env requires an argument"
                    print_usage 1
                fi
                ENV="$1"
                shift
                ;;
            *)
                echo "❌ Error: Unknown deploy option '$1'"
                print_usage 1
                ;;
            esac
        done
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
init)
    echo "🚀 Initializing API worker..."
    initialize
    ;;
secret)
    echo "🤫 Managing secrets..."
    # Check for WRANGLER_ARGS for a subcommand check|set
    if [[ ${#WRANGLER_ARGS[@]} -eq 0 ]]; then
        echo "❌ Error: Secret command requires 'check' or 'set' subcommand"
        echo "Usage: ./sigo api:worker secret <check|set>"
        exit 1
    fi

    subcommand="${WRANGLER_ARGS[0]}"

    case "$subcommand" in
    check)
        echo "🔍 Checking for $SECRET_FORECAST_API_KEY secret..."
        secret_output=$("$WRANGLER_COMMAND" secret list 2>/dev/null || echo "[]")

        if echo "$secret_output" | grep -q "$SECRET_FORECAST_API_KEY"; then
            echo "✅ $SECRET_FORECAST_API_KEY secret found in Cloudflare"
        else
            echo "❌ $SECRET_FORECAST_API_KEY secret not found in Cloudflare"
            echo "💡 Run './sigo api:worker secret set' to add it"
            exit 1
        fi
        ;;
    set)
        echo "🔐 Setting $SECRET_FORECAST_API_KEY secret..."
        "$WRANGLER_COMMAND" secret put "$SECRET_FORECAST_API_KEY"
        ;;
    *)
        echo "❌ Error: Unknown secret subcommand '$subcommand'"
        echo "Usage: ./sigo api:worker secret <check|set>"
        exit 1
        ;;
    esac
    ;;
dev)
    echo "🔥 Starting API worker in development mode..."
    dev
    ;;
deploy)
    echo "🚀 Deploying API worker..."
    if [[ "$ALL_FLAG" == "true" ]]; then
        echo "🌍 Deploying to all environments (prod, staging, dev)"
    elif [[ "$ENV" == "prod" ]]; then
        echo "🌍 Deploying to Production"
    else
        echo "🌍 Deploying to '$ENV' environment"
    fi

    deploy "$NO_CLEAN_FLAG" "$ENV" "$ALL_FLAG"
    ;;
update-wrangler)
    WORKER_DIR="$ROOT/apps/api/worker"
    echo "📦 Updating Wrangler..."
    cd "$WORKER_DIR"
    npm i -D wrangler@latest
    cd "$ROOT"
    echo "✅ Wrangler updated to $($WRANGLER_COMMAND --version)"
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
