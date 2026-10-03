# Rendering

This document describes how MonoSketch draws shapes on the screen.

## Pipeline

```
Shape tree ─▶ MonoBitmapManager ─▶ MonoBitmap ─▶ MonoBoard ─▶ BoardCanvasViewController ─▶ <canvas>
              (one bitmap for     (characters   (one grid      (one fillText call
               each shape)         of a shape)   for all)       for each cell)
```

`MainStateManager.redraw()` runs the pipeline:

1. `shapeSearcher.clear()` and `mainBoard.clearAndSetWindow()` clear the zones and the chunks in the visible window.
2. `drawShapeToMainBoard()` goes through the shape tree in z-order. For each shape that is not a group, it does these steps:
   1. Get the bitmap from `MonoBitmapManager`.
   2. Select the highlight.
   3. Put the bitmap on the board with `mainBoard.fill()`.
   4. Register the shape in `ShapeSearcher`.
3. `canvasManager.drawBoard()` draws the board on the canvas.

## When a redraw occurs

`MainStateManager` requests a redraw when one of these events occurs:

- The shape version changes (`ShapeManager.versionLiveData`).
- The visible window changes (`CanvasViewController.windowBoardBoundLiveData`).
- The selected shapes change. `updateInteractionBounds()` draws the new handles and requests a redraw.
- The focused shape changes, for example when the mouse pointer moves over a shape in select mode. `MouseInteractionController` requests the redraw.

The redraw observer uses `throttleDurationMillis = 0`. Thus many requests in one frame cause only one redraw, on the next animation frame.

A change of the theme calls `forceFullyRedrawWorkspace()`. This redraws all canvases.

## Bitmaps

### MonoBitmap

`MonoBitmap` is the characters of one shape. It is in the `monobitmap` module.

- It keeps a list of rows. Each row keeps only the cells that are not transparent.
- Each cell has two characters:
  - The **visual character** is the character on the screen.
  - The **direction character** tells the board how a box-drawing character connects. The board uses it to merge crossing lines.
- Use `MonoBitmap.Builder` to make a bitmap.

### Special characters

| Character | Value | Meaning |
|---|---|---|
| `TRANSPARENT_CHAR` | `0` | Not drawn. The shape below shows. A click here does not select the shape. |
| `HALF_TRANSPARENT_CHAR` | `1` | Not drawn. A click here selects the shape. The S0 (no stroke) style uses it. |
| Space | `' '` | Drawn as an empty cell. It hides the shape below. The F1 fill style uses it. |

### MonoBitmapManager

`MonoBitmapManager` makes the bitmap of a shape with a factory:

| Shape | Factory |
|---|---|
| `Rectangle` | `RectangleBitmapFactory` |
| `Text` | `TextBitmapFactory` |
| `Line` | `LineBitmapFactory` |
| `Group` | None. A group has no bitmap. |
| `MockShape` | None. |

The manager keeps a cache. The key is the shape ID. The cache is valid while the `versionCode` of the shape does not change.

## The board

`MonoBoard` is the infinite grid of characters. It is in the `monoboard` module.

- It divides the grid into chunks of 16 × 16 cells (`STANDARD_UNIT_SIZE`). Each chunk is a `PainterBoard`.
- It makes a chunk only when a shape draws into it.
- `clearAndSetWindow(bound)` clears only the chunks in the visible window. It does not clear the other chunks.
- `fill()` writes all cells of a bitmap. It makes the chunks that it needs.
- `get(left, top)` gets one `Pixel`. A `Pixel` has a visual character, a direction character and a highlight.
- `toStringInBound(bound)` gives the text in an area. The text export uses it.

### Crossing lines

When two box-drawing characters are in the same cell, the board merges them. For example, `─` and `│` become `┼`.

1. `PainterBoard.fill()` does not write the cell. It records a `CrossPoint` with the characters around the cell.
2. After all chunks are filled, `MonoBoard` calls `CrossingResources.getCrossingChar()` for each crossing point.
3. `getCrossingChar()` makes a bit mask for each character. The mask has the four directions in three styles: light, heavy and double.
4. When both characters have the same direction, the upper character sets the style of that direction.
5. The function keeps only the directions that the cells around also have. Thus a crossing does not show an arm to an empty cell.

