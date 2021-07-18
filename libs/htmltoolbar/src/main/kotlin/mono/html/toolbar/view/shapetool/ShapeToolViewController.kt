package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.toolbar.ActionManager
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolState
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolType
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MediatorLiveData
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import mono.shape.shape.extra.LineExtra
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

            val appearanceTool = AppearanceSection(
                fillOptions = getFillOptions(),
                borderOptions = getBorderOptions(),
                headOptions = getHeadOptions(),
                actionManager::setOneTimeAction
            )

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

    private fun getFillOptions(): List<OptionItem> {
        // TODO: Move this into a fill style manager class. This won't work well when user's style
        //  is supported.
        return RectangleExtra.FillStyle.PREDEFINED_STYLES.map {
            OptionItem(it.id, it.displayName)
        }
    }

    private fun getBorderOptions(): List<OptionItem> {
        // TODO: Move this into a border style manager class. This won't work well when user's style
        //  is supported.
        return RectangleExtra.BorderStyle.PREDEFINED_STYLES.map {
            OptionItem(it.id, it.displayName)
        }
    }

    private fun getHeadOptions(): List<OptionItem> {
        // TODO: Move this into a line head style manager class. This won't work well when user's 
        //  style is supported.
        return LineExtra.AnchorChar.PREDEFINED_ANCHOR_CHARS.map {
            OptionItem(it.id, it.displayName)
        }
    }

    private fun RectangleExtra.toAppearanceVisibilityState(): Map<ToolType, ToolState> {
        val selectedFillPosition =
            RectangleExtra.FillStyle.PREDEFINED_STYLES.indexOf(userSelectedFillStyle)
        val selectedBorderPosition =
            RectangleExtra.BorderStyle.PREDEFINED_STYLES.indexOf(userSelectedBorderStyle)
        return mapOf(
            ToolType.FILL to ToolState(isFillEnabled, selectedFillPosition),
            ToolType.BORDER to ToolState(isBorderEnabled, selectedBorderPosition)
        )
    }

    private fun Line.toAppearanceVisibilityState(): Map<ToolType, ToolState> {
        val selectedStartHeadPosition =
            LineExtra.AnchorChar.PREDEFINED_ANCHOR_CHARS.indexOf(extra.userSelectedStartAnchor)
        val selectedEndHeadPosition =
            LineExtra.AnchorChar.PREDEFINED_ANCHOR_CHARS.indexOf(extra.userSelectedEndAnchor)
        return mapOf(
            //        ToolType.STROKE to ToolState(false, 0),
            ToolType.START_HEAD to ToolState(extra.isStartHeadEnabled, selectedStartHeadPosition),
            ToolType.END_HEAD to ToolState(extra.isEndHeadEnabled, selectedEndHeadPosition)
        )
    }
}
