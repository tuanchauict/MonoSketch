# Code style

This document describes the code style rules and the tools that check them.

## Tools

| Tool | Configuration | Command |
|---|---|---|
| ktlint 0.49.0 | `ktlint.gradle`, `.editorconfig` | `./gradlew ktlint` |
| ktlint formatter | `ktlint.gradle` | `./gradlew ktlintFormat` |
| IntelliJ IDEA | `.editorconfig` | Code > Reformat Code |

- `ktlint` checks all `**/*.kt` files.
- `./gradlew check` also runs `ktlint`.
- CI runs `ktlint` on each pull request. Refer to [CI and release](ci-and-release.md).

Run `./gradlew ktlintFormat` before you commit. Then run `./gradlew ktlint` to find the errors that the formatter cannot fix.

## EditorConfig rules

`.editorconfig` defines these rules:

| Files | Rule | Value |
|---|---|---|
| All | Character set | UTF-8 |
| All | Line ending | LF |
| All | Indent | 4 spaces |
| All | Maximum line length | 100 |
| All | Final newline | Yes |
| All | Trailing whitespace | Removed |
| `*.kt`, `*.kts` | Code style | Kotlin official (`KOTLIN_OFFICIAL`) |
| `*.kt`, `*.kts` | Continuation indent | 4 spaces |
| `*.kt`, `*.kts` | Wildcard imports | Not allowed |
| `*.kt`, `*.kts` | Trailing commas | Not allowed |
| `*.kt`, `*.kts` | Import order | All imports, then alias imports (`*, ^`) |
| `*.yml`, `*.yaml` | Indent | 2 spaces |

## File header

Each Kotlin file starts with a copyright header:

```kotlin
/*
 * Copyright (c) 2023, tuanchauict
 */
```

Some newer files use `2024` or `2023-2024`. Use the current year in a new file.

## Naming

| Item | Rule | Example |
|---|---|---|
| Package | Starts with `mono.` | `mono.graphics.board` |
| Class | `PascalCase` | `MonoBoard` |
| Test class | `<ClassName>Test` | `MonoBoardTest` |
| Function, property | `camelCase` | `clearAndSetWindow` |
| Constant | `UPPER_SNAKE_CASE` | `STANDARD_UNIT_SIZE` |
| `LiveData` property | Ends with `LiveData` | `selectedShapesLiveData` |
| Compose `State` property | Ends with `State` | `projectNameState` |
| Composable function | `PascalCase` | `ScrollModeButton` |
| HTML DSL function | `PascalCase` | `Div`, `Span` |

A file with composable functions or HTML DSL functions starts with `@file:Suppress("FunctionName")`. This stops the warning for `PascalCase` function names.

## Kotlin rules

- Give each class a KDoc comment that tells its responsibility.
- Use `internal` for classes that other modules do not use.
- Keep a mutable `LiveData` private. Expose it as a `LiveData`:

  ```kotlin
  private val fontSizeMutableLiveData = MutableLiveData(13)
  val fontSizeLiveData: LiveData<Int> = fontSizeMutableLiveData
  ```

- Use `when` with all cases of a sealed type. Add `.exhaustive` (from `commons`) to make the compiler check the cases when `when` is a statement.
- Use `sealed interface` or `sealed class` for a fixed set of values with data, for example `OneTimeActionType`.

## Long lines

Some lines cannot be short, for example SVG paths. Use a ktlint comment for these lines only:

```kotlin
"M7.436 20.61L7.275 3.914l12.296 11.29-7.165.235-4.97 5.168z" // ktlint-disable max-line-length
```

For a block of lines, use `/* ktlint-disable max-line-length */` and `/* ktlint-enable max-line-length */`.

## IDE setup

1. Open the project in IntelliJ IDEA.
2. Make sure that EditorConfig support is on (Settings > Editor > Code Style > Enable EditorConfig support).
3. Use the run configurations in `.run/`: **Run** and **Run with Python**. Refer to [Getting started](getting-started.md).
