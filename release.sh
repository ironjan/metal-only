#!/bin/bash
./gradlew --daemon clean ktlint lint test assembleRelease && \
caja app/build/outputs/apk/
