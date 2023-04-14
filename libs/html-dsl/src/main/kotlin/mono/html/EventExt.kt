/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

fun Element.setOnClickListener(listener: (Event) -> Unit) = addEventListener("click", listener)

fun Element.setOnMouseWheelListener(listener: (Event) -> Unit) =
    addEventListener("mousewheel", listener)

fun HTMLInputElement.setOnChangeListener(listener: (Event) -> Unit) =
    addEventListener("change", listener)

fun Element.setOnMouseOverListener(listener: (Event) -> Unit) =
    addEventListener("mouseover", listener)

fun Element.setOnMouseOutListener(listener: (Event) -> Unit) =
    addEventListener("mouseout", listener)

fun Element.setOnFocusOut(listener: (Event) -> Unit) =
    addEventListener("focusout", listener)
