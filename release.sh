#!/bin/bash
./gradlew --daemon clean ktlint lint test assembleRelease && \
caja build/outputs/apk/
