#!/bin/bash

# This script will encrypt or decrypt files using AES-256-GCM with strong key derivation.
# Safe for use in public repositories as long as:
# 1. The encryption key is never committed or shared
# 2. Only encrypted files are committed
# 3. The key is securely shared (e.g. via GitHub Secrets)
#
# Usage:
# ./crypt.sh encrypt --key <key> <file>
# ./crypt.sh decrypt --key <key> [file]  # If no file specified, decrypts all files in ./secrets
# ./crypt.sh decrypt [file]  # Uses SIGOT_DECRYPT_KEY from environment

# Explode on errors 💥
set -euo pipefail

# Folders
CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"

# Default output directories
SECRETS_DIR="$ROOT/secrets"
DECRYPT_DIR="$ROOT/.app/secrets"

GOOGLE_SERVICES_JSON_DECRYPT_PATH="$ROOT/apps/android"
GOOGLE_SERVICES_PLIST_DECRYPT_PATH="$ROOT/apps/ios/iosApp"

print_usage() {
    echo "Usage: ./sigo crypt <command> [options] [file]"
    echo
    echo "Commands:"
    echo "  encrypt    Encrypt a file"
    echo "  decrypt    Decrypt a file (or all files in $SECRETS_DIR if no file specified)"
    echo
    echo "Options:"
    echo "  --key      Encryption/decryption key"
    echo "  -h, --help Show this help message"
    echo
    echo "Security Notes:"
    echo "  - Never commit the encryption key"
    echo "  - Store the key in environment variables"
    echo "  - Only commit encrypted files"
    echo "  - Keep the key length at least 32 characters"
    echo
    echo "Examples:"
    echo "  # Encrypt a file (outputs to $SECRETS_DIR/config.json.enc)"
    echo "  ./backstage crypt encrypt --key \$CRYPT_KEY config.json"
    echo
    echo "  # Decrypt a specific file (outputs to $DECRYPT_DIR/config.json)"
    echo "  ./backstage crypt decrypt --key \$CRYPT_KEY ./secrets/config.json.enc"
    echo
    echo "  # Decrypt all files in $SECRETS_DIR"
    echo "  ./backstage crypt decrypt --key \$CRYPT_KEY"
    echo
    echo "  # Decrypt using SIGOT_DECRYPT_KEY from environment"
    echo "  SIGOT_DECRYPT_KEY=\$CRYPT_KEY ./backstage crypt decrypt"
    echo

    exit "${1:-0}"
}

