# MeNgaji Architecture Overview & Engineering Standards

## 1. System Overview & Tech Stack

MeNgaji is an Islamic mobile application built with **Compose Multiplatform (CMP)** targeting **Android** and **iOS** from a shared Kotlin codebase.

| Component | Technology | Version | Purpose |
|---|---|---|---|
| **Language** | Kotlin Multiplatform | `2.4.10` | Cross-platform shared logic & UI |
| **UI Framework** | Compose Multiplatform | `1.11.1` | Declarative UI across Android & iOS |
| **Design System** | Material 3 | `1.11.0-alpha07` | Emerald & Gold Islamic theme |
| **DI** | Koin | `4.0.2` | Dependency Injection (`core`, `compose`, `viewmodel`) |
| **Networking** | Ktor Client | `3.1.1` | Asynchronous HTTP client (CIO/Darwin engines) |
| **Serialization** | `kotlinx.serialization` | `1.8.0` | JSON parsing and DTO mapping |
| **Concurrency** | Kotlin Coroutines & Flow | `1.10.1` | Reactive streams and async execution |
| **Navigation** | Navigation Compose | `2.9.2` | Type-safe `@Serializable` route navigation |

---

## 2. Clean Architecture & Layer Responsibilities

```
+-------------------------------------------------------------------------+
|                           PRESENTATION LAYER                            |
|  - Compose UI Screens & Components                                      |
|  - ViewModels (Koin androidx-lifecycle-viewmodel)                       |
|  - UI State (Sealed Interface) & UI Events                              |
|  - Navigation Routes (@Serializable)                                    |
+------------------------------------+------------------------------------+
                                     | depends on
                                     v
+-------------------------------------------------------------------------+
|                             DOMAIN LAYER                                |
|  - Domain Models (pure Kotlin data classes)                             |
|  - Use Cases / Interactors (Single Responsibility)                      |
|  - Repository Interfaces (Contracts)                                   |
+------------------------------------+------------------------------------+
                                     ^
                                     | implements
+------------------------------------+------------------------------------+
|                              DATA LAYER                                 |
|  - Repository Implementations                                           |
|  - Remote Data Sources (Ktor HttpClient, ApiServices)                   |
|  - Data Transfer Objects (DTOs with @Serializable)                      |
|  - Mappers (DTO -> Domain Model)                                        |
+-------------------------------------------------------------------------+
```

### 2.1 Package Organization Pattern

Features must be organized by feature domain, with internal clean architecture layering:

```
shared/src/commonMain/kotlin/com/uncaan/mengaji/
├── core/
│   ├── data/
│   │   └── network/           # Ktor client engine factory, error handling, base config
│   ├── domain/
│   │   └── model/             # Shared domain entities, AppResult/Error wrapper
│   └── di/                    # Core DI modules (NetworkModule, CoroutineModule)
├── feature/
│   ├── quran/
│   │   ├── data/
│   │   │   ├── model/         # AyahResponse, AyahDto, EditionDto, SurahDto
│   │   │   ├── remote/        # QuranApiService, QuranApiServiceImpl
│   │   │   ├── repository/    # QuranRepositoryImpl
│   │   │   └── mapper/        # AyahMapper.kt (DTO -> Domain entity)
│   │   ├── domain/
│   │   │   ├── model/         # Ayah, Surah, Edition, AudioStream
│   │   │   ├── repository/    # QuranRepository (interface)
│   │   │   └── usecase/       # GetAyahUseCase, SearchAyahUseCase
│   │   ├── presentation/
│   │   │   ├── AlQuranScreen.kt
│   │   │   ├── QuranViewModel.kt
│   │   │   ├── QuranUiState.kt
│   │   │   └── component/     # AyahCard, SearchBar, EditionSelector
│   │   └── di/
│   │       └── QuranModule.kt # Koin module for feature
│   └── shalat/
│       └── presentation/
│           ├── ShalatScheduleScreen.kt
│           └── component/
├── navigation/                # Routes, bottom navigation bar, AppNavHost
└── theme/                     # Colors, Typography, Shapes, Theme composable
```

---

## 3. Core Architectural Rules

### 3.1 Unidirectional Data Flow (UDF)
1. **State Flows Down:** ViewModel exposes a single immutable `StateFlow<UiState>`.
2. **Events Flow Up:** UI triggers intents/events via ViewModel functions or sealed interface actions (e.g. `onAction(QuranAction)`).
3. **No Direct Mutable Exposure:** Never expose `MutableStateFlow` or mutable collections to Composables.

### 3.2 Immutability & Serialization
- All domain and UI models must be immutable `data class` or `sealed interface`.
- Only DTOs in `data/model` may use `@Serializable` from `kotlinx.serialization`. Domain entities must remain pure Kotlin.
- Mappers must be explicit extension functions or converter objects (e.g. `AyahDto.toDomain(): Ayah`).

### 3.3 Platform Independence & `expect/actual`
- Business logic, network calls, and UI layouts live in `commonMain`.
- Platform-native capabilities (e.g. Audio playback with Media3 on Android vs AVPlayer on iOS) are abstracted behind common interfaces or `expect class` declarations.
- Platform implementations live in `androidMain` and `iosMain`.
- Dependency injection handles platform-specific bindings via Koin platform modules.

---

## 4. SOLID & DRY Enforcement

- **Single Responsibility (SRP):** Each UseCase executes a single business operation. ViewModels handle UI state orchestration only. Data sources handle raw HTTP/persistence only.
- **Open/Closed (OCP):** UI state models use sealed interfaces to allow adding new states without modifying consumers.
- **Liskov Substitution (LSP):** Domain use cases interact exclusively with repository interfaces.
- **Interface Segregation (ISP):** Client-specific contracts; do not create monolithic repositories.
- **Dependency Inversion (DIP):** Presentation and domain layers depend on abstractions (interfaces), not concrete network/data implementations.
- **DRY (Don't Repeat Yourself):** Common UI components, error handling, network response wrappers, and Ktor client setups are centralized in `core/`.
