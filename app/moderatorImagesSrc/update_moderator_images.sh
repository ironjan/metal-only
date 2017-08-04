#!/bin/bash
curl https://www.metal-only.de/botcon/mob.php?action=all \
 | python -m json.tool \
 | grep moderator \
 | sed "s/.*moderator\": \"\(.*\)\".*/\1/" \
 | sort -u \
 > moderators.list
for mod in $(cat moderators.list); do 
  wget "https://www.metal-only.de/botcon/mob.php?action=pic&nick=$mod" -O "$mod.png"
done
