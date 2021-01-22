rootProject.name = "MonoFlow"

val moduleMap = mapOf(
    "app" to "app",
    "commons" to "libs/commons",
    "monoboard" to "libs/monoboard",
    "graphicsgeo" to "libs/graphicsgeo",
    "shape" to "libs/shape",
    "monobitmap" to "libs/monobitmap",
    "htmlcanvas" to "libs/htmlcanvas",
    "livedata" to "libs/livedata",
    "lifecycle" to "libs/lifecycle"
)

moduleMap.entries.forEach { (name, path) ->
    include(":$name")
    project(":$name").projectDir = File(path)
}
