#!/bin/bash
rm ./app/build/outputs/apk/release/*
./gradlew assembleRelease && caja ./app/build/outputs/apk/release/
