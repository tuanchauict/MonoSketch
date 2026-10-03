# Build system

This document describes how Gradle builds MonoSketch. For the first setup, refer to [Getting started](getting-started.md).

## Overview

Gradle builds the application in three parts:

| Part | Tool | Input | Output |
|---|---|---|---|
| Application code | Kotlin/JS compiler and webpack | `src/main/kotlin`, `app/`, `libs/*` | `MonoSketch.js` |
| Component styles | Sass 1.54.0 | `src/main/sass/main.scss` | `main.css` |
| Utility styles | Tailwind CSS 3.4 | `src/main/css/tailwind.css` and all Kotlin files | `tailwind.css` |

The `jsProcessResources` task depends on `compileSass` and `compileTailwind`. Thus each Kotlin/JS build also builds the two CSS files.

The page `src/main/resources/index.html` loads `tailwind.css`, then `main.css`, then `MonoSketch.js`.

## Build files

| File | Content |
|---|---|
| `settings.gradle.kts` | The list of modules and their folders. It enables the type-safe project accessors. |
| `build.gradle.kts` | The root project. It declares the plugin versions, the browser executable and the `useSystemNodeJs` option. |
| `gradle/libs.versions.toml` | The version catalog: Kotlin test library and kotlinx-serialization. |
| `gradle.properties` | Gradle and Kotlin properties. |
| `sass.gradle` | The `compileSass` task configuration. |
| `tailwind.gradle` | The `installTailwindDependencies` and `compileTailwind` tasks. |
| `ktlint.gradle` | The `ktlint` and `ktlintFormat` tasks. |
| `package.json` | The npm packages and scripts for Tailwind CSS. |
| `tailwind.config.js` | The Tailwind CSS configuration. |
| `Pipfile` | The Python packages for the alternative development server. |

## Plugins and versions

| Plugin or library | Version |
|---|---|
| Gradle wrapper | 9.7.1 |
| `kotlin("multiplatform")` | 2.4.20 |
| `kotlin("plugin.serialization")` | 2.4.20 |
| `kotlin("plugin.compose")` | 2.4.20 |
| `org.jetbrains.compose` | 1.11.1 |
| `io.miret.etienne.sass` | 1.4.0 (runs Sass 1.54.0) |
| kotlinx-serialization-json | 1.11.0 |
| ktlint | 0.49.0 |

To change the Kotlin version, change it in `build.gradle.kts` and in `gradle/libs.versions.toml`. The two values must be the same.

## Modules

The `settings.gradle.kts` file maps each module name to a folder:

- The module `app` is in `app/`.
- All other modules are in `libs/<module-name>/`.
- The root project is the entry point. Its code is in `src/main/kotlin/app.kt`.

Each module uses the Kotlin Multiplatform plugin with one `js` target. The source folders are not the default Multiplatform folders:

| Source set | Folder |
|---|---|
| `jsMain` | `src/main/kotlin` |
| `jsTest` | `src/test/kotlin` |

Use the type-safe project accessors for dependencies between modules. For example, `implementation(projects.shapeSerialization)` adds the `shape-serialization` module.

For the list of modules, refer to [Module reference](modules.md).

### Add a module

1. Make a folder `libs/<module-name>/`.
2. Add a `build.gradle.kts` file. Copy it from a similar module, for example `libs/shape/build.gradle.kts`.
3. Put the source code in `libs/<module-name>/src/main/kotlin/`.
4. Add the line `"<module-name>" to "libs/<module-name>"` to `moduleMap` in `settings.gradle.kts`.
5. Add `implementation(projects.<moduleName>)` to the modules that use the new module.

If the module uses Compose for Web, add the plugins `kotlin("plugin.compose")` and `id("org.jetbrains.compose")`. Then add `compose.html.core` and `compose.runtime` to the dependencies. Refer to `libs/ui-toolbar/build.gradle.kts`.

## Gradle tasks

| Task | Purpose |
|---|---|
| `jsBrowserDevelopmentRun --continuous` | Run the development server with hot reload. |
| `jsBrowserProductionRun --continuous` | Run the production build on the development server. |
| `jsBrowserDevelopmentExecutableDistribution` | Build the development files into `build/developmentExecutable`. |
| `assemble` | Build the production files into `build/distributions`. |
| `allTests` | Run all unit tests. Refer to [Testing](testing.md). |
| `ktlint` | Check the code style. Refer to [Code style](code-style.md). |
| `ktlintFormat` | Fix the code style. |
| `check` | Run the tests and `ktlint`. |
| `compileSass` | Compile the Sass files. |
| `compileTailwind` | Compile the Tailwind CSS file. |

## Gradle properties

The `gradle.properties` file sets these values:

| Property | Value | Effect |
|---|---|---|
| `org.gradle.parallel` | `true` | Gradle builds modules in parallel. |
| `kotlin.code.style` | `official` | The IDE uses the official Kotlin code style. |
| `kotlin.js.generate.executable.default` | `false` | Only the modules that call `binaries.executable()` make an executable. Only the root project does this. |

**Note:** Use `-Dorg.gradle.parallel=false` with `--continuous`. A parallel continuous build does not work correctly.

### The useSystemNodeJs option

The Kotlin/JS plugin downloads its own Node.js and Yarn. Some corporate proxies block these downloads.

To use the Node.js and Yarn that are installed on your computer, add `-PuseSystemNodeJs=true` to the command. The root `build.gradle.kts` then sets `download = false` for the Node.js and Yarn plugins of all modules.

Do not use this option on the CI.

## Sass

The `sass.gradle` file configures the `compileSass` task:

- Input: `src/main/sass/`. The entry file is `main.scss`.
- Output: `build/processedResources/js/main/main.css`.
- Style: compressed.

For the content of the Sass files, refer to [Theme and styling](theme-and-styling.md).

## Tailwind CSS

The `tailwind.gradle` file defines two tasks:

1. `installTailwindDependencies` runs `npm install`. It runs only when `node_modules/.bin/tailwindcss` does not exist.
2. `compileTailwind` runs `npm run tailwind:build`.

The `tailwind:build` script reads `src/main/css/tailwind.css`. It writes a minified file to `build/processedResources/js/main/tailwind.css`.

Tailwind CSS scans the HTML and Kotlin files for class names. It keeps only the classes that it finds. The `compileTailwind` task uses all Kotlin source folders as inputs. Thus Gradle runs the task again when a Kotlin file changes.

To watch the Tailwind CSS input without Gradle, run:

```bash
npm run tailwind:watch
```

## Build outputs

| Folder | Content |
|---|---|
| `build/processedResources/js/main/` | `index.html`, fonts, `favicon.png`, `main.css`, `tailwind.css` |
| `build/developmentExecutable/` | The development build. |
| `build/distributions/` | The production build. The CI uploads this folder. |
| `build/dev/` | The copy that `tools/dev-runner.py` serves. |

## Build environment flag

The `build-environment` module has the object `Build`. The value `Build.DEBUG` is `true` when `process.env.NODE_ENV` is `"development"`. Webpack sets this value in the development build.

Use `Build.DEBUG` for debug logs and debug tools. For example, in a debug build, the `window.cmd` function is available in the browser console. Refer to [Actions and key commands](actions-and-key-commands.md).
