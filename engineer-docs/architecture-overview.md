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
| **Background Work** | WorkManager | `2.10.0` | Periodic background refresh for Android widgets |

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
│   │   │   ├── model/         # AyahResponse, AyahDto, EditionDto, SurahDto, LastReadProgressDto, AyahBookmarkDto
│   │   │   ├── remote/        # QuranApiService, QuranApiServiceImpl
│   │   │   ├── local/         # LocalRecitationCache (expect/actual SharedPreferences / NSUserDefaults)
│   │   │   ├── repository/    # QuranRepositoryImpl
│   │   │   └── mapper/        # AyahMapper.kt, SurahMapper.kt, RecitationMapper.kt
│   │   ├── domain/
│   │   │   ├── model/         # Ayah, Surah, Edition, SurahSummary, SurahDetail, MushafPage, LastReadProgress, AyahBookmark
│   │   │   ├── repository/    # QuranRepository (interface)
│   │   │   └── usecase/       # GetAyahUseCase, GetSurahListUseCase, GetSurahDetailUseCase, GetMushafPageUseCase, GetLastReadUseCase, SaveLastReadUseCase, GetBookmarksUseCase, ToggleBookmarkUseCase
│   │   ├── presentation/
│   │   │   ├── AlQuranScreen.kt
│   │   │   ├── QuranViewModel.kt
│   │   │   ├── QuranUiState.kt
│   │   │   └── component/     # AyahCard, SearchBar, EditionSelector
│   │   └── di/
│   │       └── QuranModule.kt # Koin module for feature
│   ├── shalat/
│   │   └── presentation/
│   │       ├── ShalatScheduleScreen.kt
│   │       └── component/
│   └── widget/
│       ├── data/
│       │   ├── model/         # DailyAyahResponseDto, DailyAyahEditionDto
│       │   ├── remote/        # DailyAyahApiService, DailyAyahApiServiceImpl
│       │   ├── repository/    # DailyAyahRepositoryImpl
│       │   ├── local/         # LocalAyahCache (expect/actual)
│       │   └── mapper/        # DailyAyahMapper.kt (DTO -> Domain entity)
│       ├── domain/
│       │   ├── model/         # DailyAyah
│       │   ├── repository/    # DailyAyahRepository (interface)
│       │   └── usecase/       # GetDailyAyahUseCase
│       └── di/
│           └── WidgetModule.kt# Koin module for feature
├── navigation/                # Routes, bottom navigation bar, AppNavHost
└── theme/                     # Colors, Typography, Shapes, Theme composable

androidApp/src/main/kotlin/com/uncaan/mengaji/
├── MeNgajiApplication.kt      # App entry point, Koin init, WorkManager scheduling
├── MainActivity.kt            # Compose host activity
└── widget/
    ├── DailyAyahWidgetProvider.kt # Responsive Glance AppWidget provider
    ├── DailyAyahWidgetReceiver.kt # BroadcastReceiver for widget lifecycle
    ├── action/
    │   ├── RefreshAyahAction.kt   # Manual user refresh action callback
    │   └── AyahDeepLinkAction.kt  # Glance action builder for reader deep links
    ├── worker/
    │   ├── DailyAyahRefreshWorker.kt # 24h background periodic CoroutineWorker
    │   └── DailyAyahWorkScheduler.kt # Idempotent WorkManager scheduler
    └── ui/                        # Glance layout composables & theme
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

---

## 5. Build Flavors & App Configuration

MeNgaji implements a unified multiplatform flavor and environment configuration architecture:

| Environment | Android Flavor / App ID | iOS Bundle ID | Version Name | Debug Logging |
|---|---|---|---|---|
| **Development** (`DEV`) | `dev` (`com.uncaan.mengaji.dev`) | `com.uncaan.mengaji.dev` | `0.0.1-dev` | Enabled (`LogLevel.ALL`) |
| **Production** (`PROD`) | `prod` (`com.uncaan.mengaji`) | `com.uncaan.mengaji` | `0.0.1` | Info Only (`LogLevel.INFO`) |

