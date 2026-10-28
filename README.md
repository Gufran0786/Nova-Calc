# Nova Calc — Android Calculator

A modern, offline-first Android calculator built with Kotlin and Jetpack Compose.

## Features
- Modern dark UI with cyan/violet accents
- Basic arithmetic, parentheses, percentage and sign toggle
- Calculation history for the current app session
- Offline calculations; no API key or internet permission required

## Requirements
- Android Studio with JDK 17
- Android SDK 35
- Minimum Android 6.0 (API 23)

## Build
1. Open this repository in Android Studio.
2. Allow Gradle sync to finish.
3. Select **Build > Build APK(s)**.
4. Find the debug APK under `app/build/outputs/apk/debug/`.

## Project structure
- `app/src/main/AndroidManifest.xml` — app declaration and launcher activity
- `app/src/main/java/com/gufran/novacalc/MainActivity.kt` — UI and calculator logic
- `app/build.gradle.kts` — Android and Compose dependencies
- `build.gradle.kts` / `settings.gradle.kts` — Gradle project setup

## Status
This is a starter project and has not yet been built/tested on a device. Scientific functions and unit conversion are planned for a later version.
