# Explode on errors 💥
set -euo pipefail

# Folders
CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

KJS_BUILD_COMMAND="$ROOT/gradlew :apps:api:worker:compileProductionExecutableKotlinJs"
KJS_OUTPUT_FILE="$ROOT/apps/api/worker/build/compileSync/js/main/productionExecutable/kotlin/index.mjs"

print_usage() {
    echo "Usage: ./sigo api <command> [options]"
    echo
    echo "Commands:"
    echo "  build [options]         Compile the Kotlin/JS code"
    echo "    --clean                   Clean the build directory before building"

    exit "${1:-0}"
}

# Show help if requested or no arguments provided
if [[ $# -eq 0 || "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    print_usage
fi

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
            shift
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
BUILD_ARGS=()

while [[ $# -gt 0 ]]; do
    case "$1" in
    build)
        COMMAND="build"
        shift
        # Collect build-specific arguments
        while [[ $# -gt 0 ]] && { [[ "$1" != -* ]] || [[ "$1" == "--clean" ]]; }; do
            BUILD_ARGS+=("$1")
            shift
        done
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
    echo "🔨 Building API..."
    if [[ ${#BUILD_ARGS[@]} -eq 0 ]]; then
        build
    else
        build "${BUILD_ARGS[@]}"
    fi
    ;;
esac
