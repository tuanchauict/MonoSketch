rootProject.name = "MonoFlow"

val moduleMap = mapOf(
    "app" to "app",
    "commons" to "libs/commons",
    "export-shapes-modal" to "libs/export-shapes-modal",
    "graphicsgeo" to "libs/graphicsgeo",
    "htmlcanvas" to "libs/htmlcanvas",
    "htmlmodal" to "libs/htmlmodal",
    "htmltoolbar" to "libs/htmltoolbar",
    "keycommand" to "libs/keycommand",
    "lifecycle" to "libs/lifecycle",
    "livedata" to "libs/livedata",
    "monobitmap" to "libs/monobitmap",
    "monoboard" to "libs/monoboard",
    "shape" to "libs/shape",
    "shape-interaction-bound" to "libs/shape-interaction-bound",
    "shapesearcher" to "libs/shapesearcher",
    "statemanager" to "libs/statemanager"
)

moduleMap.entries.forEach { (name, path) ->
    include(":$name")
    project(":$name").projectDir = File(path)
}
