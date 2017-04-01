#!/bin/bash
./gradlew clean ktlint lint test assembleRelease && \
caja build/outputs/apk/
