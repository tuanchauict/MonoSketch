# Storage and projects

This document describes how MonoSketch keeps projects and settings, and how it saves and opens files.

MonoSketch has no server. All data stays in the `localStorage` of the browser.

## Storage layers

| Layer | Class | Module | Responsibility |
|---|---|---|---|
| Low | `StoreManager` | `store-manager` | Reads and writes `localStorage` keys. Runs the migrations. Sends `storage` events from other tabs. |
| Low | `StorageDocument` | `store-manager` | A view of the keys under one path, for example `settings`. |
| High | `WorkspaceDao` | `store-dao` | Lists, gets and removes projects. |
| High | `WorkspaceObjectDao` | `store-dao` | Reads and writes the data of one project. |

`StoreManager` is internal. Other modules use `StorageDocument` or the DAO classes.

## Storage keys

`StoreKeys` defines all key names. A path uses `/` as the separator.

| Key | Value |
|---|---|
| `DB_VERSION` | The storage version. The current version is `2`. |
| `settings/theme-mode` | `DARK` or `LIGHT`. |
| `settings/font-size` | The canvas font size. The default is `13`. |
| `workspace/<id>/content` | The root group of the project, as JSON. |
| `workspace/<id>/connectors` | The line connectors of the project, as JSON. |
| `workspace/<id>/name` | The project name. The default is `Undefined`. |
| `workspace/<id>/offset` | The scroll position, as `left|top` in pixels. |
| `workspace/<id>/last-modified` | The time of the last change, in milliseconds. |
| `workspace/<id>/last-opened` | The time when the project was last opened, in milliseconds. |

The `<id>` is the ID of the root group.

## Migrations

`StoreManager` runs the migrations when it starts:

1. Read `DB_VERSION`. If the key is missing, use version 1.
2. Run each migration from the next version to the current version.
3. Write the current version to `DB_VERSION`.

| Migration | Changes |
|---|---|
| `MigrateTo2` | Moves `local-theme-mode` to `settings/theme-mode`. Moves the old `backup-shapes:<id>`, `offset:<id>`, `name:<id>`, `last-modified:<id>` and `last-opened:<id>` keys to `workspace/<id>/...`. Removes the old `last-open` key. |

### Add a migration

1. Make a new `object` that extends `Migration(targetVersion)` in `store-manager/.../migrations/`.
2. Write the changes in `migrate(storageManager)`.
3. Add the object to the `migrations` list in `StoreManager`.
4. Increase `DB_VERSION` in `StoreManager`.

## Projects

A project is one root group with its connectors, name and scroll position.

### Select the project at startup

`StateHistoryManager.restoreAndStartObserveStateChange()` selects the project:

1. If the URL has `?id=<id>`, use that ID. If no project has that ID, make a new empty project with that ID.
2. Else, use the project that was opened last.
3. If there is no project, make a new project with a new ID.

### Project actions

`FileRelatedActionsHelper` does the project actions. Each action is a `OneTimeActionType.ProjectAction`.

| Action | Effect |
|---|---|
| `NewProject` | Makes an empty root group with a new ID. |
| `SwitchProject(id)` | Loads the project with that ID. If the project has no content, the action does nothing. |
| `RemoveProject(id)` | Removes the project keys. If the project is open, it opens the next project. If there is no other project, it makes a new project. |
| `RenameCurrentProject(name)` | Sets the name. Then it calls `notifyProjectUpdate()`, so the title updates. |
| `SaveShapesAs` | Saves the project to a `.mono` file. |
| `OpenShapes` | Opens a `.mono` file. |
| `ExportSelectedShapes` | Shows the shapes as text. Refer to [Clipboard and export](clipboard-and-export.md). |

When the root ID changes, `CommandEnvironment.replaceRoot()` also does these steps:

1. Update `last-opened` of the new project.
2. Set the scroll position of the new project.
3. Clear the undo history.
4. Clear the selection.

