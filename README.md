# DESKOS 1.0

An offline-first monochrome desk dashboard for Android tablets, designed around the Lenovo Tab M11.

## Included

- Five swipeable screens: calendar, clock, timer, planner, focus
- Calendar month navigation and date selection
- Local task list with completion and deletion
- Quick note saved on-device
- Countdown timer, presets, stopwatch and Pomodoro sessions
- Swipe down for quick settings
- Long-press the current screen to customise its label and brightness
- Double-tap the display area to dim it; tap the dimmed screen to wake it
- Native keep-screen-awake policy, charging-only mode, brightness control and battery percentage
- No account, network access, analytics or ads

## Build the APK

### Option 1: GitHub Actions (recommended)

1. Create a new GitHub repository and upload the contents of this folder.
2. Open **Actions** in the repository.
3. Run **Build DESKOS APK** (or push to `main`).
4. Open the completed workflow run and download the `DESKOS-debug-apk` artifact.
5. Extract `app-debug.apk` and install it on the tablet. Android may ask you to allow installation from the app used to open the APK.

### Option 2: Android Studio

1. Open this folder in Android Studio.
2. Allow Gradle sync and install Android SDK Platform 35 if prompted.
3. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
4. The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Device notes

- `Charging mode only` uses Android's battery status to keep the display awake only while charging.
- The app is designed for offline use. Tasks, settings and notes are stored locally in the app's WebView storage.
- Double-tap dims the app window to near-black rather than powering off the LCD panel. Tap the dim screen to restore it.
- Android may still apply its own system timeout and battery policies. Keep-screen-awake is controlled while the app is open.
- This is a debug-signed APK build configuration, suitable for personal installation and testing. A release build should be signed with a private key before distribution.
