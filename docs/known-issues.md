# Known issues

This document lists the known defects and limits in the code on `main`. Each item tells the location, the effect and a possible fix.

Status values:

| Status | Meaning |
|---|---|
| Confirmed | The code shows the defect. |
| Likely | The code shows the cause. Nobody tested the effect in a browser. |
| Limit | The code works as written, but the behavior is not complete. |

## Shapes and connectors

### Nested groups cause infinite recursion

| Item | Value |
|---|---|
| Status | Confirmed |
| Location | `ShapeManager.createAllShapeMapRecursive()` in `shape` |
| Effect | If the root has a group inside it, the function calls itself with the same group. The stack overflows. |
| Cause | The recursive call uses `group`. It must use `shape`. |
| Fix | Change `createAllShapeMapRecursive(group, map)` to `createAllShapeMapRecursive(shape, map)`. |

The UI cannot make a group. But a `.mono` file or clipboard data can have a group.

### Connectors are lost when a project loads

| Item | Value |
|---|---|
| Status | Confirmed |
| Location | `FileRelatedActionsHelper.replaceWorkspace()` and `MainStateManager.reflectChangedFromLocal()` in `statemanager` |
| Effect | The line connectors are not loaded. A line does not follow its box after you switch projects, open a file or come back to a tab that loaded a change. |
| Cause | Both functions pass an empty `ShapeConnector()` to `replaceRoot()`. The code has the comment "TODO: load from storage". |
| Fix | Read the connectors from `WorkspaceObjectDao` or from the file. Make a `ShapeConnector` from them. |

The page load reads the connectors correctly.

### Cut leaves connectors

| Item | Value |
|---|---|
| Status | Confirmed |
| Location | `ClipboardManager.copySelectedShapes()` in `statemanager` |
| Effect | After a cut, the connectors of the removed shapes stay in `ShapeConnector`. They are saved with the project. |
| Cause | A cut calls only `removeShape()`. The delete action also removes the connectors. |
| Fix | Use the same code as `DeleteSelectedShapes`. |

### The Rectangle tool makes a Text shape

| Item | Value |
|---|---|
| Status | Limit |
| Location | `MouseCommandFactory` in `statemanager` |
| Effect | The Rectangle tool makes a `Text` with `isTextEditable = false`, not a `Rectangle`. A double click runs `MakeTextEditable`. Then the box becomes a text box. |
| Note | `AddShapeMouseCommand` makes a `Rectangle`, but no code uses it. |

### Groups have no interaction bound

| Item | Value |
|---|---|
| Status | Limit |
| Location | `MainStateManager.updateInteractionBounds()` in `statemanager` |
| Effect | A selected group shows no bound and no handles. The code has a TODO. |

## Clipboard

### Copy can fail because of the hidden class

| Item | Value |
|---|---|
| Status | Likely |
| Location | `ShapeClipboardManager.setClipboardText()` in `shape-clipboard`. `ExportShapesModal` in `export-shapes-modal`. |
| Effect | Copy, cut, copy as text and the **Copy** button of the export modal may copy nothing. |
| Cause | The code copies from a `textarea` with the class `hidden`. Tailwind makes `.hidden { display: none }`. A browser cannot select text in an element that is not displayed. |
| Fix | Use a different class name, for example `offscreen`. Or use `navigator.clipboard.writeText()`. |

Refer to [Theme and styling](theme-and-styling.md#the-hidden-class).

## LiveData

### A throttled observer stops after a null value

| Item | Value |
|---|---|
| Status | Confirmed |
| Location | `ThrottledObserver.timeoutTick()` in `livedata` |
| Effect | If the last value in a time window is `null`, the observer never gets a value again. |
| Cause | `timeoutTick()` returns before it sets `currentTimeout = null`. Thus `onChanged()` never starts a new timer. |
| Fix | Set `currentTimeout = null` first. Then send the value. Also send `null` values. |

## Storage and projects

### Removing a project leaves a key

| Item | Value |
|---|---|
| Status | Confirmed |
| Location | `WorkspaceObjectDao.removeSelf()` in `store-dao` |
| Effect | The `workspace/<id>/last-opened` key stays in `localStorage`. |
| Fix | Remove all keys under `workspace/<id>/`. |

### Unused storage code

| Item | Status | Note |
|---|---|---|
| `WorkspaceDao.lastOpenedObjectId` (key `workspace/last-open`) | Limit | No code uses it. The startup uses the `last-opened` time of each project. |
| `WorkspaceDao.removeObject()` | Limit | No code uses it. The function has a TODO. |

### A switch to an empty project does nothing

| Item | Value |
|---|---|
| Status | Limit |
| Location | `FileRelatedActionsHelper` in `statemanager` |
| Effect | `SwitchProject` does nothing if the project has no content. No message shows. |

### A file that cannot be read shows no error

| Item | Value |
|---|---|
| Status | Limit |
| Location | `FileRelatedActionsHelper` in `statemanager` |
| Effect | Only `console.warn` occurs. The code has the comment "TODO: Show error dialog". |

### A project removed in a different tab

| Item | Value |
|---|---|
| Status | Limit |
| Location | `MainStateManager.reflectChangedFromLocal()` in `statemanager` |
| Effect | If a different tab removes the open project, this tab does nothing. The code has a TODO. |

## Memory

| Item | Status | Effect |
|---|---|---|
| Undo history | Limit | `StateHistoryManager` has no size limit. Each entry is a full copy of the project. Refer to [Undo and redo](undo-and-redo.md). |
| Bitmap cache | Limit | `MonoBitmapManager` never removes an entry. The entries of removed shapes stay in memory. |

## Debug output and comments

| Item | Status | Effect |
|---|---|---|
| `CrossingResources.getCrossingChar()` | Confirmed | When `Build.DEBUG` is true, it calls `console.log` for each crossing. A large drawing writes many lines. |
| `ZoneOwnersManager` KDoc | Confirmed | It says that a zone is 8 × 8 cells. A zone is 16 × 16 cells. |
| `MonoBitmap.Builder.fill(row, column, bitmap)` | Confirmed | It uses `row` as the left and `column` as the top. All callers use `(0, 0)`, so there is no effect now. |

## Styling

| Item | Status | Effect |
|---|---|---|
| `<html>` class | Confirmed | `AppThemeManager` replaces the whole class of `<html>`. The `w-full` and `h-full` classes from `index.html` are removed. |

## Report a new issue

To report an issue, refer to [Contributing](contributing.md#report-a-bug).