### 5.1 Architecture Flow
1. **Host Entry Point:**
   - **Android (`MainActivity`):** Reads `BuildConfig.FLAVOR`, `BuildConfig.VERSION_NAME`, and `BuildConfig.DEBUG` to construct `AppConfig`.
   - **iOS (`MainViewController` / `iOSApp`):** Initializes `AppConfig` from platform build configurations.
2. **Koin Propagation (`initKoin` / `coreModule`):**
   - Injects singleton `AppConfig` into the DI graph.
   - Supplies `baseUrl` and `isDebug` dynamically to `HttpClientFactory.create()`.

---

## 6. Background Processing & Widget Synchronization (WorkManager)

MeNgaji leverages Android Jetpack **WorkManager** (`2.10.0`) for battery-conscious background task orchestration:

### 6.1 Periodic Ayah Refresh Architecture
- **Worker (`DailyAyahRefreshWorker`):** Subclasses `CoroutineWorker` and resolves domain dependencies via Koin (`inject<GetDailyAyahUseCase>()`).
- **Forced Sync Contract:** Invokes `GetDailyAyahUseCase(forceRefresh = true)` which bypasses local calendar-day cache checks, queries the remote Quran API, persists the fresh verse into `LocalAyahCache`, and pushes layout updates to all active `DailyAyahWidgetProvider` instances via `GlanceAppWidgetManager`.
- **Power & Doze Awareness:** Configured with `NetworkType.CONNECTED` without aggressive wake locks, allowing the Android OS to batch execution during battery maintenance windows.
- **Backoff & Resilience:** Retries transient network failures using `BackoffPolicy.EXPONENTIAL` up to 3 times before gracefully terminating to preserve existing cached verses on the home screen.
- **Idempotent Scheduling (`DailyAyahWorkScheduler`):** Utilizes `ExistingPeriodicWorkPolicy.KEEP` to safely register the 24-hour job on both application cold start (`MeNgajiApplication.onCreate`) and home screen widget placement (`DailyAyahWidgetReceiver.onEnabled`).

### 6.2 Widget Deep-Link Navigation Architecture (CMP-504, CMP-607)
- **Widget Tap Action:** `DailyAyahWidgetProvider` layouts (Full, Compact, Small) bind container clicks to `createAyahDeepLinkAction(ayah)` producing `actionStartActivity<MainActivity>()` populated with typed `ActionParameters` (`surah_number`, `ayah_number`). Fallback layout retains parameterless launch.
- **Host Activity Lifecycle (`MainActivity`):** Configured with `android:launchMode="singleTop"` to intercept widget clicks via both `onCreate()` (cold start) and `onNewIntent()` (warm start / app already running) without duplicate Activity instantiation.
- **Type-Safe Compose Route:** Deep links map to `AlQuranRoute(surahNumber = Int?, ayahNumber = Int?)`. Bottom navigation bar matching leverages `currentDestination.hasRoute(AlQuranRoute::class)` to retain active tab selection state.
- **Automated Reader Transition & Highlighting (CMP-607):** `AlQuranScreen` observes initial deep-link parameters via `LaunchedEffect(initialSurahNumber, initialAyahNumber)`. When `initialSurahNumber` is present, it directly dispatches `QuranUiAction.OpenSurah(surahNumber, scrollToAyah)`:
  - `QuranViewModel` loads the complete Surah (`GetSurahDetailUseCase`), transitions the reader into active state (`isReaderActive = true`, `readerMode = ReaderMode.VERSE_LIST`), sets `highlightedAyahNumber = scrollToAyah`, and scrolls `LazyListState` directly to the target verse.
  - `VerseListItemCard` visually highlights the target verse with a `2.dp` Gold border (`SecondaryGold`) and `#044736` primary emerald container tint (`alpha = 0.08f`) displaying a `📌 TARGET` badge even when audio recitation is idle.
