#!/bin/bash

# This script will install ktlint.sh to the project and add a pre-commit hook
# to run ktlint.sh on staged files before committing. This will ensure that all
# code is formatted correctly before being committed.
#
# For usage run `bootstrap --help`

# Explode on errors 💥
set -euo pipefail

# Folders
CWD="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
ROOT="$(cd "$CWD"/.. >/dev/null 2>&1 && pwd)"
SCRIPT_DIR="$ROOT/scripts"

#######################
####   CHANGE ME   ####

REPO="pinterest/ktlint"
DOWNLOAD_URL="https://github.com/pinterest/ktlint/releases/download"

# Specify a version of ktlint.sh to install, use 'latest' to fetch latest version
# latest - Fetch the latest version from github
# 1.3.1 - Fetch specific version from github
KTLINT_VERSION="latest"

# Default version to use in CI if `--version` is not used
CI_KTLINT_VERSION="1.5.0"

# ktlint.sh helper script path
KTLINT_SCRIPT="$SCRIPT_DIR/ktlint.sh"
KTLINT_PATH="$ROOT/.app"
#######################

# Colors
NC='\033[0m' # No Color
RED='\033[0;31m'
CYAN='\033[0;36m'

# Options
update_ktlint=false
is_ci=false
ktlint_version=$KTLINT_VERSION
set_ktlint_version=false
apply_idea=false

error() {
    echo -e "${RED}ERROR: ${NC}$1"
    exit 1
}

# Get the currently installed ktlint version if available
get_installed_version() {
    local version_file="$KTLINT_PATH/ktlint.version"
    if [[ -f "$version_file" ]]; then
        cat "$version_file"
        return 0
    fi
    echo ""
    return 1
}

# Parse CLI options
while test $# -gt 0; do
    case "$1" in
    -h | --help)
        echo "init-ktlint: Install ktlint"
        echo " "
        echo "Usage:"
        echo "    init-ktlint [options]"
        echo " "
        echo "Options:"
        echo "    -h, --help       Show this help message"
        echo "    -u, --update     Update ktlint if it is already installed"
        echo "    --version        The specific version of ktlint to use (default: $KTLINT_VERSION)"
        echo "    --ci             Force the script to run as if it were in a CI environment"
        echo "    --idea           Apply ktlint styles to Android Studio"
        exit 0
        ;;
    -u | --update)
        update_ktlint=true
        shift
        ;;
    --ci)
        is_ci=true
        shift
        ;;
    --version)
        shift
        ktlint_version=$1
        set_ktlint_version=true
        shift
        ;;
    --idea)
        apply_idea=true
        shift
        ;;
    *)
        break
        ;;
    esac
done

print_options() {
    echo "update_ktlint=$update_ktlint"
    echo "is_ci=$is_ci"
    echo "ktlint_version=$ktlint_version"
    echo "apply_idea=$apply_idea"
    exit 1
}

# Detect if we are in a CI environment
# $CI should be set by most CI, Github Actions does
if [[ -n ${CI:-} ]]; then
    echo "Running in CI environment"
    is_ci=true
elif [[ $is_ci = true ]]; then
    echo "Running in manual CI mode"
fi

if [[ $is_ci = true ]]; then
    set +euo pipefail
fi

if [[ $set_ktlint_version = false ]] && [[ $is_ci = true ]]; then
    echo "CI mode, but '--version' was not used!"
    echo -e "Using default ktlint version ${CYAN}$CI_KTLINT_VERSION${NC}"
    ktlint_version=$CI_KTLINT_VERSION
fi

get_latest_release() {
    git ls-remote --refs --sort="version:refname" \
        --tags https://github.com/$REPO | cut -d/ -f3- | tail -n1 | tr -d 'v'
}

check_release_exists() {
    git ls-remote --refs --tags https://github.com/$REPO | cut -d/ -f3- | grep -q "$1"
}

unsupported() {
    echo "ERROR: Unsupported OS: $OSTYPE"
    echo "Install 'ktlint' manually!"
    exit 1
}

ensure_ktlint_path() {
    if [[ "$KTLINT_PATH" != "$ROOT" ]]; then
        mkdir -p "$KTLINT_PATH"
    fi
}

