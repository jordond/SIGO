#!/bin/bash

# This script decrypts the secrets files needed for the project
# Usage: init-secrets.sh [--key <key>]

# Explode on errors 💥
set -euo pipefail

CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

print_usage() {
    echo "Usage: init-secrets [options]"
    echo
    echo "Options:"
    echo "  --key <key>  Decryption key (can also be set via SIGOT_DECRYPT_KEY env var)"
    echo "  --help, -h   Show this help message"
    echo
    echo "This script will decrypt:"
    echo "  - secrets/secrets.properties.enc -> .app/secrets/secrets.gradle"
    echo "  - secrets/fastlane_service_account.json.enc -> .app/secrets/fastlane_service_account.json"
    exit 1
}

# Show help if requested
if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    print_usage
fi

# Get decryption key from args or env
key="${SIGOT_DECRYPT_KEY:-}"
while [ $# -gt 0 ]; do
    case "$1" in
    --key)
        shift
        key="$1"
        ;;
    *)
        echo "Error: Unknown option '$1'"
        print_usage
        ;;
    esac
    shift
done

if [ -z "$key" ]; then
    "$ROOT/scripts/crypt.sh" decrypt
else
    "$ROOT/scripts/crypt.sh" decrypt --key "$key"
fi

echo "✅ All secrets decrypted to $ROOT/.beans directory"
