# Actions and key commands

This document describes how the toolbar, the menus and the keyboard send actions. For the list of keys, refer to [Keyboard shortcuts](keyboard-shortcuts.md).

## Flow

```
keydown ─▶ KeyCommandController ─▶ KeyCommand ─▶ ActionManager ─┬─▶ RetainableActionType ─▶ MouseInteractionController
toolbar, menu, Format panel ────────────────────▶ ActionManager ─┴─▶ OneTimeActionType ─▶ OneTimeActionHandler
```

| Class | Module | Responsibility |
|---|---|---|
| `KeyCommandController` | `keycommand` | Changes keyboard events into `KeyCommand` values. |
| `ActionManager` | `action-manager` | Changes key commands into actions. Keeps the two action `LiveData` objects. |
| `OneTimeActionHandler` | `statemanager` | Does the work for each one-time action. |

## Action types

`ActionManager` has two kinds of actions.

### RetainableActionType

A retainable action is the current tool. It stays until a different tool is selected.

| Value | Tool | Mouse cursor |
|---|---|---|
| `IDLE` | Select | Default |
| `ADD_RECTANGLE` | Rectangle | Crosshair |
| `ADD_TEXT` | Text | Text |
| `ADD_LINE` | Line | Crosshair |

- `ActionManager.retainableActionLiveData` keeps the value.
- Call `setRetainableAction(type)` to change it.
- After you draw one shape, `MouseInteractionController` sets it to `IDLE` again.

### OneTimeActionType

A one-time action occurs one time. `setOneTimeAction(action)` sets the action, then sets `Idle` immediately. Thus a new observer does not get an old action.

| Group | Actions |
|---|---|
| `ProjectAction` | `NewProject`, `SwitchProject(id)`, `RemoveProject(id)`, `RenameCurrentProject(name)`, `SaveShapesAs`, `OpenShapes`, `ExportSelectedShapes` |
| `AppSettingAction` | `ChangeFontSize(isIncreased)`, `ShowFormatPanel`, `HideFormatPanel`, `ShowKeyboardShortcuts` |
| Selection | `SelectAllShapes`, `DeselectShapes` |
| Edit | `DeleteSelectedShapes`, `EditSelectedShapes`, `EditSelectedShape(shape)`, `TextAlignment` |
| Position and size | `MoveShapes(row, column)`, `ChangeShapeBound(left, top, width, height)` |
| Box style | `ChangeShapeFillExtra`, `ChangeShapeBorderExtra`, `ChangeShapeBorderDashPatternExtra`, `ChangeShapeBorderCornerExtra` |
| Line style | `ChangeLineStrokeExtra`, `ChangeLineStrokeDashPatternExtra`, `ChangeLineStrokeCornerExtra`, `ChangeLineStartAnchorExtra`, `ChangeLineEndAnchorExtra` |
| Order | `ReorderShape(orderType)` |
| Clipboard | `Copy(isRemoveRequired)`, `Duplicate`, `CopyText` |
| History | `Undo`, `Redo` |

## OneTimeActionHandler

`OneTimeActionHandler` observes `oneTimeActionLiveData`. It sends each action to a helper:

| Actions | Helper |
|---|---|
| `ProjectAction` | `FileRelatedActionsHelper`. Refer to [Storage and projects](storage-and-projects.md). |
| `AppSettingAction` | `AppSettingActionHelper`. It updates `AppUiStateManager`. |
| `Copy`, `Duplicate` | `ClipboardManager`. Refer to [Clipboard and export](clipboard-and-export.md). |
| `CopyText` | `FileRelatedActionsHelper.exportSelectedShapes(false)`. |
| `Undo`, `Redo` | `StateHistoryManager`. Refer to [Undo and redo](undo-and-redo.md). |
| All other actions | Private functions in `OneTimeActionHandler`. |

Notes:

- `DeleteSelectedShapes` removes each selected shape and its connectors. Then it clears the selection.
- `EditSelectedShapes` opens the text editor only if exactly one shape is selected and it is a `Text`.

