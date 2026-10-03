# User interface

This document describes the HTML parts of the application: the navigation bar, the Format panel, the modals and the text editor. For the canvases, refer to [Rendering](rendering.md). For the use of each control, refer to [User guide](user-guide.md).

## Page structure

`src/main/resources/index.html` defines the containers. The Kotlin code fills them.

| Element ID | Content | Filled by |
|---|---|---|
| `main-nav` | The navigation bar. It is 48 px high. | Static HTML |
| `nav-brand` | The logo. | Static HTML |
| `nav-toolbar` | The tools, the project title and the app controls. | `NavBarViewController` |
| `workspace` | The board area, below the navigation bar. | Static HTML |
| `monoboard-canvas-container` | The board canvases. | `CanvasViewController` |
| `monoboard-axis-container` | The axis canvas. | `AxisCanvasViewController` |
| `shape-tools` | The Format panel. It is 250 px wide, on the right side. | `ShapeToolViewController2` |

Modals, menus and tooltips are added to `document.body` when they open. They are removed when they close.

## UI technologies

The UI uses two technologies:

| Technology | Module | Used by |
|---|---|---|
| Compose for Web | `ui-compose-ext` (helpers) | The navigation bar, the Format panel, the project modal, the menus and the dialogs. |
| HTML DSL | `html-dsl` | The text editor, the tooltips, the export modal and the keyboard shortcut panel. |

The HTML DSL makes DOM elements directly, for example `Div(classes = "...") { ... }`. New UI code uses Compose for Web.

To connect a `LiveData` to Compose, call `liveData.toState(lifecycleOwner)`. Refer to [LiveData and lifecycle](livedata-and-lifecycle.md).

## Navigation bar

`NavBarViewController` (module `ui-toolbar`) renders the navigation bar. It has three groups:

| Group | Composable | Content |
|---|---|---|
| Left | `MouseActionGroup` | The tool buttons. |
| Center | `ProjectManagerIcon`, `WorkingFileToolbar` | The project button and the project title. |
| Right | `ScrollModeButton`, `ThemeIcons`, `AppMenuIcon` | The scroll mode, the theme and the app menu. |

### Tool buttons

Each button sets a `RetainableActionType`. The tooltip shows the name and the key.

| Tooltip | Action |
|---|---|
| Select (V) | `IDLE` |
| Rectangle (R) | `ADD_RECTANGLE` |
| Text (T) | `ADD_TEXT` |
| Line (L) | `ADD_LINE` |

### Project title menu

Click the project title to open a menu:

| Item | Effect |
|---|---|
| **Rename** | Opens a text field below the title. |
| **Save As...** | Sends `SaveShapesAs`. |
| **Export Text** | Sends `ExportSelectedShapes`. |

In the rename field:

- Push Enter, or click outside the field, to keep the name.
- Push Escape to cancel.
- An empty name is ignored.

### Right controls

| Control | Effect |
|---|---|
| Scroll mode | Each click changes the mode: `BOTH`, then `VERTICAL`, then `HORIZONTAL`. |
| Theme | Changes the theme to light or dark. Only the icon of the other mode shows. |
| App menu | Opens a menu. Refer to the table below. |

| App menu item | Effect |
|---|---|
| Two **A** buttons | Decrease or increase the font size by 2. The range is 13 to 25. |
| **Show Format panel** | Shows the Format panel. It shows only when the panel is hidden. |
| **Hide Format panel** | Hides the Format panel. It shows only when the panel is visible. |
| **Keyboard shortcuts** | Shows the keyboard shortcut panel. |

## App UI state

`AppUiStateManager` (module `ui-app-state-manager`) keeps the UI state:

| State | Default | Saved |
|---|---|---|
| Format panel visibility | Visible | No |
| Scroll mode | `BOTH` | No |
| Font size | 13 | Yes, in `settings/font-size` |

To change the state, call `updateUiState()` with a `UiStatePayload`. The values that are not saved go back to the default when you reload the page.

## Project modal

