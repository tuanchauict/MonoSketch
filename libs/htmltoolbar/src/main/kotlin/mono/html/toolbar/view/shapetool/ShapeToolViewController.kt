package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.toolbar.ActionManager
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolType
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.Visibility
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MediatorLiveData
import mono.shape.extra.manager.ShapeExtraManager
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

    private fun getFillOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedRectangleFillStyles()
            .map { OptionItem(it.id, it.displayName) }

    private fun getBorderOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedRectangleBorderStyles()
            .map { OptionItem(it.id, it.displayName) }

    private fun getHeadOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedAnchorChars()
            .map { OptionItem(it.id, it.displayName) }

    private fun RectangleExtra.toAppearanceVisibilityState(): Map<ToolType, Visibility> = mapOf(
        ToolType.FILL to toFillAppearanceVisibilityState(),
        ToolType.BORDER to toBorderAppearanceVisibilityState()
    )

    private fun RectangleExtra.toFillAppearanceVisibilityState(): Visibility {
        val selectedFillPosition =
            ShapeExtraManager.getAllPredefinedRectangleFillStyles()
                .indexOf(userSelectedFillStyle)
        return Visibility.Visible(isFillEnabled, selectedFillPosition)
    }

    private fun RectangleExtra.toBorderAppearanceVisibilityState(): Visibility {
        val selectedBorderPosition =
            ShapeExtraManager.getAllPredefinedRectangleBorderStyles()
                .indexOf(userSelectedBorderStyle)
        return Visibility.Visible(isBorderEnabled, selectedBorderPosition)
    }

    private fun Line.toAppearanceVisibilityState(): Map<ToolType, Visibility> = mapOf(
        ToolType.START_HEAD to toStartHeadAppearanceVisibilityState(),
        ToolType.END_HEAD to toEndHeadAppearanceVisibilityState()
    )

    private fun Line.toStartHeadAppearanceVisibilityState(): Visibility {
        val selectedStartHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedStartAnchor)
        return Visibility.Visible(extra.isStartAnchorEnabled, selectedStartHeadPosition)
    }

    private fun Line.toEndHeadAppearanceVisibilityState(): Visibility {
        val selectedEndHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedEndAnchor)
        return Visibility.Visible(extra.isStartAnchorEnabled, selectedEndHeadPosition)
    }
}
