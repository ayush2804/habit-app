# Habit App

A simple native Android habit tracker for **Ayush Pandey**. Built with **Kotlin**, **Jetpack Compose**, and **Room**.

## Features

- Interactive habit list on launch
- Add a new habit (dialog → appears in the list)
- Tap a habit to check in:
  - **Completed** → reflection: *How do you feel?*
  - **Missed** → reflection: *What got in the way, and what could you try next time?*
- Habits and reflection notes persist locally with Room

## Open & run in Android Studio

1. Install [Android Studio](https://developer.android.com/studio) (Hedgehog or newer recommended) with an Android SDK (API 35) and a device/emulator (API 26+).
2. **File → Open** and select this repository root (the folder that contains `settings.gradle.kts`).
3. Let Gradle sync finish (Android Studio downloads the wrapper/distribution if needed).
4. Pick an emulator or a USB-connected device.
5. Click **Run** (green play) on the `app` configuration.

### Command line (optional)

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Requires `JAVA_HOME` (JDK 17+) and Android SDK (`ANDROID_HOME` / `local.properties` with `sdk.dir=...`).

## UX flows

1. **First launch** — empty list with a short hint; FAB **+** to add.
2. **Add habit** — name dialog → **Save** inserts the habit at the top of the list.
3. **Check in** — tap a habit → choose **Completed** or **Missed**.
4. **Reflect** — modal text field with save/cancel; note is stored with the outcome on the habit.
5. **Return later** — list and last outcomes reload from the local database.

## Project layout

```
app/src/main/java/com/ayushpandey/habitapp/
  MainActivity.kt
  HabitApplication.kt
  data/          # Room entities, DAO, database, repository
  ui/            # ViewModel + Compose habit list / dialogs
  ui/theme/      # Material 3 theme
```

## Requirements

- minSdk 26 / targetSdk 35
- JDK 17
