# Android Kotlin Compose Reference

Use this reference for Android code. Prefer Kotlin for Android development. When the user does not specify a UI technology, use Jetpack Compose instead of XML/View-based UI.

When implementing or reviewing Compose UI, use `$compose-expert` first.

For Android UI implementation from screenshots, mockups, design images, or existing product UI, also load `android-ui-implementation-from-design.md`.

For Android product UI design or screen generation without a fixed design image, also load `android-product-ui-design.md`.

## Core Rules

- Inspect the existing Android project structure before adding files.
- Follow the repository's existing architecture, such as MVVM, MVI, clean architecture, feature modules, or package-by-feature.
- Use Kotlin for new Android code unless the repository is explicitly Java-only or the user asks for Java.
- Use Jetpack Compose by default when UI technology is not specified.
- Do not introduce XML layouts, ViewBinding, or legacy View UI for new screens unless the project already uses that pattern or the user asks for it.
- Keep Composables focused on UI rendering and UI events. Put business logic in ViewModel, state holders, use cases, or existing project layers.
- Do not pass raw network DTOs directly into Composables. Map backend responses to UI state or UI models first.

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
- Keep one Kotlin file focused on one responsibility: screen entry, UI state, ViewModel, mapper, route/navigation, reusable component, or domain model.
- Do not put a full feature's screen, state model, event model, ViewModel logic, DTO mapping, fake data, and reusable components into one Kotlin file.
- Split large Compose screens into private section Composables, feature components, state models, and mapper files when the file starts mixing unrelated responsibilities.
- Avoid file growth that makes review, navigation, or targeted changes difficult. Treat oversized Kotlin files as a design smell and split them by responsibility before extending them.

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