- **In-Screen Back Navigation Interception (`COMPACT` Mode):**
  - When in `COMPACT (< 600.dp)` mode with an active reader or verse inspection (`isReaderActive == true` or `uiState !is QuranUiState.Initial`), physical Back button presses and predictive back gestures are intercepted by multiplatform `BackHandler` (`core/ui/BackHandler.kt`).
  - Intercepted back events dispatch `QuranUiAction.CloseReaderToCatalog`, smoothly resetting reader and highlight states and stopping audio playback to return to `SurahCatalogPane` before popping the tab destination. Top-bar back navigation (`ReaderTopBar`) dispatches the identical `CloseReaderToCatalog` action.

### 6.3 Widget Configuration Activity & Theming Architecture (CMP-505)
- **Configuration Contract:** `WidgetConfigActivity` is registered with `android:configure="com.uncaan.mengaji.widget.WidgetConfigActivity"` in `daily_ayah_widget_info.xml` and implements the standard Android AppWidget configuration lifecycle:
  1. Sets `setResult(RESULT_CANCELED)` immediately in `onCreate()` to ensure back navigation or cancellation aborts draft widget placement.
  2. Resolves `appWidgetId` from `AppWidgetManager.EXTRA_APPWIDGET_ID`.
  3. Displays a real-time live preview panel reflecting user selections (Quranic Arabic typography via `getQuranArabicTextStyle()`, sample verse in selected language, and selected theme palette).
  4. On confirmation, saves settings via `DailyAyahWidgetConfigManager`, triggers an immediate render for the newly created instance via `DailyAyahWidgetProvider().update(context, glanceId)`, returns `RESULT_OK` populated with `appWidgetId`, and finishes.
- **Instance Persistence (`DailyAyahWidgetConfigManager`):** Preferences are stored per-instance in private `SharedPreferences` (`daily_ayah_widget_preferences`):
  - `widget_config_${appWidgetId}_language`: `"id.indonesian"` | `"en.asad"`
  - `widget_config_${appWidgetId}_theme`: `"light"` | `"dark"` | `"auto"`
  Orphaned preferences are purged in `DailyAyahWidgetReceiver.onDeleted(context, appWidgetIds)`.
- **Multi-Edition Translation Querying:** Parameterized `GetDailyAyahUseCase(edition: String)` and `DailyAyahRepository.getDailyAyah(edition: String)` query `ayah/random/editions/quran-uthmani,$edition`, invalidating cache when the requested edition differs from cached verse data.
- **Glance Theme Tokens (`DailyAyahColorTokens`):** Provides fixed Light (`#FFF8F0` / `#00695C`), fixed Dark (`#1A1A2E` / `#FFD54F`), and dynamic Auto palettes across all Glance layout breakpoints (Full 4x2, Compact 4x1, Small 2x2, Fallback).

---

## 7. Recitation Persistence, Last Read & Bookmarks Architecture (CMP-602)

MeNgaji implements persistent local caching for Quran recitation state, personal ayah bookmarks, and offline catalog resilience across Android and iOS platforms:

### 7.1 Cross-Platform Storage Abstraction (`LocalRecitationCache`)
- **Storage Contract:** Pure interface `LocalRecitationCache` in `commonMain` implemented by `PlatformLocalRecitationCache` via Kotlin `expect`/`actual`:
  - **Android (`androidMain`):** Backed by Android `SharedPreferences` (`"mengaji_recitation_cache"`, `Context.MODE_PRIVATE`).
  - **iOS (`iosMain`):** Backed by `platform.Foundation.NSUserDefaults.standardUserDefaults`.
- **Storage Keys:**
  - `mengaji_last_read_progress_v1`: JSON-serialized `LastReadProgressDto`.
  - `mengaji_ayah_bookmarks_v1`: JSON-serialized `List<AyahBookmarkDto>`.
  - `mengaji_surah_catalog_cache_v1`: JSON-serialized `List<SurahSummaryDto>`.

