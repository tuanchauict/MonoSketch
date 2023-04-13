/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName", "ClassName")

package mono.html.toolbar.view.utils

import mono.html.Svg
import mono.html.setAttributes
import org.w3c.dom.Element

internal fun Element.SvgIcon(size: Int, pathBlock: Element.() -> Unit = {}) =
    SvgIcon(size, size, pathBlock)

internal fun Element.SvgIcon(width: Int, height: Int, pathBlock: Element.() -> Unit): Element =
    Svg("bi bi-cursor-fill") {
        setAttributes(
            "width" to width,
            "height" to height,
            "fill" to "currentColor",
            "viewBox" to "0 0 $width $height"
        )

        pathBlock()
    }

internal fun Element.SvgIcon(
    width: Int,
    height: Int,
    viewPortWidth: Int,
    viewPortHeight: Int,
    pathBlock: Element.() -> Unit
): Element = Svg("bi bi-cursor-fill") {
    setAttributes(
        "width" to width,
        "height" to height,
        "fill" to "currentColor",
        "viewBox" to "0 0 $viewPortWidth $viewPortHeight"
    )

    pathBlock()
}
