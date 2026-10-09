# Assignment Learning (Android – Kotlin / Jetpack Compose)

Run: open in Android Studio, sync, run `app`. Login: `demo@gmail.com` / `password123`. Unit tests (Mockito): `./gradlew test`.
Offline demo: load the course list once, enable airplane mode, relaunch → saved courses show with an "Offline" banner.

## 1. Architecture
Clean Architecture + MVVM with Hilt: `presentation (Compose + ViewModel) → domain (use cases, repository interfaces, models) → data (API + Room)`.
Domain has no Android/Room dependencies, so business rules and ViewModels are unit-tested with Mockito mocks. UI state is a single
`StateFlow` (sealed Loading/Empty/Error/Success on the dashboard). Hilt gives constructor injection and swappable implementations.

## 2. Offline Support
Room is the single source of truth; UI observes DB Flows only. `refreshCourses()` fetches from the API and upserts in one transaction.
If refresh fails while a cache exists, cached courses stay visible with an offline banner (error screen only when no cache).
Lesson completion is written locally, progress recomputed in the same transaction, and preserved across refreshes.

## 3. Security
Access token in memory; refresh token encrypted with an Android Keystore key (AES-GCM) in EncryptedSharedPreferences/DataStore, never logged.
Short-lived tokens with rotation, TLS + optional cert pinning, exclude token storage from backups, R8, wipe on logout / 401.

## 4. Scale (1M users, hundreds of courses)
1. Pagination (Paging 3 + RemoteMediator) and server-side filtering.
2. Remove N+1 lesson calls: lazy-load lessons on details, or one batched/delta (`updatedSince`) endpoint.
3. Offline progress sync via WorkManager queue, idempotent POSTs, conflict rules; ETag caching + CDN.
4. Observability & safe releases: Crashlytics, API metrics, feature flags, staged rollout, Baseline Profiles.
5. Multi-module (feature/core/data) for build times and team scaling; Room migration tests.

## 5. Second Platform (iOS)
SwiftUI + MVVM (`@Observable`), async/await. Same layers: use cases and `CourseRepository` protocol over a `URLSession` client and
SwiftData/GRDB as source of truth; `NavigationStack`; tokens in Keychain; XCTest with mock protocols; `NWPathMonitor` for connectivity.
