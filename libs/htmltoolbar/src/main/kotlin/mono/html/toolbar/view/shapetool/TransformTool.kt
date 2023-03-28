@file:Suppress("FunctionName", "ktlint:filename")

package mono.html.toolbar.view.shapetool

import mono.actionmanager.OneTimeActionType
import mono.html.Div
import mono.html.InputType
import mono.html.toolbar.view.components.InputSettings
import mono.html.toolbar.view.components.TextInputBox
import mono.html.toolbar.view.components.TextInputBoxViewHolder
import mono.html.toolbar.view.utils.CssClass
import mono.html.toolbar.view.utils.bindClass
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.map
import mono.shape.shape.AbstractShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import org.w3c.dom.Element

internal class TransformToolViewController(
    lifecycleOwner: LifecycleOwner,
    container: Element,
    singleShapeLiveData: LiveData<AbstractShape?>,
    setOneTimeAction: (OneTimeActionType) -> Unit
) {
    val visibilityStateLiveData: LiveData<Boolean>

    init {
        container.Section("TRANSFORM") { section ->
            val gridNode = Div("transform-grid")
            val posSettings = InputSettings(InputType.NUMBER)
            val sizeSettings = InputSettings(InputType.NUMBER, "min" to "1")
            val xInput = gridNode.NumberCell("X", posSettings) {
                setOneTimeAction(OneTimeActionType.ChangeShapeBound(newLeft = it))
            }
            val yInput = gridNode.NumberCell("Y", posSettings) {
                setOneTimeAction(OneTimeActionType.ChangeShapeBound(newTop = it))
            }
            val wInput = gridNode.NumberCell("W", sizeSettings) {
                setOneTimeAction(OneTimeActionType.ChangeShapeBound(newWidth = it))
            }

            val hInput = gridNode.NumberCell("H", sizeSettings) {
                setOneTimeAction(OneTimeActionType.ChangeShapeBound(newHeight = it))
            }

            singleShapeLiveData.observe(lifecycleOwner) {
                section.bindClass(CssClass.HIDE, it == null)

                val isPositionEnabled = it != null
                val isSizeChangeable = it is Rectangle || it is Text
                xInput.isEnabled = isPositionEnabled
                yInput.isEnabled = isPositionEnabled
                wInput.isEnabled = isSizeChangeable
                hInput.isEnabled = isSizeChangeable

                if (it != null) {
                    xInput.value = it.bound.left
                    yInput.value = it.bound.top
                    wInput.value = it.bound.width
                    hInput.value = it.bound.height
                }
            }
        }

        visibilityStateLiveData = singleShapeLiveData.map { it != null }
    }

    private fun Element.NumberCell(
        title: String,
        inputSettings: InputSettings,
        onValueChange: (Int) -> Unit
    ): TextInputBoxViewHolder = Div("cell").TextInputBox(title, inputSettings) {
        onValueChange(it.toInt())
    }
}
