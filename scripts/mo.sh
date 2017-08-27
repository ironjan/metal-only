#!/bin/bash
ARG=$1

if [ "$ARG" = "w" ] || [ "$ARG" = "wish" ]; then
  chromium "https://www.metal-only.de/wunschgruss.html"
elif [ "$ARG" = "p" ] || [ "$ARG" = "plan" ]; then
  chromium "https://www.metal-only.de/sendeplan.html"
else
  echo "Given arguments: '$ARG'"
  echo "Usage: mo.so [w|wish|p|plan]"
  echo "Only the first argument is used"
  chromium "http://metal-only.de"
fi
