/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.ui.compose.ext

import androidx.compose.runtime.Composable
import kotlinx.browser.document
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.Element

private class NSElementBuilder<TElement : Element>(private val tagName: String) :
    ElementBuilder<TElement> {
    private val el: Element by lazy {
        document.createElementNS(
            "http://www.w3.org/2000/svg",
            tagName
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun create(): TElement = el.cloneNode() as TElement
}

private val SVG = NSElementBuilder<Element>("svg")
private val PATH = NSElementBuilder<Element>("path")

@Composable
fun Svg(
    attrs: AttrBuilderContext<Element>? = null,
    content: ContentBuilder<Element>? = null
) = TagElement(SVG, attrs, content)

@Composable
fun SvgPath(path: String, attrs: AttrBuilderContext<Element>? = null) {
    TagElement(PATH, {
        attr("d", path)
        attrs?.invoke(this)
    }, null)
}

fun AttrsScope<Element>.size(width: Int, height: Int) {
    attr("width", width.toString())
    attr("height", height.toString())
}

fun AttrsScope<Element>.viewBox(width: Int, height: Int) {
    attr("viewBox", "0 0 $width $height")
}

fun AttrsScope<Element>.viewBox(left: Int, top: Int, width: Int, height: Int) {
    attr("viewBox", "$left $top $width $height")
}

fun AttrsScope<Element>.fill(color: String) {
    attr("fill", color)
}
