# Contributing

This document describes how to report a bug and how to send a change.

**Note:** send changes for the Kotlin code on `main`. The port to TypeScript stopped. Refer to [Project status](overview.md#project-status).

## Report a bug

1. Make sure that the bug is not in [Known issues](known-issues.md) or in the open GitHub issues.
2. Open a new issue with the **Bug report** template.
3. Select the shapes that show the bug. Push Cmd+C (Ctrl+C on Windows and Linux). Paste the result into the "Mono structure" block.
4. Push Cmd+Shift+C. Paste the result into the "Output" block.
5. Write the expected result.
6. Write your operating system and your browser.
7. If possible, add a screenshot.

The "Mono structure" block has the shape JSON. Thus a maintainer can paste the shapes into MonoSketch and see the bug. Refer to [Clipboard and export](clipboard-and-export.md).

## Send a change

1. Fork the repository.
2. Make a branch from `main`.
3. Set up the project. Refer to [Getting started](getting-started.md).
4. Make the change. Follow the [Code style](code-style.md).
5. Add or update the unit tests if the module has tests. Refer to [Testing](testing.md).
6. Run these commands. Make sure that they pass:

   ```sh
   ./gradlew ktlintFormat
   ./gradlew ktlint
   ./gradlew allTests
   ./gradlew assemble
   ```

7. Test the change in the browser with the development server.
8. Push the branch. Open a pull request to `main`.
9. Write what the change does and why. Add a screenshot for a UI change.
10. Make sure that the Code health check passes. Refer to [CI and release](ci-and-release.md).

## Checklist for common changes

| Change | Steps |
|---|---|
| Add a module | Refer to [Build system](build-system.md#add-a-module). |
| Add a keyboard shortcut | Refer to [Actions and key commands](actions-and-key-commands.md#add-a-keyboard-shortcut). |
| Change the storage format | Add a migration. Refer to [Storage and projects](storage-and-projects.md#add-a-migration). |
| Change the shape JSON | Keep the old keys readable. Old files and old clipboard data must still open. Refer to [Shape model](shape-model.md). |
| Add a canvas color | Refer to [Theme and styling](theme-and-styling.md#add-a-canvas-color). |
| Add a CSS theme color | Refer to [Theme and styling](theme-and-styling.md#add-a-theme-color-for-css). |
| Change the UI | Use Compose for Web. Refer to [User interface](user-interface.md). |

## Documentation

The documentation is in `docs/`. Each file covers one topic. [README.md](README.md) lists all files.

When you change the behavior of the code, update the related document in the same pull request.

Write the documentation in Simplified Technical English (ASD-STE100).

## License

MonoSketch uses the Apache License 2.0. Your contribution uses the same license.
