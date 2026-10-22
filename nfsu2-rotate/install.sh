#!/bin/sh
# Usage: ./install.sh <GAMEID>   (GAMEID from Dolphin: long-press game > Properties)
set -e
ID="$1"; [ -z "$ID" ] && { echo "Usage: $0 GAMEID"; exit 1; }
cp dolphin-config/GAMEID.ini "dolphin-config/$ID.ini"
adb shell mkdir -p /sdcard/Android/data/org.dolphinemu.dolphinemu/files/GameSettings
adb push "dolphin-config/$ID.ini" /sdcard/Android/data/org.dolphinemu.dolphinemu/files/GameSettings/
echo "Config installed for $ID"