### 7.2 Fault Tolerance & Malformed Payload Recovery
All JSON deserializations through `kotlinx.serialization.json.Json` (`ignoreUnknownKeys = true`, `isLenient = true`) are wrapped in resilient error handling:
- Malformed or corrupted single items (`LastReadProgressDto`) fail gracefully to `null`.
- Malformed or corrupted list items (`List<AyahBookmarkDto>`, `List<SurahSummaryDto>`) fail gracefully to `emptyList()` or `null`.
- The application runtime will never crash due to corrupted preferences data.

### 7.3 Offline 114-Surah Catalog Resilience
- `QuranRepositoryImpl.getSurahList(forceRefresh)` caches remote API payloads into `localCache.cacheSurahCatalog(rawDtos)` upon every successful network response.
- In the event of network timeouts, connection drops, or offline usage, `getSurahList` automatically queries `localCache.getCachedSurahCatalog()`, maps the cached entries to pure domain `SurahSummary` entities, and emits `AppResult.Success(cachedList)`.

### 7.4 Bookmark Toggling & Invariant Sorting
- `QuranRepository.toggleBookmark(bookmark)` provides idempotent bookmark state management:
  - If `bookmark.globalAyahNumber` already exists in storage, the verse is removed and `false` is returned.
  - If absent, the verse is appended and `true` is returned.
- Invariant ordering: Persisted bookmarks and retrieval results through `GetBookmarksUseCase` are consistently sorted with newest first (`savedAtEpochMillis` descending).

---

## 8. Continuous Verse Queue Audio Engine & KeepScreenOn Architecture (CMP-603)

MeNgaji implements a reactive, gapless recitation queue playback engine and display sleep management architecture:

### 8.1 Unified Audio Engine Abstraction (`AudioPlayer` & `AyahAudioPlayer`)
- **Queue Streaming Contract (`AudioPlayer`):** Abstract interface in `core/audio/` implemented by `expect class AyahAudioPlayer`:
  - `playQueue(tracks: List<AudioTrack>, startIndex: Int)`: Seamlessly streams a list of verses without audio gaps.
  - `skipToNext()` / `skipToPrevious()`: Queue transport navigation with bounds checking and repeat mode wrapping.
  - `setPlaybackSpeed(speed: Float)`: Configures playback rate (`0.75x`, `1.0x`, `1.25x`, `1.5x`).
  - `setRepeatMode(mode: AudioRepeatMode)`: Configures loop behavior (`NONE`, `REPEAT_AYAH`, `REPEAT_SURAH`).
  - `play(url: String)`: Backward-compatible single-track playback wrapper.
- **Rich Reactive State (`AudioState`):** Emitted as `StateFlow<AudioState>` with live verse tracking identifiers (`currentUrl`, `currentSurahNumber`, `currentAyahNumberInSurah`, `currentGlobalAyahNumber`, `playbackSpeed`, `repeatMode`, `isPlaying`, `isBuffering`, `error`).
- **Android Native Engine (`androidMain`):** Powered by AndroidX Media3 `ExoPlayer.setMediaItems(items, startIndex, C.TIME_UNSET)` with `Player.Listener.onMediaItemTransition` decoding `AudioTrack` tags to emit active verse metadata in real time.
- **iOS Native Engine (`iosMain`):** Powered by AVFoundation `AVPlayer` with `AVAudioSessionCategoryPlayback` and notification observer on `AVPlayerItemDidPlayToEndTimeNotification` for automated track progression and repeat handling.

### 8.2 KeepScreenOn Platform Abstraction (`core/ui/KeepScreenOn.kt`)
- **Lifecycle-Safe Composable Contract:** `expect @Composable fun KeepScreenOn(enabled: Boolean = true)`
  - **Android (`androidMain`):** Uses `LocalView.current.keepScreenOn = enabled` inside `DisposableEffect(enabled)`, safely restoring previous window state on disposal.
  - **iOS (`iosMain`):** Configures `UIApplication.sharedApplication.idleTimerDisabled = enabled` inside `DisposableEffect(enabled)`, resetting to previous timer configuration on disposal.

