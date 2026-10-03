# Getting started

This document tells you how to prepare your computer and run MonoSketch on it.

## Prerequisites

Install these tools before you start:

| Tool | Version | Necessary | Purpose |
|---|---|---|---|
| Java JDK | 17 | Yes | Runs Gradle and the Kotlin compiler. The CI uses Zulu JDK 17. |
| Node.js and npm | Current LTS | Yes | Runs the Tailwind CSS compiler. |
| Python | 3.11 or later | No | Runs the alternative development server. |
| Pipenv | Any | No | Installs the Python packages for the alternative development server. |

You do not have to install Gradle. The project includes the Gradle wrapper (`./gradlew`). The wrapper downloads the correct Gradle version (9.7.1).

The Kotlin/JS Gradle plugin downloads its own Node.js and Yarn for the Kotlin build. The Tailwind task uses the `npm` command from your `PATH`.

## Get the source code

1. Clone the repository:

   ```bash
   git clone https://github.com/tuanchauict/MonoSketch.git
   ```

2. Go into the project folder:

   ```bash
   cd MonoSketch
   ```

## Run the development server with Gradle

This is the recommended procedure.

1. Start the development build:

   ```bash
   ./gradlew jsBrowserDevelopmentRun --continuous -Dorg.gradle.parallel=false
   ```

2. Wait until Gradle opens the application in your browser.
3. Edit the source code. Gradle compiles the code again and the browser reloads the page.

To run the production build, use this command:

```bash
./gradlew jsBrowserProductionRun --continuous -Dorg.gradle.parallel=false
```

**Note:** Keep the `-Dorg.gradle.parallel=false` flag. The `gradle.properties` file sets `org.gradle.parallel=true`. A parallel build does not work correctly with `--continuous` mode for Kotlin/JS.

The IntelliJ IDEA run configuration `.run/Run.run.xml` runs the same command.

## Run the development server with Python

Use this procedure when the Gradle hot reload does not work correctly.

1. Install the Python packages:

   ```bash
   pipenv install
   ```

2. Start the server:

   ```bash
   pipenv run dev
   ```

3. Open `http://localhost:8000` in your browser.

The script `tools/dev-runner.py` does these steps:

1. It copies the files from `build/developmentExecutable` to `build/dev`.
2. It serves `build/dev` on port 8000 with live reload.
3. It monitors the `.kt` and `.scss` files in all `src/main` folders.
4. When a file changes, it runs `./gradlew jsBrowserDevelopmentExecutableDistribution`. Then it copies the changed files to `build/dev`.

**Note:** The script copies only the files that are already in `build/developmentExecutable`. Run a Gradle development build one time before you start the script.

## Work behind a proxy

Some networks block direct access to `nodejs.org` and `yarnpkg.com`. On these networks, the Kotlin/JS plugin cannot download Node.js and Yarn.

To use the Node.js and Yarn that are installed on your computer, add `-PuseSystemNodeJs=true` to the Gradle command:

```bash
./gradlew jsBrowserDevelopmentRun --continuous -Dorg.gradle.parallel=false -PuseSystemNodeJs=true
```

Do not use this flag on the CI. The CI must use the pinned versions.

## Open the project in an IDE

Use IntelliJ IDEA. The repository includes:

- The `.idea/` folder with the shared project settings.
- The `.run/` folder with two run configurations: `Run` (Gradle) and `Run with Python`.
- The `.editorconfig` file with the code style rules. Refer to [Code style](code-style.md).

## Next steps

- Read [Architecture](architecture.md) to learn how the application is structured.
- Read [Build system](build-system.md) to learn about the Gradle tasks.
- Read [Testing](testing.md) before you submit a change.
