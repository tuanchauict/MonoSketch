# Mouse interaction

This document describes how MonoSketch changes mouse events into changes to the shapes.

## Flow

```
DOM mouse event ─▶ MouseEventObserver ─▶ MousePointer ─▶ MouseInteractionController
                                                      ─▶ MouseCommandFactory ─▶ MouseCommand
```

| Step | Class | Module |
|---|---|---|
| 1 | `MouseEventObserver` reads the DOM events on the canvas container. It sends a `MousePointer` value. | `ui-canvas` |
| 2 | `MouseInteractionController` gets each `MousePointer`. It finds the hover shape and the mouse command. | `statemanager` |
| 3 | `MouseCommandFactory` selects a `MouseCommand` for a mouse-down event. | `statemanager` |
| 4 | The `MouseCommand` changes the shapes through `CommandEnvironment`. | `statemanager` |

The application uses only mouse events. It does not use touch events or pointer events.

## MousePointer

`MousePointer` is a sealed interface in the `graphicsgeo` module. Each value has `boardCoordinate`, the cell under the pointer.

| Value | When | Other data |
|---|---|---|
| `Idle` | No button is down, and the pointer is not moving. | None. |
| `Move` | The pointer moves and no button is down. | `pointPx` |
| `Down` | A button goes down. | `pointPx`, `isWithShiftKey` |
| `Drag` | The pointer moves to a different cell while a button is down. | `mouseDownPoint`, `boardCoordinateF`, `isWithShiftKey` |
| `Up` | The button goes up after `Down` or `Drag`. | `mouseDownPoint`, `boardCoordinateF`, `isWithShiftKey` |
| `Click` | The button goes up without a drag. | `isWithShiftKey` |
| `DoubleClick` | Two clicks occur in less than 500 ms. | None. |

### Event sequence

`MouseEventObserver` sends the values in this sequence:

| User action | Values |
|---|---|
| Move the pointer | `Move`, `Move`, ... |
| Click | `Down`, `Up`, `Click`, `Idle` |
| Second click in 500 ms | `Down`, `Up`, `DoubleClick`, `Idle` |
| Drag | `Down`, `Drag`, `Drag`, ..., `Up`, `Idle` |

Notes:

- A `Drag` occurs only when the pointer goes into a different cell. A small move in the same cell does not start a drag.
- A mouse move resets the double-click detector.
- When you push or release Shift during a drag, the observer sends the last `Drag` again with the new Shift state. Thus a line changes to a straight line immediately.

## MouseInteractionController

`MouseInteractionController.onMouseEvent()` does these steps for each `MousePointer`:

1. On `Down` or `Up`, clear the hover cache.
2. On `DoubleClick`, find the selected shape under the pointer. Send the `EditSelectedShape` action. Then stop.
3. If no command is active, find the hover shape. Refer to [Hover](#hover).
4. Ask `MouseCommandFactory` for a command. If it gives none, use the active command. If there is no active command, stop.
5. Enter the editing mode. Then run the command.
6. If the result is `DONE`, exit the editing mode.
7. If the result is `DONE` or `WORKING_PHASE2`, clear the active command and request a redraw. Then set the tool to `IDLE` (the Select tool).

The editing mode stops the undo history while a command runs. Refer to [Undo and redo](undo-and-redo.md).

### Hover

The hover shape depends on the current tool:

| Tool | Focus type | Effect |
|---|---|---|
| Select (`IDLE`) | `SELECT_MODE_HOVER` | The shape under the pointer shows in the selected color. The controller ignores the hover when the pointer is on a handle. |
| Line (`ADD_LINE`) | `LINE_CONNECTING` | The shape that a line end can connect to shows in the connect color. |
| Rectangle, Text | None | No hover. |

`HoverShapeManager` keeps the result for each cell. Thus the search runs only one time for each cell.

### Double click

A double click sends `OneTimeActionType.EditSelectedShape`. The action opens the text editor only for a `Text` shape. It first runs `MakeTextEditable`. Thus a double click also adds text to a box from the Rectangle tool.

## MouseCommandFactory

`MouseCommandFactory.getCommand()` selects a command from the `MousePointer` and the current tool.

| MousePointer | Result |
|---|---|
| `Down` | Refer to the steps below. |
| `Click` | `SelectShapeMouseCommand` if the tool is Select. Else, none. |
| Other values | None. The controller keeps the active command. |

On `Down`, the factory does these steps:

1. If a hover shape is not selected and Shift is not down, select only the hover shape.
2. If the pointer is on a handle of a selected shape, give a handle command:
   - `ScaleShapeMouseCommand` for a box handle.
   - `LineInteractionMouseCommand` for a line handle.
3. If the pointer is in the bound of a selected shape and Shift is not down, give `MoveShapeMouseCommand`. The command also gets the lines that connect to the selected shapes.
4. Else, give the command for the current tool:

| Tool | Command |
|---|---|
| Select (`IDLE`) | `SelectShapeMouseCommand` |
| Rectangle (`ADD_RECTANGLE`) | `AddTextMouseCommand(isTextEditable = false)` |
| Text (`ADD_TEXT`) | `AddTextMouseCommand(isTextEditable = true)` |
| Line (`ADD_LINE`) | `AddLineMouseCommand` |

## Mouse commands

All commands are in `statemanager/.../command/mouse/`. Each command implements `MouseCommand`:

| Member | Description |
|---|---|
| `mouseCursor` | The CSS cursor during a drag. |
| `execute(environment, mousePointer)` | Handles one `MousePointer`. It returns a `CommandResultType`. |

| CommandResultType | Meaning |
|---|---|
| `WORKING` | The command continues. |
| `WORKING_PHASE2` | The mouse part is done, but the command continues in a different way. For example, the text editor is open. |
| `DONE` | The command is done. |
| `UNKNOWN` | The command does not handle this value. |

### SelectShapeMouseCommand

| Event | Effect |
|---|---|
| `Drag` | Shows the selection rectangle. |
| `Up` | Selects the shapes that overlap the rectangle. Without Shift, it clears the old selection first. A rectangle of one cell selects nothing. |
| `Click` | Selects the top shape under the pointer. With Shift, it adds or removes that shape. |

### AddTextMouseCommand

The Rectangle tool and the Text tool both use this command. Both make a `Text` shape.

1. On `Down`, add a `Text` shape of one cell.
2. On `Drag`, change the bound of the shape.
3. On `Up`, set the final bound.
4. If the shape is editable and its size is 1 × 1, make it a free text: no fill, no border, left and top alignment.
5. If the shape is editable, open the text editor. The result is `WORKING_PHASE2`.
6. When the editor closes:
   - Delete a free text that has no text.
   - Change the size of a free text to fit the text.
   - Make a text box higher if the text needs more rows.

### AddLineMouseCommand

1. On `Down`, add a `Line`. If the start is on the border of a box, the line starts at a right angle to that border. If a shape can connect, connect the start to it.
2. On `Drag`, move the end. If a shape can connect, show it with the connect color. With Shift, make the line straight.
3. On `Up`, confirm the end and select the line.

### MoveShapeMouseCommand

The command moves the selected shapes by the distance from the mouse-down cell. It also updates the lines that connect to them. It confirms the change on `Up`.

### ScaleShapeMouseCommand

The command changes the bound of a box when you drag one of its eight handles.

### LineInteractionMouseCommand

The command moves an end or a segment of a line.

- For an end, it runs `MoveLineAnchor` with `justMoveAnchor = !isWithShiftKey`. It can connect the end to a shape.
- For a segment, it runs `MoveLineEdge`.

### AddShapeMouseCommand

This command makes a `Rectangle` shape. No code uses it.

## Mouse cursor

`MainStateManager.updateMouseCursor()` sets the cursor:

| MousePointer | Cursor |
|---|---|
| `Move` | The cursor of the handle under the pointer. Else, the cursor of the current tool. |
| `Drag` | The `mouseCursor` of the active command. |
| `Up` | Default. |
| Other values | No change. |

## Scroll

`MouseEventObserver` handles the `wheel` event. There is no drag to scroll.

1. If Alt is down, swap the X and Y values of the wheel.
2. If Shift is down, swap the `VERTICAL` and `HORIZONTAL` scroll modes. `BOTH` does not change.
3. Remove the X value in the `VERTICAL` mode. Remove the Y value in the `HORIZONTAL` mode.
4. Multiply the values by `SCROLL_SPEED_RATIO` (1 / 1.3). Keep the parts that are less than one step for the next event.
5. Move the drawing offset. The visible window changes and the board redraws.

The scroll mode is a setting in `AppUiStateManager`. You can change it in the navigation bar.

## Related documents

- [Shape model](shape-model.md): the shape commands that the mouse commands run.
- [Rendering](rendering.md): the hit tests and the highlight colors.
- [Keyboard shortcuts](keyboard-shortcuts.md): the modifier keys.
