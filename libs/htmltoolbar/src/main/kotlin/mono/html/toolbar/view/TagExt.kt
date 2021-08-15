package mono.html.toolbar.view

import kotlinx.dom.addClass
import kotlinx.dom.hasClass
import kotlinx.dom.removeClass
import mono.html.toolbar.view.shapetool.Class
import mono.html.toolbar.view.shapetool.Class.DISABLED
import mono.html.toolbar.view.shapetool.Class.HIDE
import mono.html.toolbar.view.shapetool.Class.SELECTED
import org.w3c.dom.HTMLElement

internal var HTMLElement.isVisible: Boolean
    get() = hasClass(HIDE)
    set(value) {
        addOrRemove(HIDE, !value)
    }

internal var HTMLElement.isSelected: Boolean
    get() = hasClass(SELECTED)
    set(value) {
        addOrRemove(SELECTED, value)
    }

internal var HTMLElement.isEnabled: Boolean
    get() = !hasClass(DISABLED)
    set(value) {
        addOrRemove(DISABLED, !value)
    }

private fun HTMLElement.hasClass(cls: Class) = hasClass(cls.value)

fun HTMLElement.addOrRemove(cls: Class, willAdd: Boolean) {
    if (willAdd) {
        addClass(cls.value)
    } else {
        removeClass(cls.value)
    }
}