Click the project button (tooltip "Manage projects") to open the project modal. `showRecentProjectsModal()` (module `ui-modal`) shows it.

The modal has these parts, from top to bottom:

1. A filter field. It gets the focus when the modal opens.
2. **New project** and **Import from file...**. These items are hidden while the filter field has text.
3. The list of projects.

| Item | Effect |
|---|---|
| **New project** | Sends `NewProject`. Then it opens the rename field. |
| **Import from file...** | Sends `OpenShapes`. |
| A project name | Sends `SwitchProject`. |
| "Open in new tab" icon | Opens the project in a new browser tab. |
| "Delete" icon | Shows `Delete "<name>"?` with a **Confirm** icon and a **Cancel** icon. **Confirm** sends `RemoveProject`. |

The list order:

- Without a filter, the most recently opened project is first.
- With a filter, the list shows the names that contain the filter text, in any case. The names are in alphabetical order.

The modal closes when you push Escape, click outside it, or change the window size.

## Format panel

`ShapeToolViewController2` renders the Format panel in `#shape-tools`. `ShapeToolViewModel` gives the state of each section.

| Section | Visible when | Actions |
|---|---|---|
| Reorder | One shape is selected. | `ReorderShape`: **Bring to Front**, **Bring Forward**, **Send Backward**, **Send to Back** |
| TRANSFORM | One shape is selected. | `ChangeShapeBound`. Width and height are available only for a rectangle or a text. |
| APPEARANCE | One rectangle, text or line is selected. Or the Rectangle, Text or Line tool is active. | Fill, Border, Stroke, Start head, End head, dash pattern, rounded corner |
| TEXT | One editable text is selected. Or the Text tool is active. | `TextAlignment`: Alignment and Position |
| Indicator | No other section is visible. | None. It shows "Select a shape for updating its properties here" and links. |

### Selected shape or default values

The panel works with one shape only. When you select two or more shapes, the panel shows only the indicator.

When no shape is selected and a drawing tool is active, the panel shows the default values. A change then updates `ShapeExtraManager`. The next new shape uses these values.

| Selection | Effect of a change in the panel |
|---|---|
| One shape | Changes that shape. |
| No shape, drawing tool active | Changes the default values for new shapes. |

## Text editor

`EditTextModal` (module `ui-modal`) is the text editor. `EditTextShapeHelper` (module `statemanager`) opens it.

- The editor is a `contenteditable` element over the text area of the shape.
- Each input changes the text of the shape at once.
- For a text box, each input also changes the height of the box.
- A paste in the editor inserts only plain text.
- A mouse wheel event does nothing while the editor is open.

The editor closes when you:

- Push Escape.
- Push Cmd+Enter.
- Click outside the editor.

## Dialogs and helpers

The `ui-modal` module has these parts:

| Part | Type | Description |
|---|---|---|
| `Dialog()` | Function | A dialog with a title, a message or custom content, and up to two buttons. |
| `showExitingProjectDialog()` | Function | The "Existing project" dialog, with **Replace** and **Keep both**. The name has a spelling error in the code. |
| `DropDownMenu()` | Function | A menu below an anchor element. |
| `NoBackgroundModal()` | Internal function | A modal without a dark background. The project modal uses it. |
| `showRecentProjectsModal()` | Function | The project modal. |
| `tooltip(text, position)` | Extension | Adds a tooltip to an element. It works for HTML DSL elements and Compose elements. |
| `EditTextModal` | Class | The text editor. |

The `ui-compose-ext` module has the shared Compose helpers, for example `Icons` and `onConsumeClick`.

## Keyboard shortcut panel

`KeyboardShortcuts` (module `ui-toolbar`) shows the shortcut panel. To open it, open the app menu and click **Keyboard shortcuts**.

The panel shows only some shortcuts. For the full list, refer to [Keyboard shortcuts](keyboard-shortcuts.md).

## Related documents

- [Actions and key commands](actions-and-key-commands.md): the actions that the controls send.
- [Theme and styling](theme-and-styling.md): the CSS of the controls.
- [Storage and projects](storage-and-projects.md): the project actions.
