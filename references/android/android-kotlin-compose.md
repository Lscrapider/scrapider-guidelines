# Android Kotlin Compose Reference

Use this reference for Android code. Follow the repository's existing UI technology first. For a greenfield Android UI or a feature with no established UI convention, prefer Kotlin and Jetpack Compose unless the user asks otherwise.

When implementing or reviewing Compose UI, use an available Compose-specific expert skill or
capability first. If the host provides none, apply this reference directly.

For Android UI implementation from screenshots, mockups, design images, or existing product UI, also load `android-ui-implementation-from-design.md`.

For Android product UI design or screen generation without a fixed design image, also load `android-product-ui-design.md`.

## Contents

- [Core Rules](#core-rules)
- [Android Architecture](#android-architecture)
- [Backend Field And UI Copy Rules](#backend-field-and-ui-copy-rules)
- [Backend Error Handling](#backend-error-handling)
- [Icon And Visual Asset Rules](#icon-and-visual-asset-rules)
- [Compose Rules](#compose-rules)
- [Feature Modularization](#feature-modularization)
- [File Organization](#file-organization)

## Core Rules

- Inspect the existing Android project structure before adding files.
- Follow the repository's existing architecture, such as MVVM, MVI, clean architecture, feature modules, or package-by-feature.
- Use Kotlin for new Android code unless the repository is explicitly Java-only or the user asks for Java.
- Follow the existing UI stack for changes to an established feature. Do not introduce Compose into a View/XML feature merely because the request leaves UI technology unspecified.
- For a greenfield feature with no repository convention, prefer Jetpack Compose over introducing a legacy View/XML stack.
- Keep Composables focused on UI rendering and UI events. Put business logic in ViewModel, state holders, use cases, or existing project layers.
- Do not pass raw network DTOs directly into Composables. Map backend responses to UI state or UI models first.

## Android Architecture

Follow the repository's established feature architecture first. For a new Compose feature with no project pattern, use ViewModel plus unidirectional data flow when the feature owns non-trivial UI state. Add a Repository only for a real data-access or data-aggregation boundary, and add a UseCase only for complex or reused domain rules.

Choose only the files whose responsibilities actually exist. Related small state, event, and effect types may remain together when that is clearer than creating one file per type:

- `XxxScreen.kt`: Compose entry and screen-level UI composition.
- `XxxViewModel.kt`: screen state, event handling, and lightweight orchestration.
- `XxxUiState.kt`: immutable state exposed from ViewModel to UI.
- `XxxUiEvent.kt`: user actions sent from UI to ViewModel when events are non-trivial.
- `XxxUiEffect.kt`: one-shot navigation, toast, snackbar, permission, or dialog effects when needed.
- `XxxRepository.kt`: feature or domain data aggregation.
- `XxxDataSource.kt`, `XxxApi.kt`, or SDK wrapper files: concrete backend, local store, cache, map, location, weather, or persistence access.
- `XxxMapper.kt`: DTO, entity, SDK object, domain model, or UI model conversion when conversion is not trivial.
- `XxxUseCase.kt` or domain files: complex business rules, cross-feature reuse, or orchestration too large for a ViewModel.

Use this dependency direction:

```text
Screen / Composable -> ViewModel -> Repository / UseCase -> DataSource / Api / SDK / Local Store
```

Use this UI state flow:

```text
ViewModel exposes UiState -> UI renders UiState -> UI emits Event -> ViewModel handles Event -> ViewModel updates UiState
```

- Keep `Screen` and Composable files in the view layer only.
- Do not call backend APIs, map SDKs, location SDKs, weather SDKs, databases, or persistence APIs from Composables.
- Do not put DTO mapping, SDK result parsing, repository calls, navigation decisions, or persistence logic directly in Composables.
- Keep ViewModel as the page interaction entry point.
- Keep repositories and data sources out of the UI package unless the repository already uses that structure.
- Do not create pass-through files, empty layers, or architecture folders only for appearance.
- Add a file or layer only when it owns real state, behavior, mapping, data access, orchestration, or reuse.

## Backend Field And UI Copy Rules

- Do not show raw backend field names, raw enum values, protocol values, debug fields, trace IDs, request IDs, stack traces, or development-only negotiation fields on user-facing screens.
- Backend DTO fields must be mapped to product-facing UI state, labels, and copy before rendering.
- If a backend value is business data that should be shown, format it through the UI layer instead of displaying raw protocol values.
- Do not expose implementation terms such as `statusCode`, `bizType`, `errorMsg`, `traceId`, `debugMessage`, `rawStatus`, or internal enum names in visible UI.
- Keep backend contract objects separate from UI display models when the fields are not already product-facing.

## Backend Error Handling

- Android must use a unified error-code mapping for backend API failures.
- Do not directly display backend error messages, exception names, response bodies, stack traces, or raw error fields to users.
- Convert backend error codes into user-facing messages through the project's existing error mapper, error state, or resource string system.
- Unknown or unmapped backend errors must use a generic fallback message.
- Preserve raw backend error details only in logs, telemetry, or debug tooling when appropriate; do not surface them in normal UI.
- Keep retry, empty, loading, and error states explicit in UI state.

Example shape:

```kotlin
data class UiError(
    val code: String,
    val messageRes: Int
)
```

```kotlin
fun BackendError.toUiError(): UiError =
    when (code) {
        "USER_NOT_FOUND" -> UiError(code, R.string.error_user_not_found)
        "TOKEN_EXPIRED" -> UiError(code, R.string.error_token_expired)
        else -> UiError(code, R.string.error_generic)
    }
```

## Icon And Visual Asset Rules

- Prefer the existing design system or approved asset set, and keep one compatible visual family
  within a feature.
- Reuse matching VectorDrawable, SVG, Compose vector, or Material assets. Do not hand-draw ad hoc
  vector paths to approximate custom branding.
- Generate or request a custom bitmap or vector only when the product or target design needs a
  unique visual and no approved match exists.
- Ask about asset direction only when the choice would materially affect visual identity or design
  fidelity; otherwise follow the established design system.

## Compose Rules

- Use state hoisting and immutable UI state where practical.
- Prefer `StateFlow` or the project's existing state mechanism from ViewModel to UI.
- Keep side effects in Compose explicit with APIs such as `LaunchedEffect` only when needed.
- Avoid doing network calls, database operations, or business orchestration inside Composables.
- Extract reusable Composables only when reuse or readability justifies it.
- Keep preview/sample data separate from backend DTOs and production fixtures.

## Feature Modularization

- Organize Android code by business feature when the project structure supports it.
- Prefer feature modules or feature packages for independent product areas instead of concentrating unrelated screens and logic in the app module.
- Keep shared infrastructure in explicit shared modules or packages, such as `core`, `common`, `designsystem`, `network`, or `data`, following the repository's existing naming.
- Do not create new Gradle modules only for appearance. Add a module only when it has a clear feature boundary, dependency boundary, build boundary, or reuse reason.
- Keep each Kotlin file cohesive around one primary responsibility. Small related types may remain in the same file when separating them would add navigation without clarifying ownership.
- A small cohesive feature may keep its Screen, state, event, effect, and lightweight ViewModel
  together when that matches the repository and is easier to navigate.
- Split a file when it mixes unrelated rendering, orchestration, DTO mapping, fake data, or reusable
  components, or when its size materially impairs review, navigation, or targeted changes.

## File Organization

Follow the existing project structure first. If there is no clear structure, prefer feature-based packages:

```text
feature/
  home/
    HomeScreen.kt
    HomeViewModel.kt
    HomeUiState.kt
    HomeErrorMapper.kt
  profile/
    ProfileScreen.kt
    ProfileViewModel.kt
    ProfileUiState.kt
```

For shared UI:

```text
ui/
  components/
  theme/
  state/
```

Do not place unrelated screens, ViewModels, DTO mappers, and UI components in one flat package.
