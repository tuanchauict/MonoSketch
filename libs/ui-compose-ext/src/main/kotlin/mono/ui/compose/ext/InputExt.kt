/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.ui.compose.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement

@Composable
fun sideEffectFocus(inputSelector: String) {
    var isFocused by remember { mutableStateOf(false) }
    if (!isFocused) {
        SideEffect {
            val target = document.querySelector(inputSelector) as? HTMLElement
            target?.focus()
            if (target is HTMLInputElement) {
                target.setSelectionRange(Int.MAX_VALUE, Int.MAX_VALUE)
            }
        }
        isFocused = true
    }
}
