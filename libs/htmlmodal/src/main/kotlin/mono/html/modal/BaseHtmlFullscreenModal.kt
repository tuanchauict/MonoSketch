package mono.html.modal

import kotlinx.browser.document
import kotlinx.html.dom.append
import kotlinx.html.id
import kotlinx.html.js.div
import kotlinx.html.style
import mono.common.getOnlyElementByClassName
import mono.common.onClick
import mono.lifecycle.LifecycleOwner
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

/**
 * An abstract class which defines a full screen modal with an empty content container.
 * By setting [isCancelable] true, this modal will automatically dismiss when clicking outside the
 * content zone.
 * This class is a [LifecycleOwner].
 */
abstract class BaseHtmlFullscreenModal : LifecycleOwner() {
    protected open val isCancelable: Boolean = true
    protected open val contentPosition: ModalPosition =
        ModalPosition(ModalPosition.Horizontal.MIDDLE, ModalPosition.Vertical.MIDDLE)
    protected open val backgroundRGBAColor: String = "#00000030"

    private var root: HTMLDivElement? = null

    fun show() {
        val body = document.body ?: return
        val rootId = generateUniqueModalId()
        body.append {
            div {
                id = rootId
                style = "background-color: $backgroundRGBAColor; $STYLE_POSITION"

                div(MAIN_CONTAINER_CSS_CLASS) {
                    style = "${contentPosition.toStyle()};"
                    div(PARENT_CONTAINER_CSS_CLASS) {}
                }
            }
        }
        val rootDiv = document.getElementById(rootId) as? HTMLDivElement ?: return
        root = rootDiv

        rootDiv.getOnlyElementByClassName<HTMLDivElement>(PARENT_CONTAINER_CSS_CLASS)
            ?.let(::initContent)

        rootDiv.getOnlyElementByClassName<HTMLDivElement>(MAIN_CONTAINER_CSS_CLASS)?.onClick { }
        rootDiv.onClick {
            if (isCancelable) {
                dismiss()
            }
        }
        onStart()
    }

    protected abstract fun initContent(parent: HTMLElement)

    fun dismiss() {
        onStop()
        root?.remove()
    }

    data class ModalPosition(private val horizontal: Horizontal, private val vertical: Vertical) {

        fun toStyle(): String {
            val horizontalStyle = when (horizontal) {
                Horizontal.LEFT -> "left: 0"
                Horizontal.MIDDLE -> "left: 50%;"
                Horizontal.RIGHT -> "right: 0"
            }

            val verticalStyle = when (vertical) {
                Vertical.TOP -> "top: 0"
                Vertical.MIDDLE -> "top: 50%"
                Vertical.BOTTOM -> "bottom: 0"
            }
            val transformX = if (horizontal == Horizontal.MIDDLE) "-50%" else "0"
            val transformY = if (vertical == Vertical.MIDDLE) "-50%" else "0"
            val transformStyle = "transform: translate($transformX, $transformY)"

            return "position: absolute; $horizontalStyle; $verticalStyle; $transformStyle"
        }

        enum class Horizontal {
            LEFT, MIDDLE, RIGHT
        }

        enum class Vertical {
            TOP, MIDDLE, BOTTOM
        }
    }

    companion object {
        private const val STYLE_POSITION =
            "position: absolute; left: 0; top: 0; right: 0; bottom: 0; z-index: 1000"
        private const val MAIN_CONTAINER_CSS_CLASS = "modal-main-container"
        private const val PARENT_CONTAINER_CSS_CLASS = "modal-parent-container"

        private var NEXT_MODAL_ID = 0
        private fun generateUniqueModalId(): String {
            val id = NEXT_MODAL_ID
            NEXT_MODAL_ID += 1
            return "html-modal-id-$id"
        }
    }
}
