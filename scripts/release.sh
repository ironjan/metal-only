#!/bin/bash
set -e errexit
set -o pipefail
./gradlew --daemon clean
./scripts/lint.sh
./scripts/_connected_test.sh*
./gradlew --daemon --parallel clean assembleRelease
caja app/build/outputs/apk/