**Note:** `NewProject`, `SwitchProject` and `OpenShapes` use an empty `ShapeConnector`. Thus the line connectors are lost when you switch projects or open a file. Refer to [Known issues](known-issues.md).

## Autosave

`StateHistoryManager` saves the project automatically:

1. It observes the shape version and the editing mode.
2. When the version changes and the editing mode is off, it starts a 300 ms timer.
3. When the timer ends, it checks the version again. If the version did not change, it saves the project.
4. It writes `content` and `connectors`. This also updates `last-modified`.

The same step adds the state to the undo history. Refer to [Undo and redo](undo-and-redo.md).

The scroll position is saved at once, each time it changes.

There is no "Save" button. The project is always saved.

## Changes from other tabs

You can open the same project in more than one tab.

- When a tab becomes active, `MainStateManager.reflectChangedFromLocal()` reads the project from storage. If the stored version is different, the tab loads it.
- `AppThemeManager` observes the `settings/theme-mode` key. When a different tab changes the theme, this tab changes it too.

## URL and page title

`BrowserManager` keeps the URL and the title in sync with the open project:

| Item | Value |
|---|---|
| URL | `?id=<project ID>`. A project change calls `history.pushState`. |
| Title | `<project name> - MonoSketch` |
| Back and Forward buttons | The `popstate` event loads the project from the URL. |
| Open in new tab | `BrowserManager.openInNewTab(id)` opens `?id=<id>`. |

## Files

`FileMediator` reads and writes files:

- **Save:** it makes a `Blob` and clicks a hidden link. The file name is `<project name>.mono`.
- **Open:** it clicks a hidden file input that accepts `.mono`. Then it reads the file as text.

### Open a file

1. Read the file as a `MonoFile`. If this fails, read it as a version 0 file (a root group only). If this also fails, log a warning and stop.
2. If a project with the same root ID exists, show a dialog:
   - **Replace** loads the file into the existing project.
   - **Keep both** loads the file as a new project with a new ID.
3. Write the name and the offset from the file to storage.
4. Load the root group.

### MONO file format

A `.mono` file is a JSON object. `ShapeSerializationUtil.toMonoFileJson()` makes it.

| Key | Type | Description |
|---|---|---|
| `root` | object | The root group. |
| `extra` | object | `name` (string) and `offset` (point). |
| `version` | number | The file version. The current version is `2`. |
| `modified_timestamp_millis` | number | The time when the file was saved. |
| `connectors` | array | The line connectors. Added in version 2. |

| Version | Change |
|---|---|
| 0 | The file is only the root group. |
| 1 | Adds `root`, `extra`, `version` and `modified_timestamp_millis`. |
| 2 | Adds `connectors`. |

### Shape JSON

The shape classes use short keys to make the files small. The `type` key tells the shape type.

| `type` | Class | Keys |
|---|---|---|
| `R` | `SerializableRectangle` | `i` ID, `idtemp`, `v` version, `b` bound, `e` extra |
| `T` | `SerializableText` | `i`, `idtemp`, `v`, `b`, `t` text, `e` extra, `te` editable |
| `L` | `SerializableLine` | `i`, `idtemp`, `v`, `ps` start, `pe` end, `jps` joint points, `e` extra, `em` segment moved |
| `G` | `SerializableGroup` | `i`, `idtemp`, `v`, `ss` shapes |

A connector has the keys `i` (line ID), `a` (anchor), `t` (target shape ID), `r` (ratio) and `o` (offset).

The geometry types are strings:

| Type | Format | Example |
|---|---|---|
| `Point` | `left|top` | `"3|5"` |
| `Rect` | `left|top|width|height` | `"3|5|10|4"` |
| `DirectedPoint` | `direction|left|top` (`H` or `V`) | `"H|3|5"` |

`idtemp` is `true` when the ID must be replaced. The application then makes a new ID when it loads the shape. "Keep both" uses this.