`CrossingResources` also changes ASCII characters and rounded corners to the light style before it merges them. For example, `-` becomes `─` and `╭` becomes `┌`.

### Highlight

Each pixel has a `Highlight` value. The canvas uses it to select the color.

| Highlight | When | Color (`ThemeColor`) |
|---|---|---|
| `NO` | Normal state. | `Shape` |
| `SELECTED` | The shape is selected, or the pointer is on it in select mode. | `ShapeSelected` |
| `TEXT_EDITING` | The text editor of the shape is open. | `ShapeTextEditing` |
| `LINE_CONNECT_FOCUSING` | A line end will connect to the shape. | `ShapeLineConnectTarget` |

## Canvases

`CanvasViewController` (module `ui-canvas`) makes these canvases in `#monoboard-canvas-container`, from bottom to top:

| Layer | Controller | Content |
|---|---|---|
| Grid | `GridCanvasViewController` | The grid lines. The row 0 and the column 0 have a different color. |
| Board | `BoardCanvasViewController` | The characters of the board. |
| Interaction | `InteractionCanvasViewController` | The selection bound and the handles. |
| Selection | `SelectionCanvasViewController` | The rubber-band rectangle while you select an area. |

`DrawingInfoController` calculates the drawing information: the font, the cell size and the visible rows and columns.

`AxisCanvasViewController` draws the axis in `#monoboard-axis-container`:

- The left axis is 33 px wide. It shows the row numbers.
- The top axis is 18 px high. It shows the column numbers.
- The top-left corner is a button. Click it to go to (0, 0).

### Cell size

The canvas font is `'Jetbrains Mono'` (`DrawingInfoController.DEFAULT_FONT`). The font size sets the cell size:

| Value | Formula |
|---|---|
| Cell width | `floor(fontSize × 0.63)` |
| Cell height | `fontSize × 1.312` |

The font size is from 13 to 25. Each step changes it by 2. The default is 13. This is the only zoom control.

### Pixel ratio

Each canvas uses a device pixel ratio of `max(window.devicePixelRatio, 2)`. Thus the text is sharp also on a normal screen.

### Draw the board

`BoardCanvasViewController.drawInternal()` does these steps:

1. Set the font.
2. For each visible row and column, get the pixel from the board.
3. If the pixel is not transparent, select the color from its highlight.
4. Call `fillText` with the visual character.

## Colors

The `ThemeColor` enum in the `ui-theme` module defines the canvas colors. Each value has a light color and a dark color.

| Value | Use |
|---|---|
| `AxisBackground`, `AxisText`, `AxisRule` | The axis. |
| `GridBackground`, `GridLine`, `GridLineZero` | The grid. |
| `Shape`, `ShapeSelected`, `ShapeTextEditing`, `ShapeLineConnectTarget` | The characters. |
| `SelectionAreaStroke` | The rubber-band rectangle. |
| `SelectionBoundStroke`, `SelectionDotStroke`, `SelectionDotFill` | The selection bound and its handles. |

For the CSS colors, refer to [Theme and styling](theme-and-styling.md).

## Hit tests

`ShapeSearcher` (module `shapesearcher`) finds the shapes under the mouse pointer.

- It divides the board into zones of 16 × 16 cells.
- `register(shape)` records the zones in which the bitmap of the shape has characters.
- `getShapes(point)` gets the shapes in the zone of the point. Then it keeps only the shapes that have a visible or half-transparent character at the point.
- `getAllShapesInZone(bound)` gets the shapes in an area. The rubber-band selection uses it.
- `getEdgeDirection(point)` gets the direction of a shape border at a point. A new line end uses it to start in the correct direction.

Each redraw registers all shapes again. The search zones are 16 × 16 cells (`ZoneAddressFactory` uses `shr 4`). The KDoc of `ZoneOwnersManager` says 8 × 8. That comment is not correct.
