# Clipboard and export

This document describes copy, cut, paste, duplicate and the text export.

## Classes

| Class | Module | Responsibility |
|---|---|---|
| `ClipboardManager` | `statemanager` | Copies, cuts, pastes and duplicates shapes. |
| `ShapeClipboardManager` | `shape-clipboard` | Reads the `paste` event and writes the system clipboard. |
| `FileRelatedActionsHelper` | `statemanager` | Selects the shapes to export. |
| `ExportShapesHelper` | `export-shapes-modal` | Draws the shapes as text. |
| `ExportShapesModal` | `export-shapes-modal` | Shows the text in a modal. |

## Clipboard format

MonoSketch writes shapes to the system clipboard as JSON text. The JSON is a `ClipboardObject`:

| Field | Description |
|---|---|
| `shapes` | The selected shapes, as serializable shapes. |
| `connectors` | The line connectors of the selected shapes. |

For the shape keys, refer to [Shape JSON](storage-and-projects.md#shape-json).

`ShapeClipboardManager.setClipboardText()` writes the text:

1. Make a hidden `textarea` with the text.
2. Select the text.
3. Call `document.execCommand("copy")`.
4. Remove the `textarea`.

## Copy and cut

| Key | Action |
|---|---|
| Cmd+C | `Copy(isRemoveRequired = false)` |
| Cmd+X | `Copy(isRemoveRequired = true)` |

`ClipboardManager.copySelectedShapes()` does these steps:

1. If no shape is selected, stop.
2. Serialize each selected shape.
3. Get the connectors that connect to the selected shapes.
4. Write the `ClipboardObject` to the clipboard.
5. For a cut, remove the selected shapes. Then clear the selection.

**Note:** a cut does not remove the connectors of the removed shapes. Refer to [Known issues](known-issues.md).

## Paste

Paste (Cmd+V) is not a key command. `ShapeClipboardManager` listens to the `paste` event of the document.

1. Call `preventDefault()`.
2. Read the `text/plain` data. If the text is blank, stop.
3. Read the text as a `ClipboardObject`.
4. If this fails, read the text as a list of shapes. Older versions use this format.
5. If this also fails, make one `Text` shape from the text. Refer to [Paste plain text](#paste-plain-text).
6. Send the result on `clipboardShapeLiveData`.

`ClipboardManager` observes `clipboardShapeLiveData`. If the list of shapes is not empty, it clears the selection. Then it inserts the shapes.

The pasted shapes go to a fixed position in the visible window:

| Coordinate | Value |
|---|---|
| Left | Window left + window width / 5 |
| Top | Window top + window height / 5 |

### Paste plain text

When the clipboard has text that is not shape JSON, MonoSketch makes a `Text` shape:

- The shape has no border and no fill (`TextExtra.NO_BOUND`).
- A line that is longer than 400 characters (`DEFAULT_TEXT_BOUND_WIDTH`) is cut into parts.
- The width is the length of the longest line. The height is the number of lines.
- Each space becomes a no-break space (`\u00a0`).

## Duplicate

Cmd+D sends `Duplicate`. `ClipboardManager.duplicateSelectedShapes()` copies the selected shapes. It does not use the system clipboard.

The copy goes one cell to the right and one cell down from the selected shapes.

## Insert shapes

Paste and duplicate both use `ClipboardManager.insertShapes()`:

1. Find the smallest left and top of the shapes.
2. Make each shape again in the current group. The shapes get new IDs.
3. Move each shape so that the smallest left and top go to the target position.
4. Add each shape. Select each shape.
5. Add the connectors again. A connector is added only if its line and its target are both in the inserted shapes.

Thus a pasted line keeps its connection only when you also copy its target.

## Export as text

You can export shapes as plain text in two ways:

| Command | Source | Result |
|---|---|---|
| **Export Text** | The project title menu, or `ExportSelectedShapes` | Opens the export modal. |
| Copy as text | Cmd+Shift+C, or `CopyText` | Writes the text to the clipboard. |

`FileRelatedActionsHelper.exportSelectedShapes(isModalRequired)` selects the shapes:

| Selection | Export Text | Copy as text |
|---|---|---|
| One or more shapes | The selected shapes of the current group, in z-order. | The same. |
| No shape | All shapes of the current group. | Nothing. |

### Make the text

`ExportShapesHelper.exportText()` does these steps:

1. If the list is empty, stop.
2. Find the bound of all shapes.
3. Make a new `MonoBoard`. Set its window to the bound.
4. Draw the bitmap of each shape on the board. For a group, draw each shape in the group.
5. Get the text with `toStringInBound(bound)`.
6. Show the text in the modal, or write it to the clipboard.

The board merges crossing lines in the same way as on the screen. Refer to [Rendering](rendering.md#crossing-lines).

### Export modal

The export modal shows the text in an editable `pre` element. You can change the text before you copy it.

| Control | Effect |
|---|---|
| **Copy** button | Copies the text in the modal, with your changes. |
| Close button | Closes the modal. |
| Click outside the modal | Closes the modal. |
