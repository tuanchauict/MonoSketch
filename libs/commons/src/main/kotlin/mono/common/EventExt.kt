package mono.common

import kotlinx.browser.window
import org.w3c.dom.events.KeyboardEvent

val KeyboardEvent.commandKey: Boolean
    get() = if (window.isCommandKeySupported()) metaKey else ctrlKey
