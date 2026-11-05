# Habit App

A focused native Android habit tracker for **Ayush Pandey**. Built with **Kotlin**, **Jetpack Compose**, and **Room**.

## Features

- Habit list with today’s progress and streak badges
- Add habit with color, weekday schedule, and optional reminder
- Check-in: **Completed** (how you feel) or **Missed** (reason & improvement)
- Undo today’s check-in
- Habit detail: week strip, streaks, schedule, reflection history, edit / archive / delete
- Insights: today completion, 7-day rate, longest streak
- Local reminders (AlarmManager) that survive reboot
- Offline persistence with Room (v2 schema + migration)

## Open & run in Android Studio

1. Install [Android Studio](https://developer.android.com/studio) with Android SDK API 35.
2. **File → New → Project from Version Control** and clone  
   `https://github.com/ayush2804/habit-app.git`  
   (or **File → Open** the folder that contains `settings.gradle.kts`).
3. Wait for Gradle sync.
4. Start an emulator (API 26+) or connect a device.
5. Run the `app` configuration.

### Command line

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Requires JDK 17+ and Android SDK (`local.properties` with `sdk.dir=...`).

## UX flows

1. **Home** — list + progress for habits due today; FAB to add; Insights in the app bar.
2. **Add** — name, color, days, optional reminder → Save.
3. **Check in** — tap the circle → Completed / Missed → reflection note → Save.
4. **Detail** — tap the habit title row → streaks, week view, history, edit/archive/delete.
5. **Insights** — quiet consistency summary (not a noisy dashboard).

## Requirements

- minSdk 26 / targetSdk 35
- JDK 17
