/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.ui.compose.ext

import androidx.compose.web.events.SyntheticMouseEvent
import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element

fun AttrsScope<Element>.classes(vararg classToAvailability: Pair<String, Boolean>) =
    classes(classToAvailability.mapNotNull { (name, isEnabled) -> if (isEnabled) name else null })

/**
 * Binds classes, each element can be either a `String` or a `Pair<String, Boolean>`.
 */
fun AttrsScope<Element>.classes(vararg classToAvailability: Any) {
    val classes = classToAvailability.mapNotNull {
        if (it is Pair<*, *>) {
            if (it.second as Boolean) it.first.toString() else null
        } else {
            it.toString()
        }
    }
    classes(classes)
}

fun AttrsScope<Element>.onConsumeClick(listener: (SyntheticMouseEvent) -> Unit) {
    onClick {
        listener(it)
        it.preventDefault()
        it.stopPropagation()
    }
}
