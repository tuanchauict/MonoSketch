package mono.html.toolbar.view.keyboardshortcut

import kotlinx.browser.document
import mono.html.Div
import org.w3c.dom.Element

/**
 * An HTML component for displaying keyboard shortcut hints.
 */
class KeyboardShortcuts private constructor() {
    private val root: Element?

    init {
        root = document.body?.Div {

        }
    }


    companion object {
        fun showHint() {
            KeyboardShortcuts()
        }
    }
}