install() {
    if [[ $set_ktlint_version = true ]]; then
        echo -e "Using the provided ktlint version ${CYAN}$ktlint_version${NC}!"
    fi

    if [[ "$ktlint_version" == "latest" ]]; then
        echo "Fetching latest version of $REPO"
        version=$(get_latest_release)
        if [ -z "$version" ]; then
            error "Unable to fetch latest version of ktlint!"
        fi
    else
        version="$ktlint_version"

        if ! check_release_exists "$version"; then
            error "version ${CYAN}$version${NC} does not exist! Ensure you have a valid ktlint version"
        fi
    fi

    url="$DOWNLOAD_URL/$version/ktlint"
    echo "Ktlint version: $version"
    echo "Download URL  : $url"
    echo "ktlint path   : $KTLINT_PATH"

    ensure_ktlint_path
    curl -SL -o "$KTLINT_PATH/ktlint" "$url" && chmod a+x "$KTLINT_PATH/ktlint"

    # Save the installed version to a file alongside the binary
    echo "$version" >"$KTLINT_PATH/ktlint.version"
    echo -e "Saved version ${CYAN}$version${NC} to $KTLINT_PATH/ktlint.version"
}

install_ktlint() {
    # Temporarily don't explode on errors
    set +eo pipefail

    install
    result=$?
    if [[ $result -ne 0 ]]; then
        error "Unable to install ktlint!"
    fi

    # If we are running in Github Actions we need to add ktlint.sh to the path
    set +u
    if [[ $is_ci = true ]] && [[ -n "$GITHUB_PATH" ]]; then
        echo -e "Detected Github Actions as the CI, adding ${CYAN}$KTLINT_PATH${NC} to PATH"
        echo "$KTLINT_PATH" >>"$GITHUB_PATH"
    fi

    # Re-explode
    if [[ $is_ci = false ]]; then
        set -euo pipefail
    fi
}

verify_ktlint() {
    ktlint_binary="$KTLINT_PATH/ktlint"

    # Make sure the binary exists, if not try to find it in path
    if ! test -f "$ktlint_binary"; then
        error "ktlint not in PATH and '$KTLINT_PATH/ktlint' doesn't exist!"
    fi

    echo -e "Using ktlint at ${CYAN}'$ktlint_binary'${NC}"
}

configure_ktlint_for_project() {
    if [[ $apply_idea = true ]]; then
        echo "Applying ktlint styles to Android Studio for this project"
        ktlint.sh applyToIDEAProject -y >/dev/null 2>&1
        echo -e "Please ${RED}restart${NC} Android Studio for the changes to take affect"
        echo " "
    fi
}

check_and_install_ktlint() {
    echo "Checking for 'ktlint' in $KTLINT_PATH"

    if [[ $update_ktlint = true ]]; then
        echo "Forcing ktlint update..."
        install_ktlint
    else
        # Check if ktlint binary exists
        if [[ -f "$KTLINT_PATH/ktlint" ]]; then
            # Read the currently installed version
            installed_version=$(get_installed_version)
            if [[ -n "$installed_version" ]]; then
                echo -e "Currently installed ktlint version: ${CYAN}$installed_version${NC}"

                # Determine the version to compare with
                compare_version="$ktlint_version"
                if [[ "$compare_version" == "latest" ]]; then
                    echo "Checking latest available version..."
                    compare_version=$(get_latest_release)
                    echo -e "Latest available version: ${CYAN}$compare_version${NC}"
                fi

                # Compare versions and update if needed
                if [[ "$installed_version" != "$compare_version" ]]; then
                    if [[ $is_ci = true ]]; then
                        echo -e "Running in CI mode - skipping auto-update from ${CYAN}$installed_version${NC} to ${CYAN}$compare_version${NC}"
                    else
                        echo -e "Updating ktlint from ${CYAN}$installed_version${NC} to ${CYAN}$compare_version${NC}"
                        install_ktlint
                    fi
                else
                    echo -e "ktlint is already at version ${CYAN}$installed_version${NC}"
                fi
            else
                # No version file found
                if [[ $set_ktlint_version = true || $ktlint_version != "latest" ]]; then
                    echo -e "Installing specific ktlint version: ${CYAN}$ktlint_version${NC}"
                    install_ktlint
                elif [[ $is_ci = true ]]; then
                    echo "Running in CI mode - using existing ktlint installation"
                else
                    echo "Unable to determine installed ktlint version, reinstalling..."
                    install_ktlint
                fi
            fi
        else
            echo "Installing ktlint..."
            install_ktlint
        fi
    fi
}

finish() {
    echo "ktlint has been installed to a $KTLINT_PATH"

    # Make sure script are executable
    chmod +x "$KTLINT_SCRIPT"

    echo "If you haven't already you should run ./sigo init hooks"
}

# DEBUG - Print all passed in flags
#print_options # Comment before commiting

# Step 1: Check and install ktlint.sh if not available
check_and_install_ktlint

# Stop here if we are running in CI mode
if [[ $is_ci = true ]]; then
    exit 0
fi

# Step 2: Verify ktlint.sh and scripts
verify_ktlint

# Step 3: Integrate ktlint.sh into project
configure_ktlint_for_project

# Step ∞
finish
