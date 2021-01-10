rootProject.name = "MonoFlow"

val moduleMap = mapOf(
    "commons" to "libs/commons",
    "monoboard" to "libs/monoboard",
    "graphicsgeo" to "libs/graphicsgeo",
    "shape" to "libs/shape",
    "monobitmap" to "libs/monobitmap"
)

moduleMap.entries.forEach { (name, path) ->
    include(":$name")
    project(":$name").projectDir = File(path)
}
