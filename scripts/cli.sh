#!/usr/bin/env bash

set -eou pipefail

echo "Building..."
./gradlew --quiet ":apps:cli:installDist" >/dev/null

echo "Running: sigot $*"
"./apps/cli/build/install/sigot/bin/sigot" "$@"