---

## 9. Surah/Juz Catalog, Last Read Hero Banner & Quick Jump Scrubber UI Architecture (CMP-604)

MeNgaji implements a multi-modal Quran exploration hub matching Figma Screen 01 (`node-id=1-25`), unifying the 114 Surahs, 30 Juz parts, saved bookmarks, and rapid scrubber navigation:

### 9.1 Static Juz Domain Mapping (`JuzSummary` & `JuzCatalogData`)
- **Domain Model (`JuzSummary`):** Pure Kotlin data class capturing the 1-indexed Juz ordinal (`1..30`), traditional Arabic script title (`nameArabic`), starting and ending Surah coordinates, and Medina Mushaf page boundaries (`startPageNumber..endPageNumber`).
- **Static Canonical Repository (`JuzCatalogData`):** Provides an immutable sequence of all 30 Juz covering the complete 604-page Medina Mushaf without gaps. Provides lookup helpers `getJuzByNumber(number: Int)` and `getJuzForPage(pageNumber: Int)`.

### 9.2 Last Read Hero Banner Card (`LastReadHeroCard`)
- **Visual Design:** High-contrast `#044736` to `#065F46` emerald gradient container with Gold (`#D4AF37` / `#F59E0B`) accent pill badges, right-aligned RTL Uthmani Arabic Surah title via `getQuranArabicTextStyle()`, and an active streak pill (`🔥 5-Day Streak`).
- **1-Tap Continuation:** Invokes `QuranUiAction.ResumeLastRead(progress)` on click, seamlessly resuming playback and reader focus at the recorded Surah, Ayah, or Mushaf Page in the user's preferred `ReaderMode`.

### 9.3 Unified Search & Direct Verse Lookup
- **Multi-Field Filtering:** Filters the 114 Surahs in real time against English transliterations, English meanings, and numeric Surah IDs without redundant network requests.
- **Regex Pattern Detection:** Detects `^\s*(\d{1,3})\s*:\s*(\d{1,3})\s*$` patterns (e.g. `2:255` for Ayat al-Kursi) to instantly surface the `DirectAyahJumpChip` (`"Go to Surah X, Ayah Y →"`).

### 9.4 3-Way Segmented Catalog Tabs (`SegmentedCatalogTabs`)
- **Segmented Control:** Smoothly toggles between `Surah (114)`, `Juz (30)`, and `Bookmarks (N)` with animated indicator transitions.
- **Tab Content:**
  - `SURAH`: LazyColumn of `SurahListItemCard`s featuring Rub el Hizb number badges, revelation type badges (`MECCAN` / `MEDINAN`), verse counts, and RTL Arabic calligraphy titles.
  - `JUZ`: LazyColumn of `JuzListItemCard`s displaying starting verse spans and page spans.
  - `BOOKMARKS`: LazyColumn of `BookmarkListItemCard`s with quick deletion actions and an elegant `BookmarksEmptyState` fallback.

### 9.5 Quick Jump Scrubber BottomSheet (`QuickJumpBottomSheet`)
- **Multi-Modal Navigation Scrubber:** ModalBottomSheet with 3 operational modes:
  1. **Surah & Ayah Picker:** Dropdown selector paired with bounded numeric verse input (`1..selectedSurah.numberOfAyahs`).
  2. **Mushaf Page Scrubber:** Smooth slider and direct page input across `1..604`, automatically displaying the corresponding Juz context.
  3. **30-Juz Rapid Grid:** 5x6 grid of Juz chips enabling 1-tap navigation to any Juz starting verse.

---

## 10. Dual-Mode Quran Recitation Reader, RTL Mushaf Pager & Audio Dock Architecture (CMP-605)

