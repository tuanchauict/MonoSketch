rootProject.name = "MonoFlow"

val moduleMap = mapOf(
    "graphics" to "libs/graphics"
)

moduleMap.entries.forEach { (name, path) ->
    include(":$name")
    project(":$name").projectDir = File(path)
}

