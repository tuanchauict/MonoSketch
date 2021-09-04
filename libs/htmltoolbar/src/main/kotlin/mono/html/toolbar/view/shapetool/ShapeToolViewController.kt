package mono.html.toolbar.view.shapetool

import mono.html.toolbar.ActionManager
import mono.html.toolbar.RetainableActionType
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.Visibility
import mono.html.toolbar.view.shapetool.TextSectionViewController.TextAlignVisibility
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.extra.RectangleExtra
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import org.w3c.dom.HTMLElement

class ShapeToolViewController(
    lifecycleOwner: LifecycleOwner,
    container: HTMLElement,
    actionManager: ActionManager,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>
) {
    private val singleShapeLiveData: LiveData<AbstractShape?> =
        combineLiveData(
            selectedShapesLiveData,
            shapeManagerVersionLiveData
        ) { selected, _ -> selected.singleOrNull() }

    private val shapesLiveData: LiveData<Set<AbstractShape>> =
        combineLiveData(
            selectedShapesLiveData,
            shapeManagerVersionLiveData
        ) { selected, _ -> selected }

    private val retainableActionLiveData: LiveData<RetainableActionType> =
        combineLiveData(
            actionManager.retainableActionLiveData,
            ShapeExtraManager.defaultExtraStateUpdateLiveData
        ) { action, _ -> action }

    private val appearanceDataController = AppearanceDataController(
        selectedShapesLiveData,
        shapeManagerVersionLiveData,
        actionManager
    )

    init {
        ReorderSectionViewController(
            lifecycleOwner,
            container,
            singleShapeLiveData,
            actionManager::setOneTimeAction
        )
        TransformToolViewController(
            lifecycleOwner,
            container,
            singleShapeLiveData,
            actionManager::setOneTimeAction
        )

        AppearanceSectionViewController(
            lifecycleOwner,
            container,
            fillOptions = getFillOptions(),
            strokeOptions = getBorderOptions(),
            headOptions = getHeadOptions(),
            appearanceDataController,
            actionManager::setOneTimeAction
        )

        TextSectionViewController(
            lifecycleOwner,
            container,
            createTextAlignLiveData(shapesLiveData, retainableActionLiveData),
            actionManager::setOneTimeAction
        )
    }

    private fun getFillOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedRectangleFillStyles()
            .map { OptionItem(it.id, it.displayName) }

    private fun getBorderOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedStrokeStyles()
            .map { OptionItem(it.id, it.displayName) }

    private fun getHeadOptions(): List<OptionItem> =
        ShapeExtraManager.getAllPredefinedAnchorChars()
            .map { OptionItem(it.id, it.displayName) }


    private fun createTextAlignLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<TextAlignVisibility> {
        val selectedTextAlignLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> TextAlignVisibility.Hide
                else -> {
                    val text = it.single() as? Text
                    val editableText = text?.takeIf(Text::isTextEditable)
                    editableText?.extra?.textAlign?.let(TextAlignVisibility::Visible)
                }
            }
        }
        val defaultTextAlignLiveData = retainableActionTypeLiveData.map {
            if (it == RetainableActionType.ADD_TEXT) {
                TextAlignVisibility.Visible(ShapeExtraManager.defaultTextAlign)
            } else {
                TextAlignVisibility.Hide
            }
        }
        return combineLiveData(
            selectedTextAlignLiveData,
            defaultTextAlignLiveData
        ) { selected, default -> selected ?: default }
    }
}
