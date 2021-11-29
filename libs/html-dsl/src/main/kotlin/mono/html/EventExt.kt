package mono.html

import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

fun Element.setOnClickListener(listener: (Event) -> Unit) = addEventListener("click", listener)

fun Element.setOnMouseWheelListener(listener: (Event) -> Unit) =
    addEventListener("mousewheel", listener)

fun HTMLInputElement.setOnChangeListener(listener: (Event) -> Unit) =
    addEventListener("change", listener)

