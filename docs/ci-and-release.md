# CI and release

This document describes the GitHub Actions workflows and the release process.

## Workflows

All workflows are in `.github/workflows/`.

| File | Name | Trigger | Purpose |
|---|---|---|---|
| `code-health-check.yml` | Code health check | Each pull request. Each push to `main`. | Runs the tests, `ktlint` and the build. |
| `shared-build-and-upload-artifact.yml` | Reusable build | Called by other workflows. | Builds the production files and uploads them. |
| `alpha-release-bundle.yml` | Publish Alpha release | Push to `release/alpha/**`. | Builds and publishes the alpha release. |
| `product-release-bundle.yml` | Publish Production release | Push to `release/product`, or a manual run. | Builds and publishes the production release. |
| `alpha-release-automerge.yml` | Auto merge | Push to `main`. | Opens a pull request that updates the alpha branch. |

All build jobs use Ubuntu, Zulu JDK 17 and a cache of `~/.gradle/caches` and `~/.gradle/wrapper`.

## Code health check

The workflow has three jobs. They run at the same time.

| Job | Command | On failure |
|---|---|---|
| `unit_test` | `./gradlew allTests --continue` | Removes the reports of the modules that passed. Uploads the other reports as the `test-result` artifact. |
| `ktlint` | `./gradlew ktlint` | The job fails. |
| `assemble` | `./gradlew assemble` | The job fails. |

Make sure that all three jobs pass before you merge a pull request.

### Test reports in CI

`tools/devops/remove_success_test_report` does these steps for the root project, `app` and each module in `libs/`:

1. Find `build/reports/tests/allTests/index.html`. If it is missing, skip the module.
2. If the report shows no failures, remove the report folder.
3. Else, keep the report. Print the names of the failed test classes.

Download the `test-result` artifact from the workflow run page. Open `index.html` in a browser.

## Release

### Release flow

```
main ──push──▶ Auto merge ──▶ PR to the alpha branch ──merge──▶ Publish Alpha release
release/product ──push──▶ Publish Production release
```

### Build job

`shared-build-and-upload-artifact.yml` does these steps:

1. Check out the code.
2. Record the commit SHA and the branch name as outputs.
3. Run `./gradlew assemble`.
4. Upload `build/distributions` as an artifact. The caller gives the artifact name.

### Deploy job

The alpha and production workflows have the same deploy job. Only the GitHub environment and the artifact name are different.

| Workflow | Environment | Artifact |
|---|---|---|
| Publish Alpha release | `alpha` | `alpha-release-artifact` |
| Publish Production release | `production` | `production-release-artifact` |

The deploy job does these steps:

1. Download the artifact to `distributions`.
2. Clone the release repository to `working`.
3. In `working`, run `sh tools/release.sh "../distributions" "<branch>-<sha>"`.

`tools/release.sh` is in the release repository, not in this repository.

### Configuration

Each GitHub environment must have these values:

| Name | Type | Description |
|---|---|---|
| `TARGET_AUTH_KEY` | Secret | The key to push to the release repository. |
| `TARGET_RELEASE_REPOSITORY` | Variable | The address of the release repository, without `https://`. |

The Auto merge workflow uses one repository variable:

| Name | Type | Description |
|---|---|---|
| `ALPHA_BRANCH` | Variable | The alpha branch. Publish Alpha release runs only for a branch that matches `release/alpha/**`. |

### Auto merge

After each push to `main`, the Auto merge workflow does these steps:

1. Check out `ALPHA_BRANCH`.
2. Reset it to `main`.
3. Open or update a pull request from the `auto-merge-alpha-release` branch. The pull request has the label `bot`.

When a maintainer merges this pull request, the alpha branch changes. Then Publish Alpha release runs.

### Make a production release

1. Make sure that the alpha release works.
2. Push the commit to `release/product`. Or start Publish Production release manually from the Actions tab.
3. Make sure that the deploy job passes.

The application is at [app.monosketch.io](https://app.monosketch.io/).

## Other GitHub files

| File | Purpose |
|---|---|
| `.github/ISSUE_TEMPLATE/bug_report.md` | The template for bug reports. |
| `.github/FUNDING.yml` | The sponsor links. |
