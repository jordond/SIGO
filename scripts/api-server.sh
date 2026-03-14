# Explode on errors 💥
set -euo pipefail

# Folders
CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

DOCKERFILE="$ROOT/apps/api/server/Dockerfile"
IMAGE_NAME="sigo-api"
DEFAULT_PORT=8080

INSTALL_DIR="$ROOT/apps/api/server/build/install/server"

print_usage() {
    echo "Usage: ./sigo api:server <command> [options]"
    echo
    echo "Commands:"
    echo "  dev [options]           Run the JVM server locally via Gradle"
    echo "    --port <port>             Override the server port (default: $DEFAULT_PORT)"
    echo "  build [options]         Build the server distribution"
    echo "    --clean                   Clean before building"
    echo "  run [options]           Run the built distribution (build first)"
    echo "    --port <port>             Override the server port (default: $DEFAULT_PORT)"
    echo "  docker build [options]  Build the Docker image"
    echo "    --tag <tag>               Image tag (default: $IMAGE_NAME)"
    echo "  docker run [options]    Run the Docker container"
    echo "    --port <port>             Host port to bind (default: $DEFAULT_PORT)"
    echo "    --tag <tag>               Image tag to run (default: $IMAGE_NAME)"

    exit "${1:-0}"
}

# Show help if requested or no arguments provided
if [[ $# -eq 0 || "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    print_usage
fi

cmd_dev() {
    local port="$DEFAULT_PORT"

    while [[ $# -gt 0 ]]; do
        case "$1" in
        --port)
            port="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            print_usage 1
            ;;
        esac
    done

    echo "Starting JVM server on port $port..."
    PORT="$port" "$ROOT"/gradlew :apps:api:server:run
}

cmd_build() {
    local clean=false

    while [[ $# -gt 0 ]]; do
        case "$1" in
        --clean)
            clean=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            print_usage 1
            ;;
        esac
    done

    if $clean; then
        echo "Cleaning..."
        "$ROOT"/gradlew :apps:api:server:clean
    fi

    echo "Building server distribution..."
    "$ROOT"/gradlew :apps:api:server:installDist
}

cmd_run() {
    local port="$DEFAULT_PORT"

    while [[ $# -gt 0 ]]; do
        case "$1" in
        --port)
            port="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            print_usage 1
            ;;
        esac
    done

    if [[ ! -x "$INSTALL_DIR/bin/server" ]]; then
        echo "Distribution not found. Building..."
        cmd_build
    fi

    echo "Starting server on port $port..."
    PORT="$port" "$INSTALL_DIR/bin/server"
}

cmd_docker_build() {
    local tag="$IMAGE_NAME"

    while [[ $# -gt 0 ]]; do
        case "$1" in
        --tag)
            tag="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            print_usage 1
            ;;
        esac
    done

    echo "Building Docker image: $tag"
    docker build -f "$DOCKERFILE" -t "$tag" "$ROOT"
}

cmd_docker_run() {
    local port="$DEFAULT_PORT"
    local tag="$IMAGE_NAME"

    while [[ $# -gt 0 ]]; do
        case "$1" in
        --port)
            port="$2"
            shift 2
            ;;
        --tag)
            tag="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            print_usage 1
            ;;
        esac
    done

    echo "Running $tag on port $port..."
    docker run -p "$port:8080" "$tag"
}

command="$1"
shift

case "$command" in
dev)
    cmd_dev "$@"
    ;;
build)
    cmd_build "$@"
    ;;
run)
    cmd_run "$@"
    ;;
docker)
    if [[ $# -eq 0 ]]; then
        echo "Missing docker subcommand (build|run)"
        print_usage 1
    fi
    subcommand="$1"
    shift
    case "$subcommand" in
    build)
        cmd_docker_build "$@"
        ;;
    run)
        cmd_docker_run "$@"
        ;;
    *)
        echo "Unknown docker subcommand: $subcommand"
        print_usage 1
        ;;
    esac
    ;;
*)
    echo "Unknown command: $command"
    print_usage 1
    ;;
esac
