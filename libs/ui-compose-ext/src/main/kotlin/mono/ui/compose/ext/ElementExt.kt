/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.ui.compose.ext

import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element

fun AttrsScope<Element>.classes(vararg classToAvailability: Pair<String, Boolean>) =
    classes(classToAvailability.mapNotNull { (name, isEnabled) -> if (isEnabled) name else null })
