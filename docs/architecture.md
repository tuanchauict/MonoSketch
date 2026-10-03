# Architecture

This document describes the structure of the MonoSketch code. It shows the layers, the startup sequence and the main data flows.

## Layers

The code has eight layers. A module uses only modules in its own layer or in lower layers. Layer 1 is the top layer.

| Layer | Name | Modules |
|---|---|---|
| 1 | Entry | root project (`app.kt`), `app` |
| 2 | Coordination | `statemanager` |
| 3 | UI | `ui-toolbar`, `ui-modal`, `ui-canvas`, `export-shapes-modal`, `ui-compose-ext`, `ui-app-state-manager`, `ui-theme` |
| 4 | Input | `action-manager`, `keycommand`, `browser-manager` |
| 5 | Storage | `store-dao`, `store-manager` |
| 6 | Shape model | `shape`, `shape-selection`, `shape-clipboard`, `shape-serialization`, `shape-interaction-bound`, `shapesearcher`, `monobitmap-manager` |
| 7 | Graphics | `monoboard`, `monobitmap` |
| 8 | Foundation | `commons`, `graphicsgeo`, `livedata`, `lifecycle`, `uuid`, `build-environment`, `html-dsl` |

Some dependencies cross many layers. For example, `shape` uses `monobitmap`, because a shape keeps its style characters. `shapesearcher` uses `monobitmap`, because it does hit tests on the rendered characters.

For the dependencies of each module, refer to [Module reference](modules.md).

## Main classes

| Class | Module | Responsibility |
|---|---|---|
| `MonoSketchApplication` | `app` | Creates the main objects and connects them. It is the root `LifecycleOwner`. |
| `MainStateManager` | `statemanager` | Connects the shape model, the rendering, the input and the storage. It redraws the board. |
| `CommandEnvironment` | `statemanager` | The interface that mouse commands and action handlers use to change the state. |
| `ShapeManager` | `shape` | Keeps the shape tree. It runs the shape commands. |
| `SelectedShapeManager` | `shape-selection` | Keeps the selected shapes and the focused shape. |
| `MonoBitmapManager` | `monobitmap-manager` | Makes and caches the bitmap of each shape. |
| `MonoBoard` | `monoboard` | The infinite grid of characters. |
| `CanvasViewController` | `ui-canvas` | Draws the board on the HTML canvases. It sends the mouse events. |
| `ActionManager` | `action-manager` | Sends the actions from the toolbar, the menus and the keyboard. |
| `KeyCommandController` | `keycommand` | Changes keyboard events into key commands. |
| `OneTimeActionHandler` | `statemanager` | Does the work for each one-time action. |
| `StateHistoryManager` | `statemanager` | Saves the project, and keeps the undo and redo history. |
| `BrowserManager` | `browser-manager` | Keeps the project ID in the URL and the page title. |
| `AppUiStateManager` | `ui-app-state-manager` | Keeps the theme, the font size, the scroll mode and the Format panel visibility. |
| `NavBarViewController` | `ui-toolbar` | Shows the navigation bar. |
| `ShapeToolViewController2` | `ui-toolbar` | Shows the Format panel. |

## Startup sequence

The function `main()` in `src/main/kotlin/app.kt` starts the application:

1. `main()` makes a `MonoSketchApplication` object.
2. The constructor makes `MonoBoard`, `ShapeManager`, `SelectedShapeManager`, `MonoBitmapManager` and `AppUiStateManager`. `AppUiStateManager` applies the theme immediately. This prevents a flash of the wrong theme.
3. `main()` sets `window.onload` to `onStart()` and `window.onresize` to `onResize()`.

When the page is loaded, `onStart()` calls `onStartInternal()`. This function does these steps in sequence:

1. Make the `KeyCommandController` on the page body.
2. Make the `CanvasViewController` in `#monoboard-canvas-container` and `#monoboard-axis-container`.
3. Make the `ActionManager`. In a debug build, install `window.cmd`.
4. Make the `BrowserManager`. It reads the project ID from the URL.
5. Make the `MainStateManager`. It loads the project and starts to observe the changes.
6. Make the `NavBarViewController` in `#nav-toolbar`.
7. Make the `ShapeToolViewController2` in `#shape-tools`.
8. Call `onResize()` to set the window size.
9. Observe the active state of the page: `visibilitychange`, `focus` and `blur`.
10. Observe the theme. When the theme changes, redraw the full workspace.
11. Start to keep the URL and the page title up to date.
12. Observe the font size. Send it to the canvas.

## Data flows

The modules communicate with `LiveData`. Refer to [LiveData and lifecycle](livedata-and-lifecycle.md).

### Mouse input

```
mouse event ─▶ MouseEventObserver ─▶ MousePointer ─▶ MouseInteractionController
            ─▶ MouseCommandFactory ─▶ MouseCommand ─▶ CommandEnvironment ─▶ ShapeManager
```

For the details, refer to [Mouse interaction](mouse-interaction.md).

### Actions

```
toolbar, menu or key ─▶ ActionManager ─▶ OneTimeActionType ─▶ OneTimeActionHandler
                     ─▶ CommandEnvironment ─▶ ShapeManager
```

For the details, refer to [Actions and key commands](actions-and-key-commands.md).

### Redraw

1. A command changes a shape. `ShapeManager` increases the version of the shape and of its parent groups.
2. `ShapeManager.versionLiveData` sends the new version.
3. `MainStateManager` requests a redraw. A change of the visible window also requests a redraw.
4. The redraw runs one time on the next animation frame. The `throttleDurationMillis = 0` option merges many requests into one redraw.
5. `MainStateManager` clears the board and the shape searcher for the visible window.
6. It gets the bitmap of each shape from `MonoBitmapManager` and puts it on the board.
7. `CanvasViewController` draws the board on the canvas.

For the details, refer to [Rendering](rendering.md).

### Save and undo

1. `StateHistoryManager` observes the shape version.
2. When the version does not change for 300 ms, it records the state in the history.
3. It writes the project to local storage.

For the details, refer to [Storage and projects](storage-and-projects.md) and [Undo and redo](undo-and-redo.md).

### Changes in other tabs

When the page becomes active again, `MainStateManager` calls `reflectChangedFromLocal()`. This function loads the project from local storage again. Thus a change that you make in a different tab shows in this tab.

## Design principles

- **One source of truth.** `ShapeManager` keeps the shapes. The UI reads the shapes and sends actions. The UI does not change the shapes directly.
- **Commands change shapes.** All changes to the shape tree go through `ShapeManager.execute(Command)`.
- **Versions show changes.** Each shape has a `versionCode`. A change makes a new value. The caches use the version to know if they must update.
- **Lifecycle-bound observers.** Each observer has a `LifecycleOwner`. The observer stops when its owner stops.
- **No server.** All the data stays in the browser.
