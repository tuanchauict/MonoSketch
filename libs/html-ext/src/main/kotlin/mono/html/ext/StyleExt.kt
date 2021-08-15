package mono.html.ext

import kotlinx.html.TagConsumer
import org.w3c.dom.HTMLElement

typealias Tag = TagConsumer<HTMLElement>

val Number.px: String
    get() = "${this}px"

@Suppress("unused")
fun Tag.styleOf(vararg attributes: Pair<String, String>): String =
    attributes.asSequence().map { "${it.first}: ${it.second}" }.joinToString(";")
