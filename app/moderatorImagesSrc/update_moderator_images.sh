#!/bin/bash
function downloadModeratorList {
  curl https://www.metal-only.de/botcon/mob.php?action=all \
   | python -m json.tool \
   | grep moderator \
   | sed "s/.*moderator\": \"\(.*\)\".*/\1/" \
   | sort -u \
   > moderators.list
}

function downloadImages {
  for mod in $(cat moderators.list); do 
    wget "https://www.metal-only.de/botcon/mob.php?action=pic&nick=$mod" -O "$mod.png"
  done
}

function renameImagesToLowerCase {
  # From https://ubuntuforums.org/showthread.php?t=1336909&s=79968574034235fa266e1a7e897b0330&p=10371429#post10371429
  for file in *; do
    if [[ "$file" != "${file,,}" ]]; then
      mv -b -- "$file" "${file,,}"
    fi
  done
}

downloadModeratorList
downloadImages
renameImagesToLowerCase
