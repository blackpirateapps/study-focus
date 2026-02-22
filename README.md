# Study Focus

A Pomodoro-focused task management Android app inspired by **Things 3** design language.

## Features

- 📋 **Tasks** — Create tasks with subtasks, due dates, notes, and tags
- 🔁 **Recurring Tasks** — Daily, weekly, or custom day-of-week recurrence
- 🍅 **Pomodoro Timer** — Configurable sessions and duration per task
- 📁 **Projects** — Organize tasks into color-coded projects
- 🏷️ **Tags** — Categorize tasks with colored tags
- 🏠 **Home** — Recent projects and tasks at a glance
- 📅 **Today** — Tasks due today in one view

## Tech Stack

- Kotlin + Jetpack Compose + Material 3
- Room Database (local persistence)
- Hilt (dependency injection)
- Navigation Compose
- Foreground Service (Pomodoro timer)

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17+

### Setup Signing Key

Generate a keystore for release builds:

```bash
keytool -genkeypair -v \
  -keystore app/keystore/release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias studyfocus \
  -storepass YOUR_STORE_PASSWORD \
  -keypass YOUR_KEY_PASSWORD
```

Add to `local.properties`:

```properties
KEYSTORE_FILE=keystore/release.jks
KEYSTORE_PASSWORD=YOUR_STORE_PASSWORD
KEY_ALIAS=studyfocus
KEY_PASSWORD=YOUR_KEY_PASSWORD
```

### GitHub Actions Secrets

For CI/CD, add these repository secrets:

| Secret | Value |
|--------|-------|
| `KEYSTORE_BASE64` | `base64 -w 0 app/keystore/release.jks` |
| `KEYSTORE_PASSWORD` | Your store password |
| `KEY_ALIAS` | `studyfocus` |
| `KEY_PASSWORD` | Your key password |

### Build

```bash
# Debug
./gradlew assembleDebug

# Release (requires keystore)
./gradlew assembleRelease
```

## Project Structure

```
app/src/main/java/com/studyfocus/
├── data/          # Room entities, DAOs, repositories, DI
├── service/       # Pomodoro foreground service
└── ui/
    ├── theme/     # Colors, typography, dimensions
    ├── components/# Reusable composables
    ├── navigation/# Route definitions
    └── screens/   # Home, Today, Pomodoro, Projects, Task
```
