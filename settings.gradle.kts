rootProject.name = "MonoSketch"

val moduleMap = mapOf(
    "app" to "app",
    "build-environment" to "libs/build-environment",
    "commons" to "libs/commons",
    "export-shapes-modal" to "libs/export-shapes-modal",
    "graphicsgeo" to "libs/graphicsgeo",
    "html-ext" to "libs/html-ext",
    "htmlcanvas" to "libs/htmlcanvas",
    "htmlmodal" to "libs/htmlmodal",
    "htmltoolbar" to "libs/htmltoolbar",
    "keycommand" to "libs/keycommand",
    "lifecycle" to "libs/lifecycle",
    "livedata" to "libs/livedata",
    "monobitmap" to "libs/monobitmap",
    "monobitmap-manager" to "libs/monobitmap-manager",
    "monoboard" to "libs/monoboard",
    "shape" to "libs/shape",
    "shape-clipboard" to "libs/shape-clipboard",
    "shape-extra-manager" to "libs/shape-extra-manager",
    "shape-interaction-bound" to "libs/shape-interaction-bound",
    "shape-selection" to "libs/shape-selection",
    "shape-serialization" to "libs/shape-serialization",
    "shapesearcher" to "libs/shapesearcher",
    "statemanager" to "libs/statemanager",
    "store-manager" to "libs/store-manager",
    "uuid" to "libs/uuid"
)

moduleMap.entries.forEach { (name, path) ->
    include(":$name")
    project(":$name").projectDir = File(path)
}

// Enable Gradle's version catalog support
// https://docs.gradle.org/current/userguide/platforms.html
enableFeaturePreview("VERSION_CATALOGS")