## Key commands

### KeyCommandController

`KeyCommandController` listens to `keydown` and `keyup` on the page body.

1. On `keydown`, if the event target is the body, find the `KeyCommand`. Else, use `IDLE`. Thus the keys do nothing while an input field or the text editor has the focus.
2. If the command does not allow propagation, call `preventDefault()` and `stopPropagation()`.
3. Set the command on `keyCommandLiveData`.
4. If the command is repeatable, set `IDLE` immediately.
5. On `keyup`, set `IDLE`.

`ActionManager` observes the commands with `distinctUntilChange()`. Thus a key that you hold down sends one action, except for a repeatable command. A repeatable command goes back to `IDLE` after each event. Thus each repeated `keydown` sends a new action.

### KeyCommand

Each `KeyCommand` value has these properties:

| Property | Description |
|---|---|
| `keyCodes` | The key codes that start the command. |
| `commandKeyState` | `ON`, `OFF` or `ANY` for the command key. |
| `shiftKeyState` | `ON`, `OFF` or `ANY` for the Shift key. |
| `isKeyEventPropagationAllowed` | If `false`, the browser does not get the event. For example, Cmd+D does not add a bookmark. |
| `isRepeatable` | If `true`, the command repeats while you hold the key. |

The **command key** is the Meta key on macOS and the Ctrl key on other systems (`KeyboardEvent.commandKey` in `commons`).

`getCommandByKey()` uses the first value that matches the key code and the two key states. Thus the order of the enum values is important.

### Map from key command to action

| KeyCommand | Action |
|---|---|
| `SELECT_ALL` | `SelectAllShapes` |
| `DESELECTION` | `DeselectShapes` |
| `DELETE` | `DeleteSelectedShapes` |
| `MOVE_LEFT`, `MOVE_UP`, `MOVE_RIGHT`, `MOVE_DOWN` | `MoveShapes` by 1 cell |
| `FAST_MOVE_LEFT`, `FAST_MOVE_UP`, `FAST_MOVE_RIGHT`, `FAST_MOVE_DOWN` | `MoveShapes` by 5 cells |
| `ADD_RECTANGLE`, `ADD_TEXT`, `ADD_LINE` | The retainable action of the tool |
| `SELECTION_MODE` | The retainable action `IDLE` |
| `ENTER_EDIT_MODE` | `EditSelectedShapes` |
| `COPY` | `Copy(isRemoveRequired = false)` |
| `CUT` | `Copy(isRemoveRequired = true)` |
| `DUPLICATE` | `Duplicate` |
| `COPY_TEXT` | `CopyText` |
| `UNDO`, `REDO` | `Undo`, `Redo` |
| `SHIFT_KEY` | No action. `MonoSketchApplication` sends the Shift state to `MouseEventObserver`. |

Paste (Cmd+V) is not a key command. The browser sends a `paste` event. Refer to [Clipboard and export](clipboard-and-export.md).

### Add a keyboard shortcut

1. Add a value to `KeyCommand`. Put it before other values that use the same key with less strict key states.
2. In `ActionManager.onKeyEvent()`, map the value to an action.
3. If the action is new, add it to `OneTimeActionType` and handle it in `OneTimeActionHandler`.
4. Add the shortcut to the `KeyboardShortcuts` panel in `ui-toolbar`.
5. Update [Keyboard shortcuts](keyboard-shortcuts.md).

## Debug commands

In a debug build, `ActionManager.installDebugCommand()` adds `window.cmd`. Call it in the browser console with the class name of an action:

```js
cmd("Undo")
cmd("SaveShapesAs")
```

`DebugCommandController` supports only actions without parameters:

`SaveShapesAs`, `OpenShapes`, `ExportSelectedShapes`, `ShowKeyboardShortcuts`, `SelectAllShapes`, `DeselectShapes`, `DeleteSelectedShapes`, `EditSelectedShapes`, `CopyText`, `Undo`, `Redo`.
