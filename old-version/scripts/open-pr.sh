#!/bin/bash
CURRENT_BRANCH=$(git branch | grep \* | cut -d ' ' -f2)
BASE_BRANCH=$1
if [ -z "$BASE_BRANCH" ]; then
  echo "Set target branch to beta"
  BASE_BRANCH=beta
fi

nohup chromium "https://github.com/ironjan/metal-only/compare/$BASE_BRANCH...$CURRENT_BRANCH" &