MeNgaji implements an adaptive dual-mode recitation reading environment matching Figma Screen 02 (`node-id=1-91`) and Screen 03 (`node-id=1-155`), unifying verse-by-verse analytical study, authentic 604-page Medina Mushaf pagination, thumb-zone playback ergonomics, and distraction-free *Khushu'* focus:

### 10.1 Dual-Mode Reader Presentation (`ReaderMode`)
- **`VERSE_LIST` (Verse-by-Verse Study Mode):**
  - Displays ornamental calligraphy header with Surah metadata, English translation, and revelation badges.
  - Enforces the canonical 3-way Bismillah rule:
    1. **Surah 9 (*At-Tawbah*):** Never render ornamental Bismillah header.
    2. **Surah 1 (*Al-Fatihah*):** Ayah 1 is the Bismillah itself (`بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ۝١`); rendered as Ayah 1 without duplicate header banner.
    3. **Surahs 2..8 and 10..114:** Pre-pended API Bismillah prefixes are stripped from Ayah 1, rendering the dedicated ornamental Bismillah banner above Ayah 1.
  - Active reciting Ayah karaoke highlighting: `2.dp` Gold border (`#F59E0B`), warm cream background tint (`#FFFBEB` Light / `#0A2E24` Dark), and `🔊 RECITING` badge.
  - Auto-scroll: Smooth scroll animation (`lazyListState.animateScrollToItem`) tracking active audio track transitions.
  - Recitation progress persistence: Debounced (`800ms`) scroll tracking automatically saves `LastReadProgress` checkpoints.
- **`MUSHAF_PAGE` (Authentic 604-Page Medina Mushaf Pager):**
  - Paginated across pages `1..604` using Compose `HorizontalPager(state = pagerState, reverseLayout = true)` wrapped in `CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl)` for natural Arabic left-to-right swiping ($N \to N+1$).
  - Double-bordered illuminated frame on authentic cream `#FFFDF7` (`#0A2E24` Dark) paper canvas.
  - Inline chapter headers rendered when a new Surah commences on that page.
  - Flowing RTL Uthmani text with embedded end-of-ayah calligraphy medallions (`۝`).
  - Single tap toggles distraction-free **Immersive *Khushu'* Mode**, hiding the top bar and audio dock with smooth slide/fade animations.
- **Bidirectional Coordinate Preservation:** Seamlessly preserves user reading position when switching modes (`surahNumber:ayahNumber` in `VERSE_LIST` $\longleftrightarrow$ `pageNumber` in `MUSHAF_PAGE`).

### 10.2 Floating Thumb-Zone Audio Transport Dock (`RecitationAudioDock`)
- Positioned in the bottom thumb zone with an elevated `#044736` deep emerald rounded pill container.
- Real-time queue progress indicator, active Surah/Ayah coordinates, and Qari metadata.
- Integrated controls: playback speed selector (`0.75x`, `1.0x`, `1.25x`, `1.5x`), repeat mode cycle chip (`NONE`, `REPEAT_AYAH`, `REPEAT_SURAH`), previous track, play/pause toggle with buffering spinner, and next track.

### 10.3 Contextual Ayah Action Sheet (`AyahActionBottomSheet`)
- Triggered by `•••` action buttons on verse cards or by tapping/long-pressing verses in Mushaf mode.
- Contextual actions: `▶ Play from Here`, `🔁 Loop Ayah (Hifz)`, `📌 Mark as Last Read`, `★ Bookmark`, and `📤 Copy / Share` formatted verses to the system clipboard.

### 10.4 Display Sleep Awareness (`KeepScreenOn`)
- `KeepScreenOn(enabled = readerState.isReaderActive || audioState.isPlaying)` prevents screen dimming or timeout during active Quran study and audio recitation sessions.

---

## 11. Multi-Factor Adaptive Layouts & Navigation Shell Architecture (CMP-606)

MeNgaji implements a multi-factor adaptive layout architecture matching Figma Screen 04 (`node-id=1-200`), delivering ergonomic usability across smartphones, foldables, tablets, and desktop displays:

