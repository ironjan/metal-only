#!/bin/bash
./gradlew --daemon --parallel clean ktlint lint test assembleRelease && \
caja app/build/outputs/apk/
