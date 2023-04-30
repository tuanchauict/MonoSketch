/*
 * Copyright (c) 2023, tuanchauict
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "MonoSketch"

val moduleMap = mapOf(
    "app" to "app",
    "action-manager" to "libs/action-manager",
    "build-environment" to "libs/build-environment",
    "commons" to "libs/commons",
    "export-shapes-modal" to "libs/export-shapes-modal",
    "graphicsgeo" to "libs/graphicsgeo",
    "html-dsl" to "libs/html-dsl",
    "keycommand" to "libs/keycommand",
    "lifecycle" to "libs/lifecycle",
    "livedata" to "libs/livedata",
    "monobitmap" to "libs/monobitmap",
    "monobitmap-manager" to "libs/monobitmap-manager",
    "monoboard" to "libs/monoboard",
    "shape" to "libs/shape",
    "shape-clipboard" to "libs/shape-clipboard",
    "shape-interaction-bound" to "libs/shape-interaction-bound",
    "shape-selection" to "libs/shape-selection",
    "shape-serialization" to "libs/shape-serialization",
    "shapesearcher" to "libs/shapesearcher",
    "statemanager" to "libs/statemanager",
    "store-dao" to "libs/store-dao",
    "store-manager" to "libs/store-manager",
    "ui-app-state-manager" to "libs/ui-app-state-manager",
    "ui-canvas" to "libs/ui-canvas",
    "ui-modal" to "libs/ui-modal",
    "ui-theme" to "libs/ui-theme",
    "ui-toolbar" to "libs/ui-toolbar",
    "uuid" to "libs/uuid",
)

moduleMap.entries.forEach { (name, path) ->
    include(":$name")
    project(":$name").projectDir = File(path)
}

// Enable Gradle's version catalog support
// https://docs.gradle.org/current/userguide/platforms.html
enableFeaturePreview("VERSION_CATALOGS")
// Enable Type-safe project dependencies
// https://docs.gradle.org/7.0/userguide/declaring_dependencies.html#sec:type-safe-project-accessors
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
