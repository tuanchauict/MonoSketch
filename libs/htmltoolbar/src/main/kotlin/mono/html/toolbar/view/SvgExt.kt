@file:Suppress("FunctionName", "ClassName")

package mono.html.toolbar.view

import kotlinx.html.HTMLTag
import kotlinx.html.HtmlBlockInlineTag
import kotlinx.html.SVG
import kotlinx.html.TagConsumer
import kotlinx.html.attributesMapOf
import kotlinx.html.svg
import kotlinx.html.visit
import mono.html.Svg
import mono.html.ext.Tag
import mono.html.setAttributes
import org.w3c.dom.Element

internal fun Element.SvgIcon(width: Int, height: Int, pathBlock: Element.() -> Unit) {
    Svg("bi bi-cursor-fill") {
        setAttributes(
            "width" to width,
            "height" to height,
            "fill" to "currentColor",
            "viewBox" to "0 0 $width $height"
        )

        pathBlock()
    }
}

internal fun Tag.SvgIcon(
    width: Int,
    height: Int,
    viewPortWidth: Int,
    viewPortHeight: Int,
    pathBlock: SVG.() -> Unit
) {
    svg("bi bi-cursor-fill") {
        attributes["width"] = "$width"
        attributes["height"] = "$height"
        attributes["fill"] = "currentColor"
        attributes["viewBox"] = "0 0 $viewPortWidth $viewPortHeight"

        pathBlock()
    }
}

internal inline fun Tag.SvgPath(
    path: String,
    crossinline block: SVG_PATH.() -> Unit = {}
) = SVG_PATH(attributesMapOf("d", path), this).visit(block)

internal class SVG_PATH(
    initialAttributes: Map<String, String>,
    override val consumer: TagConsumer<*>
) :
    HTMLTag(
        tagName = "path",
        consumer,
        initialAttributes,
        namespace = "http://www.w3.org/2000/svg",
        inlineTag = true,
        emptyTag = true
    ),
    HtmlBlockInlineTag