# Validate key strength
validate_key() {
    local key="$1"
    if [ ${#key} -lt 32 ]; then
        echo "Error: Encryption key must be at least 32 characters long"
        exit 1
    fi
}

# Function to decrypt a single file
decrypt_file() {
    local input_file="$1"
    local key="$2"

    # Get just the filename from the path
    local filename
    filename=$(basename "$input_file")

    # Remove .enc extension if present
    if [[ $filename == *.enc ]]; then
        filename="${filename%.enc}"
    fi

    # Construct output path
    local output_path

    # Special handling for google-services.json
    if [[ $filename == "google-services.json" ]]; then
        output_path="$GOOGLE_SERVICES_JSON_DECRYPT_PATH/$filename"
        # Check if Android app directory exists
        if [[ ! -d "$GOOGLE_SERVICES_JSON_DECRYPT_PATH" ]]; then
            echo "Error: Android app directory '$GOOGLE_SERVICES_JSON_DECRYPT_PATH' does not exist"
            exit 1
        fi
    elif [[ $filename == "GoogleService-Info.plist" ]]; then
        output_path="$GOOGLE_SERVICES_PLIST_DECRYPT_PATH/$filename"
        if [[ ! -d "$GOOGLE_SERVICES_PLIST_DECRYPT_PATH" ]]; then
            echo "Error: iOS app directory '$GOOGLE_SERVICES_PLIST_DECRYPT_PATH' does not exist"
            exit 1
        fi
    else
        output_path="$DECRYPT_DIR/$filename"
    fi

    echo "🔐 Decrypting file: $input_file"
    echo "📝 Output will be written to: $output_path"

    # Common options for decryption
    local common_opts=(
        -aes-256-cbc
        -salt
        -in "$input_file"
        -out "$output_path"
        -k "$key"
        -pbkdf2
        -iter 500000
        -md sha512
    )

    openssl enc -d "${common_opts[@]}"
    echo "✅ Decryption complete for: $input_file"
}

# Show help if requested
if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
    print_usage
fi

# Parse command line arguments
COMMAND=""
FILE=""
key=""

while [[ $# -gt 0 ]]; do
    case "$1" in
    encrypt)
        COMMAND="encrypt"
        shift
        ;;
    decrypt)
        COMMAND="decrypt"
        shift
        ;;
    --key)
        shift
        if [[ $# -eq 0 ]]; then
            echo "Error: --key requires a value"
            print_usage 1
        fi
        key="$1"
        shift
        ;;
    -h | --help)
        print_usage
        ;;
    *)
        if [[ -z "$FILE" ]]; then
            FILE="$1"
        else
            echo "Error: Unexpected argument '$1'"
            print_usage 1
        fi
        shift
        ;;
    esac
done

# Validate arguments
if [[ -z "$COMMAND" ]]; then
    echo "Error: Command required (encrypt or decrypt)"
    print_usage 1
fi

# For encryption, file is required
if [[ $COMMAND = "encrypt" ]] && [[ -z "$FILE" ]]; then
    echo "Error: File path required for encryption"
    print_usage 1
fi

# If no key provided, try to get it from environment
if [[ -z "$key" ]]; then
    if [[ -n "${SIGOT_DECRYPT_KEY:-}" ]]; then
        key="$SIGOT_DECRYPT_KEY"
        echo "Using SIGOT_DECRYPT_KEY from environment"
    else
        # Check if we are in a CI environment
        if [[ -n "${CI:-}" || -n "${GITHUB_ACTIONS:-}" ]]; then
            echo "Error: --key is required or SIGOT_DECRYPT_KEY must be set in environment"
            print_usage 1
        else
            echo "No encryption key provided."
            read -r -s -p "Enter encryption key: " key
            echo
            if [[ -z "$key" ]]; then
                echo "Error: No key provided"
                exit 1
            fi
        fi
    fi
fi

# Create output directory if it doesn't exist
mkdir -p "$DECRYPT_DIR"

# Validate key strength
validate_key "$key"

if [ "$COMMAND" = "encrypt" ]; then
    # Get just the filename from the path
    FILENAME=$(basename "$FILE")

    # Add .enc extension if not already present
    if [[ ! $FILENAME == *.enc ]]; then
        FILENAME="${FILENAME}.enc"
    fi

    # Construct output path
    OUTPUT_PATH="$SECRETS_DIR/$FILENAME"

    # Create secrets directory if it doesn't exist
    mkdir -p "$SECRETS_DIR"

    # Check if output would overwrite input
    if [ "$FILE" = "$OUTPUT_PATH" ]; then
        echo "Error: Output file cannot be the same as input file"
        exit 1
    fi

    # Ensure we're not trying to encrypt an already encrypted file
    if [[ "$FILE" == *.enc ]]; then
        echo "Warning: Input file appears to be already encrypted (.enc extension)"
        read -p "Continue anyway? (y/N) " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi

    # Check if input file exists
    if [ ! -f "$FILE" ]; then
        echo "Error: Input file '$FILE' does not exist"
        exit 1
    fi

    echo "🔐 Encrypting file: $FILE"
    echo "📝 Output will be written to: $OUTPUT_PATH"

    # Common options for encryption
    common_opts=(
        -aes-256-cbc
        -salt
        -in "$FILE"
        -out "$OUTPUT_PATH"
        -k "$key"
        -pbkdf2
        -iter 500000
        -md sha512
    )

    openssl enc "${common_opts[@]}"
    echo "✅ Encryption complete!"
else
    # Decrypt mode
    if [[ -n "$FILE" ]]; then
        # Single file decryption
        decrypt_file "$FILE" "$key"
    else
        # Batch decryption of all files in secrets directory
        if [[ ! -d "$SECRETS_DIR" ]]; then
            echo "Error: $SECRETS_DIR directory does not exist"
            exit 1
        fi

        # Find all .enc files in the secrets directory
        enc_files=("$SECRETS_DIR"/*.enc)

        if [[ ! -e "${enc_files[0]}" ]]; then
            echo "No encrypted files found in $SECRETS_DIR"
            exit 0
        fi

        echo "🔐 Decrypting all files in $SECRETS_DIR"
        echo

        for file in "${enc_files[@]}"; do
            decrypt_file "$file" "$key"
            echo
        done

        echo "✅ All files decrypted successfully!"
    fi
fi
