rootProject.name = "MonoFlow"

val moduleMap = mapOf(
    "graphics" to "libs/graphics",
    "graphicsgeo" to "libs/graphicsgeo"
)

moduleMap.entries.forEach { (name, path) ->
    include(":$name")
    project(":$name").projectDir = File(path)
}
