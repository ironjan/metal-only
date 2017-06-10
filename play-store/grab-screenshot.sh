#!/bin/bash
if [ $# -ne 1 ];
then
  echo "File name?"
  exit 1;
fi
adb shell screencap -p | sed 's/\r$//' > $1
