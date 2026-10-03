# MonoSketch documentation

This folder contains the documentation for MonoSketch. Each file covers one topic.

MonoSketch is a web application that you use to draw diagrams with ASCII and Unicode characters. The application runs fully in the browser. It does not send your data to a server.

## Documents

### For users

| Document | Content |
|---|---|
| [Overview](overview.md) | What MonoSketch is, its features and its technology. |
| [User guide](user-guide.md) | How to draw, edit, style, save and export diagrams. |
| [Keyboard shortcuts](keyboard-shortcuts.md) | All keyboard shortcuts. |

### For developers

| Document | Content |
|---|---|
| [Getting started](getting-started.md) | Prepare your computer and run the application. |
| [Build system](build-system.md) | Gradle, Kotlin/JS, Sass, Tailwind CSS and ktlint. |
| [Architecture](architecture.md) | Layers, startup sequence and data flow. |
| [Module reference](modules.md) | All Gradle modules and their dependencies. |
| [LiveData and lifecycle](livedata-and-lifecycle.md) | The reactive state library of the project. |
| [Shape model](shape-model.md) | Shapes, groups, commands, styles and line connectors. |
| [Rendering](rendering.md) | From shapes to bitmaps, to the board, to the HTML canvas. |
| [Mouse interaction](mouse-interaction.md) | How mouse events become changes to shapes. |
| [Actions and key commands](actions-and-key-commands.md) | How the toolbar, the keyboard and the menus send actions. |
| [Storage and projects](storage-and-projects.md) | Local storage, autosave, projects and `.mono` files. |
| [Undo and redo](undo-and-redo.md) | The history stack. |
| [Clipboard and export](clipboard-and-export.md) | Copy, cut, paste, duplicate and text export. |
| [User interface](user-interface.md) | Compose for Web, the toolbar, the Format panel and the modals. |
| [Theme and styling](theme-and-styling.md) | Light and dark theme, Sass and Tailwind CSS. |
| [Testing](testing.md) | How to write and run tests. |
| [Code style](code-style.md) | Formatting rules and code conventions. |
| [CI and release](ci-and-release.md) | GitHub Actions workflows and the release procedure. |
| [Contributing](contributing.md) | How to send a change to the project. |
| [Known issues](known-issues.md) | Problems in the current code. |

## Writing style

These documents use Simplified Technical English (ASD-STE100). Obey these rules when you change a document:

- Write one topic in one file.
- Write short sentences. Use a maximum of 20 words in an instruction and 25 words in a description.
- Write one instruction in one sentence.
- Use the active voice.
- Use the imperative form for instructions. For example, write "Run the command", not "You should run the command".
- Use the simple present tense for descriptions.
- Use one word for one meaning. For example, this documentation always uses "shape", "board", "canvas" and "project" with the meanings in [Overview](overview.md#terms).
- Use tables and numbered lists for procedures and reference data.
- Use the names from the code (class, file and module names) exactly as they are.
