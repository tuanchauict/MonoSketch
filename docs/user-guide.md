# User guide

This document tells you how to use MonoSketch. For a list of keys, refer to [Keyboard shortcuts](keyboard-shortcuts.md).

## Screen layout

| Area | Position | Content |
|---|---|---|
| Navigation bar | Top | Tool buttons, project controls, scroll mode, theme and the app menu. |
| Axis | Left and top of the drawing area | Row numbers and column numbers. |
| Drawing area | Center | The infinite board with a grid. |
| Format panel | Right | The position, size and style of the selected shape. |

## Select a tool

The navigation bar has four tool buttons:

| Tool | Key | Use |
|---|---|---|
| Select | V | Select, move and resize shapes. |
| Rectangle | R | Draw a box. |
| Text | T | Draw a box that contains text. |
| Line | L | Draw a line with right-angle bends. |

After you draw one shape, the application selects the Select tool again.

## Draw a rectangle

1. Push R, or click the Rectangle button.
2. Put the pointer at one corner of the rectangle.
3. Push and hold the mouse button.
4. Move the pointer to the opposite corner.
5. Release the mouse button.

## Add text

To add a text box:

1. Push T, or click the Text button.
2. Drag a box on the board.
3. Type the text in the editor.
4. To close the editor, push Esc or Cmd+Enter (Ctrl+Enter on Windows and Linux), or click outside the editor.

To add free text without a border:

1. Push T.
2. Click one time on the board. Do not drag.
3. Type the text.
4. Close the editor.

The application changes the size of free text to fit the text. If you close the editor and the free text is empty, the application deletes it.

To change the text of a text shape, do one of these steps:

- Double-click the text shape.
- Select the text shape and push Enter.

## Draw a line

1. Push L, or click the Line button.
2. Push and hold the mouse button at the start point.
3. Move the pointer to the end point. The line bends at right angles.
4. To make a straight horizontal or vertical line, hold Shift.
5. Release the mouse button.

### Connect a line to a shape

You can connect each end of a line to a rectangle or a text shape:

1. Start or end the line on the border of the shape, or one cell outside it.
2. Make sure that the application highlights the shape. The highlight shows that the line will connect.
3. Release the mouse button.

When you move or resize the shape, the connected end of the line moves with it.

### Change the route of a line

1. Select the line.
2. To move one end, drag the dot at that end.
3. To move one segment, drag the dot at the middle of that segment.

After you move a segment, the line keeps its route. When you then move an end, the application moves the nearest segment with it. To keep that segment and add a new bend, hold Shift while you drag the end.

## Select shapes

| To do this | Do this |
|---|---|
| Select one shape | Click the shape. |
| Add a shape to the selection or remove it | Hold Shift and click the shape. |
| Select all the shapes in an area | Drag a rectangle on an empty part of the board. |
| Add the shapes in an area to the selection | Hold Shift and drag a rectangle. |
| Select all shapes | Push Cmd+A (Ctrl+A). |
| Clear the selection | Push Esc. |

## Move and resize shapes

- To move the selected shapes, drag one of them.
- To move the selected shapes by one cell, push an arrow key.
- To move the selected shapes by five cells, hold Shift and push an arrow key.
- To resize a rectangle or a text shape, drag one of the eight dots on its border.
- To set an exact position or size, type the values in the **TRANSFORM** section of the Format panel.

## Change the style of a shape

1. Select one shape.
2. Use the **APPEARANCE** section of the Format panel.

| Shape | Style options |
|---|---|
| Rectangle and text | Fill, border, rounded corners, dash pattern |
| Line | Stroke, rounded corners, dash pattern, start head, end head |
| Text | Horizontal alignment and vertical position (in the **TEXT** section) |

If you select no shape and a drawing tool is active, the Format panel shows the default style for that tool. A change to the panel then changes the style of the next shape that you draw. The last style that you use becomes the default style.

To show or hide the Format panel, open the app menu. It is the three dots on the right side of the navigation bar. Then click **Show Format panel** or **Hide Format panel**.

## Change the order of shapes

1. Select one shape.
2. In the Format panel, click one of these buttons: **Bring to Front**, **Bring Forward**, **Send Backward** or **Send to Back**.

## Delete shapes

1. Select the shapes.
2. Push Delete or Backspace.

## Copy, cut, paste and duplicate

| Operation | Keys | Result |
|---|---|---|
| Copy | Cmd+C | Copies the selected shapes. |
| Cut | Cmd+X | Copies the selected shapes and deletes them. |
| Paste | Cmd+V | Adds the shapes from the clipboard. |
| Duplicate | Cmd+D | Adds a copy of the selected shapes one cell down and one cell right. |
| Copy as text | Cmd+Shift+C | Copies the selected shapes as plain text. |

On Windows and Linux, use Ctrl in place of Cmd.

When you paste plain text from another application, MonoSketch adds it as a text shape without a border.

## Undo and redo

- To undo a change, push Cmd+Z.
- To redo a change, push Cmd+Shift+Z.

The history is in memory only. When you reload the page or open a different project, the history is cleared.

## Scroll the board

- Use the mouse wheel or the trackpad to scroll.
- Hold Alt (Option on macOS) to swap the horizontal and vertical directions.
- Click the scroll mode button in the navigation bar to scroll in both directions, only vertically or only horizontally.
- Click the top-left corner of the axis to go back to the cell (0, 0).

## Change the font size

1. Open the app menu.
2. Click the small "A" to make the text smaller, or the large "A" to make the text larger.

The font size is the only zoom control. The range is 13 px to 25 px.

## Change the theme

Click the sun or moon icon in the navigation bar. The theme changes in all the open tabs.

## Manage projects

The application saves your work automatically in the browser local storage. Each project has a name and an ID. The URL shows the project ID, for example `https://app.monosketch.io/?id=<project-id>`.

| To do this | Do this |
|---|---|
| Rename the project | Click the project name, then click **Rename**. Type the name and push Enter. |
| See all projects | Click the inbox icon (**Manage projects**). |
| Create a project | In the project list, click **New project**. |
| Open a project | In the project list, click the project. |
| Open a project in a new tab | In the project list, click the **Open in new tab** icon. |
| Delete a project | In the project list, click the **Delete** icon. Then click **Confirm**. |
| Find a project | In the project list, type a part of the name in the filter box. |

The browser Back and Forward buttons move between the projects that you opened.

**CAUTION:** The browser keeps the projects in local storage. If you clear the browser data, you lose your projects. Save important projects as files.

## Save and open files

To save a project as a file:

1. Click the project name.
2. Click **Save As...**.
3. The browser downloads the file `<project name>.mono`.

To open a file:

1. Click the inbox icon (**Manage projects**).
2. Click **Import from file...**.
3. Select a `.mono` file.
4. If a project with the same ID exists, select **Replace** or **Keep both**.

## Export as text

1. Select the shapes. If you select no shape, the application exports all shapes.
2. Click the project name.
3. Click **Export Text**.
4. In the modal, click **Copy**.

## Show the keyboard shortcuts

1. Open the app menu.
2. Click **Keyboard shortcuts**.
