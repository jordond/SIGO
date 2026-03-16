#!/usr/bin/env bash

set -eou pipefail

echo "Building..."
./gradlew --quiet ":apps:cli:installDist" >/dev/null

echo "Running: sigo $*"
"./apps/cli/build/install/sigo/bin/sigo" "$@"
