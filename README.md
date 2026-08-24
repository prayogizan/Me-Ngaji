# 🌙 MeNgaji (مِعْنَاقَاجِي)

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.11.1-purple.svg?logo=jetpackcompose)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green.svg)](https://github.com/prayogizan/Me-Ngaji)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20Architecture%20%2B%20UDF-orange.svg)](#-architecture--design-patterns)
[![License](https://img.shields.io/badge/License-MIT-brightgreen.svg)](LICENSE)

**MeNgaji** is a modern, cross-platform Islamic mobile application built with **Compose Multiplatform (CMP)** targeting **Android** and **iOS** from a single shared Kotlin codebase. It delivers an elegant Quran reading experience, audio recitation streaming, and Islamic lifestyle tools wrapped in a curated Material 3 Emerald & Gold design system.

---

## 📱 Features

- **📖 Quran Ayah Reader & Search**
  - Instant verse lookup via `Surah:Ayah` references (e.g., `2:255`, `112:1`) or global verse index.
  - Native RTL Quranic Arabic typography powered by Google Fonts Amiri font family.
  - Dual layout rendering: RTL Quranic Arabic and LTR translated text.
  - Translation edition presets (English Sahih International, Indonesian Kemenag, etc.).

- **🔊 Cross-Platform Audio Recitation Engine**
  - High-fidelity streaming audio recitation powered by native platform engines:
    - **Android:** AndroidX Media3 (ExoPlayer).
    - **iOS:** AVFoundation (AVPlayer) with `AVAudioSessionCategoryPlayback`.
  - Real-time animated 3-bar equalizer visualizer during active playback.
  - Interactive Play, Pause, Resume, and Stop controls with smooth icon transitions.
  - Lifecycle-aware auto-stop mechanism on tab navigation, new search execution, and screen disposal.
  - Dynamic card border highlights and elevated shadow during active streaming.

- **🕌 Shalat Prayer Schedule (In Progress)**
  - Automated prayer time calculation and adhan alerts.

- **🎨 Islamic Material 3 Design System**
  - Curated Emerald (`#1B4D3E`) & Secondary Gold (`#D4AF37`) palette.
  - Smooth light and dark theme adaptation.
  - Skeleton loading states and informative empty / network error states with retry capabilities.

---

## 🏗️ Architecture & Design Patterns

MeNgaji strictly adheres to **Clean Architecture**, **SOLID**, and **Unidirectional Data Flow (UDF)** principles.

```
+-------------------------------------------------------------------------+
|                           PRESENTATION LAYER                            |
|  - Stateful Screens (AlQuranScreen) & Stateless Content (AlQuranContent)|
|  - ViewModels (koin-compose-viewmodel)                                  |
|  - Sealed UI States (QuranUiState) & UI Actions (QuranUiAction)         |
|  - Type-Safe Routes (@Serializable) with Navigation Compose 2.9.2       |
+------------------------------------+------------------------------------+
                                     | depends on
                                     v
+-------------------------------------------------------------------------+
|                             DOMAIN LAYER                                |
|  - Pure Kotlin Business Entities (Ayah, Surah, Edition)                 |
|  - Single-Responsibility Use Cases (GetAyahUseCase)                     |
|  - Repository Interfaces (QuranRepository, AudioPlayer)                 |
|  - Zero framework / serialization / UI dependencies                     |
+------------------------------------+------------------------------------+
                                     ^
                                     | implements
+------------------------------------+------------------------------------+
|                              DATA LAYER                                 |
|  - Repository Implementations (QuranRepositoryImpl)                     |
|  - Remote Data Sources & Ktor ApiServices (QuranApiService)             |
|  - DTO Serialization (AyahResponseDto, AyahDto)                         |
|  - Mappers (AyahDto.toDomain() -> Ayah)                                 |
|  - Safe Network Execution (safeApiCall error boundary)                  |
+-------------------------------------------------------------------------+
```

### 🧩 Architectural Highlights
1. **Unidirectional Data Flow (UDF):**
   - ViewModels expose immutable `StateFlow<UiState>` collected with lifecycle awareness (`collectAsStateWithLifecycle()`).
   - UI intents are funneled through sealed `QuranUiAction` variants.
2. **Platform Abstraction (`expect/actual` & DI):**
   - Native audio playback engines implement common `AudioPlayer` interface and `expect class AyahAudioPlayer`.
   - Injected seamlessly via Koin platform modules.
3. **Type-Safe Navigation:**
   - Bottom navigation and screen destinations utilize Kotlinx Serialization `@Serializable` routes.

---

## 🛠️ Technology Stack

| Category | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | [Kotlin Multiplatform](https://kotlinlang.org/) | `2.4.10` | Shared business logic, networking, and UI |
| **UI Framework** | [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) | `1.11.1` | Declarative cross-platform UI |
| **Design System** | [Material 3](https://m3.material.io/) | `1.11.0-alpha07` | Theme tokens, dynamic components |
| **DI** | [Koin](https://insert-koin.io/) | `4.0.2` | Dependency Injection (`core`, `compose`, `viewmodel`) |
| **Networking** | [Ktor Client](https://ktor.io/) | `3.1.1` | Async HTTP client (CIO for Android, Darwin for iOS) |
| **Serialization** | [`kotlinx.serialization`](https://github.com/Kotlin/kotlinx.serialization) | `1.8.0` | JSON deserialization & navigation routing |
| **Concurrency** | [`kotlinx.coroutines`](https://github.com/Kotlin/kotlinx.coroutines) | `1.10.1` | Reactive streams (`StateFlow`, `Flow`) |
| **Navigation** | [Navigation Compose](https://developer.android.com/guide/navigation) | `2.9.2` | Type-safe multiplatform navigation |
| **Android Audio** | [AndroidX Media3](https://developer.android.com/media/media3) | `1.5.1` | ExoPlayer audio stream manager |
| **iOS Audio** | [AVFoundation](https://developer.apple.com/documentation/avfoundation) | Native | AVPlayer audio recitation streaming |

---

## 📁 Repository Structure

```
MeNgaji/
├── androidApp/                        # Android application entry point (MainActivity)
├── iosApp/                            # iOS Xcode project & SwiftUI host entry point
├── shared/                            # Shared Multiplatform module
│   ├── src/
│   │   ├── commonMain/                # Shared Kotlin & Compose UI
│   │   │   ├── composeResources/      # Vector drawables, fonts (Amiri), strings
│   │   │   └── kotlin/com/uncaan/mengaji/
│   │   │       ├── App.kt             # NavHost, Scaffold, and BottomBar
│   │   │       ├── core/
│   │   │       │   ├── audio/         # AudioPlayer interface & AudioState
│   │   │       │   ├── data/network/  # Ktor factory & safeApiCall
│   │   │       │   ├── domain/model/  # AppResult wrapper
│   │   │       │   └── di/            # CoreModule & KoinInit
│   │   │       ├── feature/
│   │   │       │   ├── quran/         # Quran reader (Data, Domain, Presentation, DI)
│   │   │       │   └── shalat/        # Shalat schedule feature
│   │   │       ├── navigation/        # Type-safe routes & bottom navigation tabs
│   │   │       └── theme/             # Material 3 Theme, Typography, Colors
│   │   ├── androidMain/               # Android-specific actuals (Media3 ExoPlayer)
│   │   ├── iosMain/                   # iOS-specific actuals (AVPlayer engine)
│   │   └── commonTest/                # Shared unit & ViewModel tests (MockEngine, Fakes)
├── engineer-docs/                     # Architecture contracts & system documentation
└── gradle/                            # Gradle wrapper and Version Catalog (libs.versions.toml)
```

---

## 🚀 Getting Started

### Prerequisites

- **JDK:** 17 or higher
- **Android Studio:** Ladybug (2024.2+) or Meerkat with Kotlin Multiplatform plugin
- **Xcode:** 15.0+ (for building and running iOS target on macOS)
- **CocoaPods / SwiftPM:** Configured for iOS simulator targets

### Clone Repository

```bash
git clone https://github.com/prayogizan/Me-Ngaji.git
cd Me-Ngaji
```

### Running on Android

You can run directly from Android Studio using the `androidApp` run configuration, or via CLI:

```bash
# Build and install debug APK on connected Android device/emulator
./gradlew :androidApp:installDebug

# Or build debug APK bundle
./gradlew :androidApp:assembleDebug
```

### Running on iOS

1. Open the project in Xcode:
   ```bash
   open iosApp/iosApp.xcodeproj
   ```
2. Select your target simulator (e.g. `iPhone 16`) and click **Run** (`Cmd + R`).

---

## 🧪 Testing & Quality Assurance

MeNgaji maintains high unit test coverage across Use Cases, Repositories (using Ktor `MockEngine`), and ViewModels (using Coroutine `StandardTestDispatcher`):

```bash
# Run all shared unit tests across all targets
./gradlew :shared:allTests

# Run Android host unit tests
./gradlew testDebugUnitTest

# Run full project checks
./gradlew check
```

---

## 📚 Engineering Documentation

Detailed architectural patterns, layer boundaries, and coding conventions are documented in:
- 📖 [Architecture Overview & Engineering Standards](engineer-docs/architecture-overview.md)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.