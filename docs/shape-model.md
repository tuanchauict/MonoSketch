# Shape model

This document describes the shapes, the shape tree, the commands, the styles and the line connectors. Most of this code is in the `shape` module.

## Shape types

All shapes extend the sealed class `AbstractShape`.

| Class | Description |
|---|---|
| `Rectangle` | A box with a fill and a border. |
| `Text` | A box with a fill, a border and text. |
| `Line` | A line with right-angle bends. Each end can connect to a shape. |
| `Group` | A container for other shapes. |
| `RootGroup` | The top group of a project. Its ID is the project ID. |
| `MockShape` | A shape for tests only. It has no bitmap. |

**Note:** The Rectangle tool makes a `Text` shape with `isTextEditable = false`. It does not make a `Rectangle` shape. The application makes `Rectangle` objects only when it loads saved data that contains them. The class `AddShapeMouseCommand` also makes them, but no code uses that class.

### Common properties

| Property | Description |
|---|---|
| `id` | A unique string. The `uuid` module makes it. |
| `parentId` | The ID of the parent group. |
| `bound` | The position and the size, as a `Rect`. |
| `extra` | The style. Refer to [Styles](#styles). |
| `versionCode` | A random `Int` that changes after each change to the shape. |

### Version code

Change the properties of a shape only inside `update { }`. The block returns `true` when a property changes. Then `update` sets a new random `versionCode`.

The version code is not a counter. Compare it only for equality. Other classes use it to know if a shape changed. For example, `MonoBitmapManager` keeps a bitmap for each shape ID and version code.

### Text

`Text` has these special properties:

| Property | Description |
|---|---|
| `text` | The text content. |
| `isTextEditable` | `true` for shapes from the Text tool. `false` for shapes from the Rectangle tool. |
| `isTextEditing` | `true` while the text editor is open. The board then draws the shape with the `TEXT_EDITING` highlight. |
| `renderableText` | The text after word wrap. The width of the box sets the line length. |

### Line

A `Line` has a start point and an end point. Each point is a `DirectedPoint`. A `DirectedPoint` has a position and a direction (horizontal or vertical).

| Property or function | Description |
|---|---|
| `jointPoints` | All points where the line bends, with the two ends. |
| `reducedJoinPoints` | The joint points without the points on a straight segment. |
| `edges` | The segments between the joint points. |
| `confirmedJointPoints` | The joint points after you move a segment. When this list is not empty, the line keeps its route. |
| `moveAnchorPoint()` | Moves one end. |
| `moveEdge()` | Moves one segment. |

`Line.Anchor` has two values: `START` and `END`.

`LineHelper` makes the joint points from the two ends.

### Group

A `Group` keeps its children in a `QuickList`. The order of the list is the z-order. The first child is at the bottom. The last child is at the top.

**Note:** The UI does not let you make a group. The code supports groups, but it is not complete. Refer to [Known issues](known-issues.md).

## ShapeManager

`ShapeManager` keeps the shape tree of the current project.

| Member | Description |
|---|---|
| `root` | The current `RootGroup`. |
| `rootLiveData` | Sends the root when the project changes. |
| `versionLiveData` | Sends the version code of the root after each change. |
| `shapeConnector` | The connectors between lines and shapes. |
| `execute(command)` | Runs a command. Refer to [Commands](#commands). |
| `getShape(id)` | Gets a shape by ID. |
| `replaceRoot(root, connector)` | Loads a different project. |
| `notifyProjectUpdate()` | Sends the root again, for example after you rename the project. |

`ShapeManager` keeps a map from ID to shape. Thus `getShape()` is fast.

## Commands

Change the shape tree only with commands. Do not change a shape directly from the UI.

`ShapeManager.execute(command)` does these steps:

1. It finds the parent group that the command changes.
2. It runs the command.
3. If the parent did not change, it stops.
4. It calls `update` on each ancestor of the parent. Thus each ancestor gets a new version code.
5. It sends the new root version on `versionLiveData`.

| Command | File | Effect |
|---|---|---|
| `AddShape` | `ShapeManagerCommands.kt` | Adds a shape to a group. |
| `RemoveShape` | `ShapeManagerCommands.kt` | Removes a shape. If a group then has only one child, it ungroups that group. |
| `GroupShapes` | `ShapeManagerCommands.kt` | Puts shapes with the same parent into a new group. |
| `Ungroup` | `ShapeManagerCommands.kt` | Moves the children of a group to its parent. Then it removes the group. |
| `ChangeOrder` | `ShapeManagerCommands.kt` | Moves a shape in the z-order: `FORWARD`, `BACKWARD`, `FRONT` or `BACK`. |
| `ChangeBound` | `GeneralShapeCommands.kt` | Sets the bound of a shape. Use it to move and to resize. |
| `ChangeExtra` | `GeneralShapeCommands.kt` | Sets the style of a shape. |
| `MoveLineAnchor` | `LineCommands.kt` | Moves one end of a line. It can connect the end to a shape. |
| `MoveLineEdge` | `LineCommands.kt` | Moves one segment of a line. |
| `ChangeText` | `TextCommands.kt` | Sets the text of a `Text` shape. |
| `MakeTextEditable` | `TextCommands.kt` | Sets `isTextEditable` to `true`. |
| `UpdateTextEditingMode` | `TextCommands.kt` | Sets `isTextEditing`. |

`RemoveShape` does not remove the connectors of the shape. Call `shapeConnector.removeShape(shape)` also. The Delete action does this. The Cut action does not.

## Styles

The style of a shape is its `extra`. Each extra class is a data class. To change a style, make a copy with `copy()` and run `ChangeExtra`.

### RectangleExtra

| Property | Description |
|---|---|
| `isFillEnabled` | Fill on or off. |
| `userSelectedFillStyle` | The fill style. |
| `isBorderEnabled` | Border on or off. |
| `userSelectedBorderStyle` | The border stroke style. |
| `dashPattern` | The dash pattern of the border. |
| `corner` | Rounded corners on or off. |

The class keeps the selected style also when the fill or the border is off. Thus the style comes back when you turn it on again.

### TextExtra

| Property | Description |
|---|---|
| `boundExtra` | A `RectangleExtra` for the box. |
| `textAlign` | The horizontal alignment and the vertical position of the text. |

### LineExtra

| Property | Description |
|---|---|
| `isStrokeEnabled`, `userSelectedStrokeStyle` | The stroke of the line. |
| `isStartAnchorEnabled`, `userSelectedStartAnchor` | The head at the start. |
| `isEndAnchorEnabled`, `userSelectedEndAnchor` | The head at the end. |
| `dashPattern` | The dash pattern. |
| `isRoundedCorner` | Rounded bends on or off. |

### Predefined styles

The `extra/manager/predefined` folder defines the styles. Each style has an ID. The files keep the ID, not the characters.

Fill styles (`PredefinedRectangleFillStyle`):

| ID | Character |
|---|---|
| F0 | No fill (transparent) |
| F1 | Space |
| F2 | █ |
| F3 | ▒ |
| F4 | ░ |
| F5 | ▚ |

Stroke styles (`PredefinedStraightStrokeStyle`):

| ID | Characters | Note |
|---|---|---|
| S0 | Half-transparent | No stroke. You cannot see it, but you can select it. |
| S1 | `─ │ ┐ ┌ ┘ └` | Light. |
| S2 | `━ ┃ ┓ ┏ ┛ ┗` | Heavy. |
| S3 | `═ ║ ╗ ╔ ╝ ╚` | Double. |
| S4 | `─ │ ╮ ╭ ╯ ╰` | The rounded form of S1. The UI does not show it. |

When rounded corners are on, `getStyle()` changes S1 to S4. Only S1 has a rounded form.

Anchor characters (`PredefinedAnchorChar`):

| ID | Character | ID | Character |
|---|---|---|---|
| A1 | ▶ | A3 | ○ |
| A12 | ▷ | A4 | ◎ |
| A13 | ► | A5 | ● |
| A14 | ▻ | A6 | ├ |
| A2 | ■ | A61 | ┣ |
| A21 | □ | A62 | ╠ |
| A220 | ◆ | | |
| A221 | ◇ | | |

The arrow and T-junction anchors have one character for each direction.

### Default styles

`ShapeExtraManager` keeps the default style for new shapes. When you change a style in the Format panel and no shape is selected, the default style changes. Thus the last style that you use becomes the default style.

## Line connectors

A connector links one end of a line to a `Rectangle` or a `Text` shape.

### LineConnector

| Property | Description |
|---|---|
| `lineId` | The ID of the line. |
| `anchor` | `START` or `END`. |
| `ratio` | The relative position of the end on the shape, as a `PointF`. |
| `offset` | The distance from the shape, as a `Point`. |

### ShapeConnector

`ShapeConnector` keeps all connectors of a project in a `TwoWayQuickMap`.

| Function | Effect |
|---|---|
| `addConnector(line, anchor, shape)` | Connects one end of a line to a shape. |
| `removeConnector(line, anchor)` | Disconnects one end of a line. |
| `hasConnector(line, anchor)` | Tells if one end of a line is connected. |
| `getConnectors(shape)` | Gets all connectors to a shape. |
| `removeShape(shape)` | Removes all connectors to a shape. If the shape is a line, it also removes the connectors of its two ends. |

### Connect and update

1. When you move a line end, `ShapeConnectorUseCase.getConnectableShape()` finds the target shape.
   - Only `Rectangle` and `Text` shapes can be targets.
   - The end must be on the border, or not more than one cell (`MAX_DISTANCE`) outside it.
   - If many shapes are candidates, it uses the top shape.
2. `MoveLineAnchor` adds or removes the connector.
3. When a shape moves or changes size, `UpdateShapeBoundHelper` moves the connected line ends. It uses `getPointInNewBound()` with the ratio and the offset.

## Serialization

The `shape` module has the serializable form of each shape in `serialization/AbstractSerializableShape.kt`. Each shape has `toSerializableShape()`. The `shape-serialization` module changes these objects to JSON and back.

For the file format, refer to [Storage and projects](storage-and-projects.md#mono-file-format).

## Geometry

The `graphicsgeo` module defines the geometry types:

| Type | Description |
|---|---|
| `Point` | A cell position: `left` and `top`. |
| `PointF` | A position with `Double` values. |
| `DirectedPoint` | A `Point` with a direction (horizontal or vertical). |
| `Size` | A width and a height. |
| `Rect` | A position and a size. `right` and `bottom` are inclusive. |
| `MousePointer` | A mouse state. Refer to [Mouse interaction](mouse-interaction.md). |

## IDs

`UUID.generate()` in the `uuid` module makes the IDs. An ID starts with `02-`. The rest is a base64 form of the time and of random numbers.

## Related modules

| Module | Purpose |
|---|---|
| `shape-selection` | `SelectedShapeManager` keeps the selected shapes. It also keeps the focused shape and the focus type: `LINE_CONNECTING` or `SELECT_MODE_HOVER`. |
| `shape-interaction-bound` | `ScalableInteractionBound` has eight handles to resize a box. `LineInteractionBound` has handles for the ends and the segments of a line. |
| `shapesearcher` | `ShapeSearcher` finds the shapes at a point or in an area. Refer to [Rendering](rendering.md#hit-tests). |
