#!/bin/bash
echo no | android create avd --force -n test -t android-19 --abi armeabi-v7a
emulator -avd test -no-audio -no-window &
android-wait-for-emulator
sleep 180
adb devices
adb shell input keyevent 82