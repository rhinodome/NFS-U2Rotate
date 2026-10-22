# NFSU2 launcher for Anbernic RG Rotate

A small app with a built-in "Browse for ISO..." button. Pick your NFSU2 ISO once, then
"Play" boots Dolphin straight into it. A per-game Dolphin config tuned for the Unisoc T618
is included. The app does not contain the game or Dolphin.

## Setup
1. Install official Dolphin for Android on the Rotate, run it once, and grant it storage access.
2. Build the APK (see below) and install it.
3. Open the app, tap "Browse for ISO...", and choose your NFSU2 file. The app shows the
   Game ID it reads from the ISO.
4. Rename `dolphin-config/GAMEID.ini` to `<GameID>.ini` and copy it to
   `Android/data/org.dolphinemu.dolphinemu/files/GameSettings/` (or run `./install.sh <GameID>`
   with adb).
5. Tap "Play".

## Building without Android Studio
Push this folder to a new GitHub repo (files at the top level, including the hidden `.github`
folder). The workflow builds the debug APK automatically: open the Actions tab, pick the latest
run, and download the `nfsu2-rotate-apk` artifact. Debug APKs are already signed.
Or open the folder in Android Studio and run Build > Build APK.

## Notes
- If the app only opens Dolphin's menu instead of the game, use Dolphin's built-in
  "Add shortcut to home screen" on the game instead.
- Config values are a starting point, not tested on hardware. If it's slow, lower Overclock to 0.6;
  if graphics glitch, set EFBAccessEnable = True or EFBToTextureEnable = False.
