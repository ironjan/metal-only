#!/bin/bash
BRANCH=$1
nohup chromium "https://github.com/ironjan/metal-only/compare/$BRANCH?expand=1" &
