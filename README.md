# ForYou

Native Android app for managing medication therapies, with reminders, secure access and a monthly therapy report.
Individual project for the *Mobile Device Programming* course (University of Turin, 2024/2025).

> 🇮🇹 App Android nativa per la gestione delle terapie farmacologiche. Progetto individuale del corso di Programmazione Dispositivi Mobili (2024/2025).

<!-- TODO: add 3-4 screenshots or one GIF here, e.g. docs/screenshots/home.png -->

## Features

- Medication reminders scheduled with `AlarmManager` and delivered as local notifications
- Access protected by PIN and biometric authentication
- Monthly therapy report
- <!-- TODO: add the medicine management / daily schedule / intake tracking features from your old README -->

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Kotlin (about 3,300 lines) |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | MVVM |
| Persistence | Room |
| Async | Coroutines and Flow |
| Testing | JUnit, Mockito-Kotlin, instrumented tests with in-memory Room |

The app is fully local: no backend or network layer.

## Testing and field test

Unit tests cover the ViewModel logic; instrumented tests run against an in-memory Room database.

```bash
./gradlew test                  # unit tests
./gradlew connectedAndroidTest  # instrumented tests (emulator or device)
```

I also ran a field test with **4 users aged 25 to 85**. <!-- TODO: add 1-2 concrete findings and what you changed because of them -->

## Getting started

1. Clone the repository: `git clone https://github.com/Salvatore-LaRosa/ForYou2.git`
2. Open it in Android Studio and let Gradle sync.
3. Run on an emulator or a physical device (minimum SDK: <!-- TODO: fill in -->).

A prebuilt APK is available in [Releases](https://github.com/Salvatore-LaRosa/ForYou2/releases). <!-- TODO: upload one, or delete this line -->

## Project documents

Course presentation and project sheet are in [`docs/`](docs/).

## Author

Salvatore La Rosa · [LinkedIn](https://linkedin.com/in/larosa-salvatore) · [GitHub](https://github.com/Salvatore-LaRosa)