### 11.1 Window Size Classification (`MeNgajiWindowWidthClass`)
- **Classifier (`AdaptiveWindowSize.kt`):** Maps viewport horizontal dimension to canonical breakpoints:
  - `COMPACT` (`< 600.dp`): Handheld smartphones in portrait orientation.
  - `MEDIUM` (`600.dp..839.dp`): Unfolded foldables, small tablets, and landscape smartphones.
  - `EXPANDED` (`>= 840.dp`): Large tablets, desktops, and large foldable displays.
- **Ambient Propagation:** Evaluated via pure CMP `rememberWindowWidthClass(maxWidth)` and distributed through `LocalWindowWidthClass` composition local.

### 11.2 Adaptive Navigation Shell (`MeNgajiNavigationRail`)
- **Compact Viewports (`< 600.dp`):** Employs standard `MeNgajiBottomBar` and top app bar.
- **Medium & Expanded Viewports (`>= 600.dp`):** Docks an 80.dp vertical `MeNgajiNavigationRail` on the start edge with app branding and tab selection, maximizing vertical reading height.
- **Distraction-Free Immersion:** Automatically conceals navigation chrome during *Khushu'* mode.

### 11.3 Split-Pane Master-Detail Layout (`MEDIUM`)
- **Two-Pane Split:**
  - Left Pane (`35%` width, min `280.dp`): `SurahCatalogPane` in split-pane mode with active Surah highlighted.
  - Hinge-Safe Gutter (`16.dp`): Prevents UI elements from being bisected by foldable center creases.
  - Right Pane (`65%` width): Active `VerseListReader` or single-page `MushafPageReader`.
- **Non-Blank Detail Guarantee:** Automatically initializes with `lastReadProgress` (or Surah 1 *Al-Fatihah*) so the detail pane is never blank on initial open.

### 11.4 Authentic Dual-Page RTL Medina Mushaf Book Spread (`EXPANDED`)
- **Dual-Leaf Book Emulation (`DualMushafSpreadReader`):**
  - **Right Leaf:** Odd page $2k + 1 \in [1, 3, \dots, 603]$ (e.g., Page 1 *Al-Fatihah*).
  - **Left Leaf:** Even facing page $2k + 2 \in [2, 4, \dots, 604]$ (e.g., Page 2 *Al-Baqarah* 1–5).
  - **Central Spine Shadow & Foldable Hinge Gutter (`20.dp`):** Realistic book spine depth gradient protecting against foldable display crease occlusions.
- **Collapsible Master Sidebar (`320.dp`):** 1-tap toggling (`⫷ Index` / `⫸ Index`) allows instant navigation across Surahs and Juzs without leaving the dual-page reading canvas.
- **Wide Recitation Audio Dock (`WideRecitationAudioDock`):** Full-width emerald dock format with active verse coordinates on the left and transport controls on the right.

### 11.5 Bi-directional Coordinate Mapping & Zero State Loss
- **Spread Coordinate Mapping:** $k = (P - 1) / 2$ guarantees seamless transitions between single-page and dual-spread reading modes.
- **State Invariance:** Active audio playback streams, reciting Ayah highlighting, and recitation checkpoints are fully preserved across screen rotations, window resizes, and foldable posture shifts.

---

## 12. Verification & Multiplatform Test Architecture (CMP-608)

MeNgaji implements an automated, isolated, multi-tiered unit test architecture across `shared/src/commonTest/kotlin/`:

### 12.1 Test Architecture Topology

