rootProject.name = "MonoFlow"

val moduleMap = mapOf(
    "app" to "app",
    "commons" to "libs/commons",
    "monoboard" to "libs/monoboard",
    "graphicsgeo" to "libs/graphicsgeo",
    "shape" to "libs/shape",
    "shape-interaction-bound" to "libs/shape-interaction-bound",
    "shapesearcher" to "libs/shapesearcher",
    "monobitmap" to "libs/monobitmap",
    "htmlcanvas" to "libs/htmlcanvas",
    "htmltoolbar" to "libs/htmltoolbar",
    "htmlmodal" to "libs/htmlmodal",
    "keycommand" to "libs/keycommand",
    "statemanager" to "libs/statemanager",
    "livedata" to "libs/livedata",
    "lifecycle" to "libs/lifecycle"
)

moduleMap.entries.forEach { (name, path) ->
    include(":$name")
    project(":$name").projectDir = File(path)
}
