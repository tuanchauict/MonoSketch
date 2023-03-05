package mono.html.toolbar.view.shapetool

import kotlinx.dom.hasClass
import mono.html.bindClass
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

internal enum class CssClass(val value: String) {
    DISABLED("disabled"),
    HIDE("hide"),
    SELECTED("selected");
}

internal fun HTMLElement.hasClass(cls: CssClass) = hasClass(cls.value)

internal fun Element.bindClass(cssClass: CssClass, isApplicable: Boolean) =
    bindClass(cssClass.value, isApplicable)