```
shared/src/commonTest/kotlin/com/uncaan/mengaji/
├── core/
│   ├── audio/
│   │   ├── AudioStateTest.kt             # AudioState immutable reduction tests
│   │   └── ContinuousAudioEngineTest.kt  # Queue streaming, repeat mode & speed tests
│   └── ui/
│       └── AdaptiveWindowSizeTest.kt     # Breakpoint mapping (<600dp, 600..839dp, >=840dp)
├── feature/
│   ├── quran/
│   │   ├── data/
│   │   │   ├── local/LocalRecitationCacheJsonTest.kt # Resilient serialization & corrupted payload tests
│   │   │   ├── mapper/                               # DTO <-> Domain entity mapping tests
│   │   │   └── repository/
│   │   │       ├── QuranRepositoryTest.kt            # Base repository tests
│   │   │       └── QuranRepositoryRecitationTest.kt  # Ktor MockEngine multi-edition zipping & offline fallback
│   │   ├── domain/
│   │   │   ├── model/JuzCatalogDataTest.kt           # 30-Juz Medina Mushaf page boundaries
│   │   │   └── usecase/
│   │   │       ├── GetSurahDetailUseCaseTest.kt      # Boundary & argument validation (1..114)
│   │   │       ├── GetMushafPageUseCaseTest.kt       # Boundary & argument validation (1..604)
│   │   │       ├── ManageBookmarksUseCaseTest.kt     # Bookmark lifecycle & validation
│   │   │       └── ManageLastReadUseCaseTest.kt      # Recitation progress lifecycle & validation
│   │   └── presentation/
│   │       ├── QuranSpreadCoordinateTest.kt          # Dual-spread k = (P-1)/2 coordinate math
│   │       ├── QuranViewModelTest.kt                 # Single verse search & basic audio
│   │       ├── QuranViewModelCatalogTest.kt          # 114-Surah catalog, search & Quick Jump
│   │       ├── QuranViewModelReaderTest.kt           # Dual-mode reader transitions & Khushu mode
│   │       ├── QuranViewModelAdaptiveTest.kt         # Responsive layout state preservation
│   │       └── QuranViewModelRecitationTest.kt       # End-to-end recitation user journeys
│   └── widget/                                       # Glance widget & WorkManager tests
└── testutil/
    ├── FakeLocalRecitationCache.kt                   # In-memory LocalRecitationCache test double
    └── MockHttpClient.kt                             # Ktor MockEngine factory helper
```

### 12.2 Multi-Edition Network Testing with Ktor MockEngine
- **Multi-Edition Response Zipping:** Verifies that raw multi-edition arrays combining Uthmani Arabic script (`quran-uthmani`), translation (`en.asad` / `id.indonesian`), and audio recitation URLs (`ar.alafasy`) merge deterministically into typed `SurahDetail` and `MushafPage` domain entities without dropped verses or misaligned audio URLs.
- **Offline Catalog Fallback:** Simulates network connection drops (`IOException` / HTTP 500) during 114-Surah catalog queries to guarantee automatic fallback to `FakeLocalRecitationCache`, emitting `AppResult.Success(cachedList)` without user-facing crash.

### 12.3 Test Double Architecture
- **`FakeLocalRecitationCache`:** Thread-safe, in-memory implementation of `LocalRecitationCache` isolating persistence tests from Android `SharedPreferences` and iOS `NSUserDefaults`.
- **`FakeAudioPlayer`:** Controllable test double implementing `AudioPlayer`, recording `playQueue`, `pause`, `resume`, `stop`, speed, and repeat mode calls while exposing a mutable `_audioState` StateFlow to simulate real-time playback transitions.

### 12.4 Coroutine & ViewModel State Verification Conventions
- **Deterministic Scheduling:** All ViewModel test suites install `StandardTestDispatcher` via `Dispatchers.setMain(testDispatcher)` in `@BeforeTest` and reset via `Dispatchers.resetMain()` in `@AfterTest`.
- **Virtual Time Advancement:** Test bodies execute within `runTest(testDispatcher)` using `advanceUntilIdle()` to deterministically process asynchronous coroutine jobs launched in `viewModelScope`.

### 12.5 Gradle Test Execution Protocol
In accordance with the `androidMultiplatformLibrary` Gradle plugin in `shared/build.gradle.kts`, host JVM verification is executed using:
```bash
./gradlew :shared:testAndroidHostTest
```
All unit tests in `commonTest` run against the host JVM with zero emulator/simulator requirements.





