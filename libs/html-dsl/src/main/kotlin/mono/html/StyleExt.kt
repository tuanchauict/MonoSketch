package mono.html

val Number.px: String
    get() = "${this}px"

fun styleOf(vararg attributes: Pair<String, String>): String =
    attributes.asSequence().map { "${it.first}: ${it.second}" }.joinToString(";")
