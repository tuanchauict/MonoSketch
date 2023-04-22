/*
 * Copyright (c) 2023, tuanchauict
 */

@file:Suppress("FunctionName")

package mono.html.toolbar.view.components

import mono.html.Div
import mono.html.bindClass
import mono.html.setOnClickListener
import org.w3c.dom.Element

internal fun Element.OptionCloud(
    itemContentFactory: CloudItemFactory,
    onSelect: (Int) -> Unit
): CloudViewBinder {
    val container = Div("comp-option-cloud-layout")
    val items = (0 until itemContentFactory.size).map { index ->
        container.Div("cloud-item") {
            itemContentFactory.create(this, index)

            setOnClickListener { onSelect(index) }
        }
    }
    return CloudViewBinder(items)
}

internal class CloudViewBinder(private val items: List<Element>) {
    fun setSelectedItem(selectedIndex: Int) {
        items.forEachIndexed { index, element ->
            element.bindClass("selected", index == selectedIndex)
        }
    }
}

internal class CloudItemFactory(val size: Int, val create: Element.(Int) -> Element)
