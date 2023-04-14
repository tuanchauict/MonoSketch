/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html

import org.w3c.dom.Element

fun Element.SvgIcon(size: Int, vararg paths: String, block: Element.() -> Unit = {}): Element =
    SvgIcon(size, size, size, size, *paths) { block() }

fun Element.SvgIcon(
    width: Int,
    height: Int,
    vararg paths: String,
    block: Element.() -> Unit = {}
): Element = SvgIcon(width, height, width, height, *paths) { block() }

fun Element.SvgIcon(
    width: Int,
    height: Int,
    viewPortWidth: Int,
    viewPortHeight: Int,
    vararg paths: String,
    block: Element.() -> Unit = {}
): Element = Svg {
    setAttributes(
        "width" to width,
        "height" to height,
        "fill" to "currentColor",
        "viewBox" to "0 0 $viewPortWidth $viewPortHeight"
    )

    for (path in paths) {
        SvgPath(path)
    }

    block()
}
