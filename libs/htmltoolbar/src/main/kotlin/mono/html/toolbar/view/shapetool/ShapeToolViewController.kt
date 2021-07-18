package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.toolbar.ActionManager
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolState
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolType
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolType.*
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MediatorLiveData
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import mono.shape.shape.extra.RectangleExtra
import org.w3c.dom.HTMLElement

class ShapeToolViewController(
    lifecycleOwner: LifecycleOwner,
    controller: HTMLElement,
    actionManager: ActionManager,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>
) {
    init {
        controller.append {
            val moveTool = ReorderSection(actionManager::setOneTimeAction)
            val transformTool = TransformSection(actionManager::setOneTimeAction)

            val appearanceTool = AppearanceSection()
            val textAlignmentTool = TextSection(actionManager::setOneTimeAction)

            val singleShapeLiveData = MediatorLiveData<AbstractShape?>(null).apply {
                add(selectedShapesLiveData) {
                    value = it.singleOrNull()
                }
                add(shapeManagerVersionLiveData) {
                    value = value
                }
            }

            singleShapeLiveData.observe(lifecycleOwner) {
                moveTool.setEnabled(it != null)

                val isSizeChangeable = it is Rectangle || it is Text
                transformTool.setEnabled(it != null, isSizeChangeable)
                if (it != null) {
                    transformTool.setValue(it.bound)
                }

                val textAlign = (it as? Text)?.extra?.textAlign
                textAlignmentTool.setCurrentTextAlign(textAlign)

                val visibleAppearanceTools = when (it) {
                    is Rectangle -> it.extra.toAppearanceVisibilityState()
                    is Text -> it.extra.boundExtra.toAppearanceVisibilityState()
                    is Line -> it.toAppearanceVisibilityState()

                    is MockShape,
                    is Group,
                    null -> emptyMap()
                }
                appearanceTool.setVisibility(visibleAppearanceTools)
            }
        }
    }

    private fun RectangleExtra.toAppearanceVisibilityState(): Map<ToolType, ToolState> = mapOf(
        FILL to ToolState(isFillEnabled, 0),
        BORDER to ToolState(isBorderEnabled, 0)
    )

    // TODO: Correct state values
    private fun Line.toAppearanceVisibilityState(): Map<ToolType, ToolState> = mapOf(
        STROKE to ToolState(false, 0),
        START_HEAD to ToolState(false, 0),
        END_HEAD to ToolState(false, 0)
    )
}
