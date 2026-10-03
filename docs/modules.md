# Module reference

This document lists all Gradle modules in MonoSketch. For the layers, refer to [Architecture](architecture.md). For how to add a module, refer to [Build system](build-system.md#add-a-module).

## Module list

All modules except `app` are in the `libs/` folder.

### Entry

| Module | Purpose | Main classes |
|---|---|---|
| root project | The entry point. It makes the executable `MonoSketch.js`. | `main()` in `src/main/kotlin/app.kt` |
| `app` | Makes the main objects and connects them. | `MonoSketchApplication`, `MonoSketchAppModel` |

### Coordination

| Module | Purpose | Main classes |
|---|---|---|
| `statemanager` | Connects the shape model, the rendering, the input and the storage. It contains the mouse commands, the action handler, autosave and undo. | `MainStateManager`, `CommandEnvironment`, `MouseCommandFactory`, `OneTimeActionHandler`, `StateHistoryManager`, `ClipboardManager`, `FileMediator` |

### UI

| Module | Purpose | Main classes |
|---|---|---|
| `ui-toolbar` | The navigation bar, the Format panel and the keyboard shortcut panel. | `NavBarViewController`, `ShapeToolViewController2`, `ShapeToolViewModel`, `KeyboardShortcuts` |
| `ui-modal` | Modals, dialogs, menus and tooltips. | `showRecentProjectsModal()`, `showExitingProjectDialog()`, `EditTextModal`, `DropDownMenu()`, `Dialog()`, `NoBackgroundModal()`, `Tooltip` |
| `ui-canvas` | The HTML canvases and the mouse events. | `CanvasViewController`, `BoardCanvasViewController`, `MouseEventObserver` |
| `export-shapes-modal` | The modal that shows the text export. | `ExportShapesModal` |
| `ui-compose-ext` | Helpers for Compose for Web: SVG icons and the conversion from `LiveData` to Compose `State`. | `LiveData.toState()`, `Icons` |
| `ui-app-state-manager` | The UI settings: theme, font size, scroll mode, Format panel visibility. | `AppUiStateManager`, `ScrollMode` |
| `ui-theme` | The theme mode and the canvas colors. | `ThemeManager`, `ThemeMode`, `ThemeColor` |

### Input

| Module | Purpose | Main classes |
|---|---|---|
| `action-manager` | Sends the actions from the toolbar, the menus and the keyboard. | `ActionManager`, `RetainableActionType`, `OneTimeActionType` |
| `keycommand` | Changes keyboard events into key commands. | `KeyCommandController`, `KeyCommand` |
| `browser-manager` | Keeps the URL and the page title in sync with the current project. | `BrowserManager` |

### Storage

| Module | Purpose | Main classes |
|---|---|---|
| `store-dao` | Reads and writes projects and settings. | `WorkspaceDao`, `WorkspaceObjectDao` |
| `store-manager` | A low-level wrapper around the browser local storage. | `StoreManager`, `StorageDocument` |

### Shape model

| Module | Purpose | Main classes |
|---|---|---|
| `shape` | The shapes, the shape tree, the commands, the styles, the line connectors and the serializable shape classes. | `AbstractShape`, `ShapeManager`, `Command`, `ShapeExtraManager`, `ShapeConnector`, `AbstractSerializableShape` |
| `shape-selection` | The selected shapes and the focused shape. | `SelectedShapeManager` |
| `shape-clipboard` | Reads the paste event from the browser. | `ShapeClipboardManager`, `ClipboardObject` |
| `shape-serialization` | The `.mono` file format, and JSON conversion of shapes and connectors. | `MonoFile`, `ShapeSerializationUtil` |
| `shape-interaction-bound` | The handles that you drag to resize a shape or to change a line. | `ScalableInteractionBound`, `LineInteractionBound` |
| `shapesearcher` | Finds the shapes at a position or in an area. | `ShapeSearcher` |
| `monobitmap-manager` | Makes and caches the bitmap of each shape. | `MonoBitmapManager`, `RectangleBitmapFactory`, `TextBitmapFactory`, `LineBitmapFactory` |

### Graphics

| Module | Purpose | Main classes |
|---|---|---|
| `monoboard` | The infinite grid of characters. It merges crossing box-drawing characters. | `MonoBoard`, `PainterBoard`, `CrossingResources`, `Highlight` |
| `monobitmap` | The characters of one shape. | `MonoBitmap` |

### Foundation

| Module | Purpose | Main classes |
|---|---|---|
| `commons` | Shared helpers: characters, key codes, mouse cursors and timers. | `Characters`, `Key`, `MouseCursor` |
| `graphicsgeo` | Geometry types. | `Point`, `PointF`, `DirectedPoint`, `Rect`, `Size`, `MousePointer` |
| `livedata` | Observable values. | `LiveData`, `MutableLiveData`, `MediatorLiveData` |
| `lifecycle` | Lifecycle owners and observers. | `LifecycleOwner`, `LifecycleObserver` |
| `uuid` | Makes unique IDs for shapes and projects. | `UUID` |
| `build-environment` | The build mode. | `Build` |
| `html-dsl` | A small DSL to make DOM elements in Kotlin. | `Div()`, `Span()`, `SvgIcon()` |

## Dependencies

This table shows the project dependencies of each module in the `jsMain` source set.

| Module | Depends on |
|---|---|
| `app` | `action-manager`, `browser-manager`, `commons`, `graphicsgeo`, `keycommand`, `lifecycle`, `livedata`, `monoboard`, `monobitmap`, `monobitmap-manager`, `shape`, `shape-clipboard`, `shape-selection`, `shape-serialization`, `statemanager`, `store-manager`, `ui-app-state-manager`, `ui-canvas`, `ui-toolbar` |
| `statemanager` | `action-manager`, `build-environment`, `commons`, `export-shapes-modal`, `graphicsgeo`, `html-dsl`, `keycommand`, `lifecycle`, `livedata`, `monobitmap`, `monobitmap-manager`, `monoboard`, `shape`, `shape-clipboard`, `shape-interaction-bound`, `shape-selection`, `shape-serialization`, `shapesearcher`, `store-dao`, `store-manager`, `ui-app-state-manager`, `ui-canvas`, `ui-modal`, `ui-toolbar`, `uuid` |
| `ui-toolbar` | `action-manager`, `commons`, `graphicsgeo`, `html-dsl`, `lifecycle`, `livedata`, `shape`, `store-dao`, `ui-app-state-manager`, `ui-compose-ext`, `ui-modal`, `ui-theme` |
| `ui-modal` | `browser-manager`, `commons`, `html-dsl`, `lifecycle`, `livedata`, `ui-compose-ext` |
| `ui-canvas` | `commons`, `graphicsgeo`, `html-dsl`, `lifecycle`, `livedata`, `monoboard`, `shape-interaction-bound`, `ui-app-state-manager`, `ui-modal`, `ui-theme` |
| `export-shapes-modal` | `commons`, `graphicsgeo`, `html-dsl`, `lifecycle`, `livedata`, `monobitmap`, `monoboard`, `shape` |
| `ui-compose-ext` | `lifecycle`, `livedata`, `ui-theme` |
| `ui-app-state-manager` | `lifecycle`, `livedata`, `store-manager`, `ui-theme` |
| `ui-theme` | `livedata` |
| `action-manager` | `build-environment`, `commons`, `keycommand`, `lifecycle`, `livedata`, `shape` |
| `keycommand` | `build-environment`, `commons`, `livedata` |
| `browser-manager` | `lifecycle`, `livedata`, `store-dao` |
| `store-dao` | `commons`, `graphicsgeo`, `shape`, `shape-serialization`, `store-manager` |
| `store-manager` | none |
| `shape` | `commons`, `graphicsgeo`, `livedata`, `monobitmap`, `uuid`, kotlinx-serialization-json |
| `shape-selection` | `livedata`, `shape` |
| `shape-clipboard` | `graphicsgeo`, `html-dsl`, `livedata`, `shape` |
| `shape-serialization` | `commons`, `graphicsgeo`, `shape` |
| `shape-interaction-bound` | `commons`, `graphicsgeo`, `shape` |
| `shapesearcher` | `commons`, `graphicsgeo`, `monobitmap`, `shape` |
| `monobitmap-manager` | `commons`, `graphicsgeo`, `monobitmap`, `shape` |
| `monoboard` | `build-environment`, `commons`, `graphicsgeo`, `monobitmap` |
| `monobitmap` | `commons`, `graphicsgeo` |
| `livedata` | `commons`, `lifecycle` |
| `commons` | none |
| `graphicsgeo` | none |
| `lifecycle` | none |
| `uuid` | none |
| `build-environment` | none |
| `html-dsl` | none |

The `ui-compose-ext` module declares a dependency on `ui-theme`, but its code does not use it.

## Modules with Compose for Web

These modules apply the Compose plugins:

- `ui-compose-ext`
- `ui-modal`
- `ui-toolbar`

## Modules with tests

These modules have a `src/test/kotlin` folder:

- `graphicsgeo`
- `lifecycle`
- `livedata`
- `monobitmap`
- `monobitmap-manager`
- `monoboard`
- `shape`

For more information, refer to [Testing](testing.md).
