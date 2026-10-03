# LiveData and lifecycle

This document describes the `livedata` and `lifecycle` modules. Most modules use them to send state changes.

The design is similar to Android `LiveData`, but it is simpler. It is not the Android library.

## Lifecycle

### LifecycleOwner

`LifecycleOwner` is an abstract class. It has three states:

| State | Meaning |
|---|---|
| `INITIAL` | The owner is made, but not started. |
| `STARTED` | `onStart()` was called. |
| `STOPPED` | `onStop()` was called. The owner cannot start again. |

| Function | Effect |
|---|---|
| `onStart()` | Sets the state to `STARTED`. Calls `onStart()` on each observer. Then calls `onStartInternal()`. |
| `onStop()` | Sets the state to `STOPPED`. Calls `onStopInternal()`. Then calls `onStop()` on each observer and removes all observers. |
| `addObserver(observer)` | Adds a `LifecycleObserver`. If the owner is started, it calls `observer.onStart()` immediately. If the owner is stopped, it ignores the observer. |

`MonoSketchApplication` is the root `LifecycleOwner`. `window.onload` starts it. The application never stops it.

### LifecycleObserver

`LifecycleObserver` is an interface with two functions: `onStart()` and `onStop()`. Both have an empty default body.

## LiveData

### Classes

| Class | Description |
|---|---|
| `LiveData<T>` | A value that you can observe. It is read-only. |
| `MutableLiveData<T>` | A `LiveData` with a public setter for `value`. |
| `MediatorLiveData<T>` | A `MutableLiveData` that observes other `LiveData` objects. |

### Rules

- Each `LiveData` must have an initial value. There is no "no value" state.
- When you set a value, all observers get it. `LiveData` does not compare the new value with the old value. Use `distinctUntilChange()` to skip equal values.
- When you start to observe, the observer gets the current value immediately.
- An observer stops when its `LifecycleOwner` stops.
- If the `LifecycleOwner` is already stopped, `observe()` does nothing.

### Observe a value

```kotlin
fontSizeLiveData.observe(lifecycleOwner) { fontSize ->
    canvas.setFont(fontSize)
}
```

The `observe()` function has the parameter `throttleDurationMillis`:

| Value | Effect |
|---|---|
| `-1` (default) | The observer runs immediately for each value. |
| `0` | The observer runs one time on the next animation frame, with the last value. |
| More than `0` | The observer runs one time after the duration, with the last value. |

Use `0` to merge many changes into one update. For example, `MainStateManager` uses `throttleDurationMillis = 0` for the redraw request.

### Operators

| Operator | Result |
|---|---|
| `map { }` | A `LiveData` with the transformed value. |
| `distinctUntilChange()` | A `LiveData` that skips a value when it is equal to the current value. |
| `filterNotNull()` | A `LiveData` that skips `null` values. |
| `combineLiveData(a, b) { x, y -> }` | A `LiveData` that changes when `a` or `b` changes. |
| `combineLiveData(a, b, c, ...) { list -> }` | The same for three or more sources. |

**Note:** The operators observe the source permanently. They do not use the lifecycle of the final observer. Do not make an operator chain in a loop or a click handler. Make the chain one time and keep it in a property.

### Naming convention

Keep the mutable object private. Show the read-only object to the other classes:

```kotlin
private val fontSizeMutableLiveData: MutableLiveData<Int> = MutableLiveData(13)
val fontSizeLiveData: LiveData<Int> = fontSizeMutableLiveData
```

Use the suffix `LiveData` for the public property. Use the suffix `MutableLiveData` for the private property.

## Use with Compose for Web

The `ui-compose-ext` module has the extension `LiveData<T>.toState(lifecycleOwner)`. It changes a `LiveData` into a Compose `State`. Use it to show a `LiveData` value in a composable function.

## Tests

The `livedata` and `lifecycle` modules have unit tests in `src/test/kotlin`. Refer to [Testing](testing.md).
