package mono.html.modal

import kotlinx.browser.document
import kotlinx.html.TagConsumer
import kotlinx.html.dom.append
import kotlinx.html.js.div
import kotlinx.html.js.onClickFunction
import kotlinx.html.style
import mono.lifecycle.LifecycleOwner
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

/**
 * An abstract class which defines a full screen modal with an empty content container.
 * By setting [isCancelable] true, this modal will automatically dismiss when clicking outside the
 * content zone.
 * This class is a [LifecycleOwner].
 */
abstract class BaseHtmlFullscreenModal(
    private val modalContainer: HTMLElement? = document.body,
) : LifecycleOwner() {
    protected open val isCancelable: Boolean = true
    protected open val contentPosition: ModalPosition =
        ModalPosition(ModalPosition.Horizontal.MIDDLE, ModalPosition.Vertical.MIDDLE)
    protected open val backgroundRGBAColor: String = "#00000010"

    private var root: HTMLDivElement? = null

    private var onDismiss: () -> Unit = {}

    fun show() {
        val body = modalContainer ?: document.body ?: return
        body.append {
            root = div {
                style = "background-color: $backgroundRGBAColor; $STYLE_POSITION"

                div {
                    style = "${contentPosition.toStyle()};"
                    div {
                        initContent()
                    }

                    onClickFunction = {
                        it.stopPropagation()
                    }
                }

                onClickFunction = {
                    if (isCancelable) {
                        dismiss()
                    }
                }
            }
        }

        onStart()
    }

    protected abstract fun TagConsumer<HTMLElement>.initContent()

    fun dismiss() {
        onStop()
        root?.remove()
        onDismiss()
    }

    fun setOnDismiss(onDismiss: () -> Unit) {
        this.onDismiss = onDismiss
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
    }
}
