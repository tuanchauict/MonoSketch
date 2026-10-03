# Overview

MonoSketch is a web application for ASCII diagrams. You use it to draw boxes, text and lines with text characters. You can then copy the diagram as plain text into documents, code comments and chat messages.

The production application is at [app.monosketch.io](https://app.monosketch.io/).

## Main properties

- **Client-side only.** The application runs fully in the browser. There is no server-side code. The application keeps your data in the browser local storage.
- **Infinite canvas.** You can scroll in all directions. The coordinates can be negative.
- **Text output.** Each diagram is a grid of characters. The export gives plain text that you can paste anywhere.

## Features

| Area | Features |
|---|---|
| Drawing tools | Rectangle, Text, Line |
| Shape styles | Fill, border, dash pattern, rounded corners, line stroke, start head and end head |
| Editing | Select, move, resize, reorder, copy, cut, paste, duplicate, undo, redo |
| Lines | Lines connect to rectangles and text shapes. When you move a shape, the connected lines move with it. |
| Projects | Many projects in one browser. Autosave. Open a project in a new tab. |
| Files | Save a project as a `.mono` file. Open a `.mono` file. |
| Export | Export the selected shapes as text. Copy as text with a keyboard shortcut. |
| Display | Light and dark theme. Font size from 13 px to 25 px. |

For the procedures, refer to the [User guide](user-guide.md).

## Technology

| Technology | Purpose |
|---|---|
| [Kotlin/JS](https://kotlinlang.org/docs/js-overview.html) 2.4.20 | All the application code. Gradle compiles the Kotlin code to JavaScript. |
| [Compose for Web](https://github.com/JetBrains/compose-multiplatform) (compose-html) 1.11.1 | The toolbar, the Format panel and most of the modals. |
| `html-dsl` (a module in this project) | Direct DOM creation for some older UI parts. |
| HTML canvas | The drawing area. |
| [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) 1.11.0 | JSON for storage, files and the clipboard. |
| [Sass](https://sass-lang.com/) 1.54.0 | Styles for the navigation bar and the modals. |
| [Tailwind CSS](https://tailwindcss.com/) 3.4 | Utility classes in the Kotlin code. |
| Gradle 9.7.1 | The build system. |
| Karma and Chrome Headless | The unit test runner. |

## Terms

This documentation uses these terms with one meaning only:

| Term | Meaning |
|---|---|
| Shape | One item in a diagram: a rectangle, a text, a line or a group. The base class is `AbstractShape`. |
| Root group | The top group of the shape tree. Its ID is also the project ID. |
| Project | One diagram that the application keeps in local storage. The code also uses the terms "workspace object" and "root". |
| Bitmap | A `MonoBitmap`. It is the characters of one shape. |
| Board | The `MonoBoard`. It is the infinite grid of characters that contains all the shapes. |
| Canvas | An HTML `<canvas>` element that shows the board. |
| Cell | One position on the board. One cell holds one character. |
| Window | The part of the board that is visible on the screen. |
| Extra | The style data of a shape, for example `RectangleExtra` and `LineExtra`. |
| Connector | A link between one end of a line and a rectangle or a text shape. |
| Action | A request from the user, from the toolbar, from the keyboard or from a menu. |

## Project status

The Kotlin code on the `main` branch is the active code. This documentation is about that code.

A port to TypeScript was started, but it stopped because of performance problems. The TypeScript version is in the [`port-to-js`](https://github.com/tuanchauict/MonoSketch/tree/port-to-js) branch.

## License

The project uses the [Apache License 2.0](https://opensource.org/licenses/Apache-2.0). Refer to the `LICENSE` file.
