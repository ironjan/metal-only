#!/bin/bash
android list targets
echo no | android create avd --force -n test -t android-25 --abi "google_apis/armeabi-v7a"
echo "emulator was created"
emulator -avd test -noaudio -no-window &
echo "waiting for emulator"
android-wait-for-emulator
sleep 180
echo "waiting for boot"
adb devices
adb shell input keyevent 82