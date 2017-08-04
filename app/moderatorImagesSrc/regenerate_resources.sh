#!/bin/bash
for i in *png; do convert $i -resize 100 ../src/main/res/drawable-mdpi/$i; done
for i in *png; do convert $i -resize 75 ../src/main/res/drawable-ldpi/$i; done
for i in *png; do convert $i -resize 150 ../src/main/res/drawable-hdpi/$i; done
for i in *png; do convert $i -resize 200 ../src/main/res/drawable-xhdpi/$i; done

