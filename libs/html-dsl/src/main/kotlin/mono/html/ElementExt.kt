package mono.html

import org.w3c.dom.Element
import org.w3c.dom.events.Event

fun Element.addOnClickListener(listener: (Event) -> Unit) = addEventListener("click", listener)
