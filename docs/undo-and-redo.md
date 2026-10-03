# Undo and redo

This document describes the undo and redo history. `StateHistoryManager` in the `statemanager` module keeps it.

## Design

The history keeps snapshots, not commands. Each entry is a full copy of the project:

| Field | Description |
|---|---|
| `versionCode` | The version code of the root group. |
| `serializableGroup` | The root group, as a `SerializableGroup`. |
| `connectors` | The line connectors, as a list of `SerializableLineConnector`. |

The history is in memory only. A page reload clears it.

## Add an entry

`StateHistoryManager` adds an entry in the same step as the autosave:

1. It observes the root version and the editing mode with `combineLiveData`.
2. When the version changes and the editing mode is off, it starts a 300 ms timer.
3. When the timer ends, it checks the version again. If the version changed, it does nothing. A later timer adds the entry.
4. It serializes the root group and the connectors.
5. It adds the entry to the undo stack and clears the redo stack. If the last entry has the same version, it does not add the entry.
6. It saves the project to storage.

Thus a fast series of changes makes only one entry.

## Editing mode

A mouse command or a text edit can make many changes. The editing mode makes these changes into one entry.

| Function | Effect |
|---|---|
| `CommandEnvironment.enterEditingMode()` | Stops the new entries. |
| `CommandEnvironment.exitEditingMode(isNewStateAccepted)` | Allows the new entries again. If `isNewStateAccepted` is `false`, the current version is skipped. |

`MouseInteractionController` enters the editing mode before each mouse command. It exits the editing mode when the command is done. The text editor exits the editing mode with `isNewStateAccepted = false` when the text did not change.

## Undo and redo

| Action | Key | Effect |
|---|---|---|
| Undo | Cmd+Z | Moves the last entry from the undo stack to the redo stack. Then it loads the new last entry of the undo stack. |
| Redo | Cmd+Shift+Z | Moves the last entry from the redo stack to the undo stack. Then it loads that entry. |

- Undo does nothing when the undo stack has one entry or less. Thus you cannot undo the load of the project.
- To load an entry, `StateHistoryManager` makes a `RootGroup` and a `ShapeConnector` from the entry. Then it calls `CommandEnvironment.replaceRoot()`.
- The loaded root keeps the version code of the entry. Thus the load does not add a new entry.

The application menu does not have Undo and Redo. Use the keys. In a debug build, you can also call `cmd("Undo")` in the browser console.

## When the history is cleared

`replaceRoot()` clears the history when the root ID changes. This occurs when you:

- Make a new project.
- Switch to a different project.
- Open a file.
- Remove the open project.

## Limits

- The history has no size limit. Each entry is a full copy of the project.
- A change from a different tab replaces the root, but it does not clear the history.
