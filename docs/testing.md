# Testing

This document describes the unit tests and how to run them.

## Test setup

| Item | Value |
|---|---|
| Test library | `kotlin.test` (`libs.kotlin.test.js`) |
| Runner | Karma |
| Browser | Chrome Headless |
| Source folder | `libs/<module>/src/test/kotlin` |

Each module sets the test runner in its `build.gradle.kts`:

```kotlin
kotlin {
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
    }
    sourceSets {
        named("jsTest") {
            kotlin.srcDir("src/test/kotlin")
            dependencies {
                implementation(libs.kotlin.test.js)
            }
        }
    }
}
```

The tests run in a real browser. Thus you must install Chrome or Chromium.

## Run the tests

| Command | Effect |
|---|---|
| `./gradlew allTests` | Runs the tests of all modules. |
| `./gradlew allTests --continue` | Runs all tests. It does not stop at the first module that fails. CI uses this command. |
| `./gradlew :<module>:allTests` | Runs the tests of one module, for example `./gradlew :livedata:allTests`. |
| `./gradlew check` | Runs the tests and `ktlint`. |

The Gradle project name of a module is the folder name in `libs/`.

## Test reports

Gradle writes an HTML report for each module:

```
libs/<module>/build/reports/tests/allTests/index.html
```

In CI, `tools/devops/remove_success_test_report` removes the reports of the modules that passed. Then CI uploads the remaining reports as the `test-result` artifact. Refer to [CI and release](ci-and-release.md).

## Modules with tests

| Module | Test files | What the tests cover |
|---|---|---|
| `graphicsgeo` | `RectTest` | `Rect` operations. |
| `lifecycle` | `LifecycleOwnerTest` | The lifecycle states. |
| `livedata` | `LiveDataTest`, `MediatorLiveDataTest`, `TransformLiveDataTest`, `DistinctOnlyLiveDataTest`, `NonNullOnlyLiveDataTest` | The `LiveData` classes and the operators. `MockLifecycleOwner` is a helper. |
| `monobitmap` | `NinePatchDrawableTest`, `RepeatableRangeTest` | The nine-patch drawable and its repeatable ranges. |
| `monobitmap-manager` | `RectangleBitmapFactoryTest`, `TextBitmapFactoryTest`, `LineBitmapFactoryTest` | The bitmaps of each shape type. |
| `monoboard` | `MonoBoardTest`, `PainterBoardTest`, `CrossingResourcesTest` | The board, the chunks and the crossing characters. |
| `shape` | `GroupTest`, `RectangleTest`, `TextTest`, `LineTest`, `LineHelperTest`, `ShapeManagerTest`, `ShapeConnectorUseCaseTest`, `SerializableLineConnectorTest`, `GroupSerializationTest`, `StraightStrokeDashPatternTest`, `QuickListTest`, `TwoWayQuickMapTest` | The shapes, the shape manager, the connectors and the serialization. |

The other modules have no tests. This includes `statemanager`, the UI modules and the storage modules.

## Write a test

1. Put the test file in `libs/<module>/src/test/kotlin`, in the same package as the class under test.
2. Name the class `<ClassName>Test`.
3. Add a function with the `@Test` annotation for each case.
4. Use the functions of `kotlin.test`, for example `assertEquals` and `assertTrue`.
5. If the module has no tests yet, add the `jsTest` source set and the `testTask` block to its `build.gradle.kts`.

Example:

```kotlin
package mono.graphics.board

import kotlin.test.Test
import kotlin.test.assertEquals
import mono.graphics.geo.Rect

class MonoBoardTest {
    private val target = MonoBoard().apply {
        clearAndSetWindow(Rect.byLTWH(-100, -100, 200, 200))
    }

    @Test
    fun testFill() {
        target.fill(Rect.byLTWH(1, 1, 3, 3), 'A', Highlight.NO)
        assertEquals(1, target.boardCount)
    }
}
```

### Tips

- To compare a drawing, compare `toString()` of the board or the bitmap with a multi-line string. Use `trimMargin()`.
- For a class that needs a `LifecycleOwner`, use a subclass of `LifecycleOwner`. `MockLifecycleOwner` is in the `livedata` tests only. Other modules must make their own.
- The tests run in a browser. Thus `document` and `window` are available. But most tested classes do not use them.
