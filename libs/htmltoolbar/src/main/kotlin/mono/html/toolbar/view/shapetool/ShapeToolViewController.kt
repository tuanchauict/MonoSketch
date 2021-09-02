package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.toolbar.ActionManager
import mono.html.toolbar.RetainableActionType
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.ToolType
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController.Visibility
import mono.html.toolbar.view.shapetool.TextSectionViewController.TextAlignVisibility
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MediatorLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import mono.shape.extra.RectangleExtra
import org.w3c.dom.HTMLElement

class ShapeToolViewController(
    lifecycleOwner: LifecycleOwner,
    controller: HTMLElement,
    actionManager: ActionManager,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>
) {
    private val singleShapeLiveData: LiveData<AbstractShape?> =
        MediatorLiveData<AbstractShape?>(null).apply {
            add(selectedShapesLiveData) {
                value = it.singleOrNull()
            }
            add(shapeManagerVersionLiveData) {
                value = value
            }
        }

    private val shapesLiveData = MediatorLiveData<Set<AbstractShape>>(emptySet()).apply {
        add(selectedShapesLiveData) {
            value = it
        }
        add(shapeManagerVersionLiveData) {
            value = value
        }
    }
    private val retainableActionLiveData =
        MediatorLiveData(actionManager.retainableActionLiveData.value).apply {
            add(actionManager.retainableActionLiveData) {
                value = it
            }
            add(ShapeExtraManager.defaultExtraStateUpdateLiveData) {
                value = value
            }
        }

    init {
        ReorderSectionViewController(
            lifecycleOwner,
            controller,
            singleShapeLiveData,
            actionManager::setOneTimeAction
        )

        controller.append {

            val transformTool = TransformSection(actionManager::setOneTimeAction)

            val appearanceTool = AppearanceSection(
                fillOptions = getFillOptions(),
                borderOptions = getBorderOptions(),
                headOptions = getHeadOptions(),
                actionManager::setOneTimeAction
            )

            val textAlignmentTool = TextSection(actionManager::setOneTimeAction)

            singleShapeLiveData.observe(lifecycleOwner) {
                val isSizeChangeable = it is Rectangle || it is Text
                transformTool.setEnabled(it != null, isSizeChangeable)
                if (it != null) {
                    transformTool.setValue(it.bound)
                }
            }

            val fillAppearanceVisibilityLiveData = createFillAppearanceVisibilityLiveData(
                shapesLiveData,
                retainableActionLiveData
            )
            val borderAppearanceVisibilityLiveData = createBorderAppearanceVisibilityLiveData(
                shapesLiveData,
                retainableActionLiveData
            )
            val startHeadAppearanceVisibilityLiveData = createStartHeadAppearanceVisibilityLiveData(
                shapesLiveData,
                retainableActionLiveData
            )
            val endHeadAppearanceVisibilityLiveData = createEndHeadAppearanceVisibilityLiveData(
                shapesLiveData,
                retainableActionLiveData
            )
            MediatorLiveData(emptyMap<ToolType, Visibility>())
                .apply {
                    add(fillAppearanceVisibilityLiveData) {
                        value = value + (ToolType.FILL to it)
                    }
                    add(borderAppearanceVisibilityLiveData) {
                        value = value + (ToolType.BORDER to it)
                    }
                    add(startHeadAppearanceVisibilityLiveData) {
                        value = value + (ToolType.START_HEAD to it)
                    }
                    add(endHeadAppearanceVisibilityLiveData) {
                        value = value + (ToolType.END_HEAD to it)
                    }
                }
                .observe(lifecycleOwner, listener = appearanceTool::setVisibility)

            createTextAlignLiveData(shapesLiveData, retainableActionLiveData)
                .observe(lifecycleOwner, listener = textAlignmentTool::setCurrentTextAlign)
        }
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

    private fun createFillAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Rectangle -> shape.extra.toFillAppearanceVisibilityState()
                        is Text -> shape.extra.boundExtra.toFillAppearanceVisibilityState()
                        is Group,
                        is Line,
                        is MockShape -> Visibility.Hide
                    }
                }
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT ->
                    ShapeExtraManager.defaultRectangleExtra.userSelectedFillStyle
                RetainableActionType.ADD_LINE,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedRectangleFillStyles().indexOf(defaultState)
                Visibility.Visible(
                    ShapeExtraManager.defaultRectangleExtra.isFillEnabled,
                    selectedFillPosition
                )
            } else {
                Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createBorderAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Rectangle -> shape.extra.toBorderAppearanceVisibilityState()
                        is Text -> shape.extra.boundExtra.toBorderAppearanceVisibilityState()
                        is Group,
                        is Line,
                        is MockShape -> Visibility.Hide
                    }
                }
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT ->
                    ShapeExtraManager.defaultRectangleExtra.userSelectedBorderStyle
                RetainableActionType.ADD_LINE,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(defaultState)
                Visibility.Visible(
                    ShapeExtraManager.defaultRectangleExtra.isBorderEnabled,
                    selectedFillPosition
                )
            } else {
                Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createStartHeadAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.toStartHeadAppearanceVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> Visibility.Hide
                    }
                }
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_LINE ->
                    ShapeExtraManager.defaultLineExtra.userSelectedStartAnchor
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedStartHeaderPosition =
                    ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(defaultState)
                Visibility.Visible(
                    ShapeExtraManager.defaultLineExtra.isStartAnchorEnabled,
                    selectedStartHeaderPosition
                )
            } else {
                Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createEndHeadAppearanceVisibilityLiveData(
        selectedShapesLiveData: LiveData<Set<AbstractShape>>,
        retainableActionTypeLiveData: LiveData<RetainableActionType>
    ): LiveData<Visibility> {
        val selectedVisibilityLiveData = selectedShapesLiveData.map {
            when {
                it.isEmpty() -> null
                it.size > 1 -> Visibility.Hide
                else -> {
                    when (val shape = it.single()) {
                        is Line -> shape.toEndHeadAppearanceVisibilityState()
                        is Rectangle,
                        is Text,
                        is Group,
                        is MockShape -> Visibility.Hide
                    }
                }
            }
        }
        val defaultVisibilityLiveData = retainableActionTypeLiveData.map {
            val defaultState = when (it) {
                RetainableActionType.ADD_LINE ->
                    ShapeExtraManager.defaultLineExtra.userSelectedEndAnchor
                RetainableActionType.ADD_RECTANGLE,
                RetainableActionType.ADD_TEXT,
                RetainableActionType.IDLE -> null
            }
            if (defaultState != null) {
                val selectedFillPosition =
                    ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(defaultState)
                Visibility.Visible(
                    ShapeExtraManager.defaultLineExtra.isEndAnchorEnabled,
                    selectedFillPosition
                )
            } else {
                Visibility.Hide
            }
        }

        return createAppearanceVisibilityLiveData(
            selectedVisibilityLiveData,
            defaultVisibilityLiveData
        )
    }

    private fun createAppearanceVisibilityLiveData(
        selectedShapeVisibilityLiveData: LiveData<Visibility?>,
        defaultVisibilityLiveData: LiveData<Visibility>
    ): LiveData<Visibility> = MediatorLiveData(Pair<Visibility?, Visibility>(null, Visibility.Hide))
        .apply {
            add(selectedShapeVisibilityLiveData) {
                value = value.copy(first = it)
            }
            add(defaultVisibilityLiveData) {
                value = value.copy(second = it)
            }
        }
        .map { it.first ?: it.second }

    private fun RectangleExtra.toFillAppearanceVisibilityState(): Visibility {
        val selectedFillPosition =
            ShapeExtraManager.getAllPredefinedRectangleFillStyles()
                .indexOf(userSelectedFillStyle)
        return Visibility.Visible(isFillEnabled, selectedFillPosition)
    }

    private fun RectangleExtra.toBorderAppearanceVisibilityState(): Visibility {
        val selectedBorderPosition =
            ShapeExtraManager.getAllPredefinedStrokeStyles().indexOf(userSelectedBorderStyle)
        return Visibility.Visible(isBorderEnabled, selectedBorderPosition)
    }

    private fun Line.toStartHeadAppearanceVisibilityState(): Visibility {
        val selectedStartHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedStartAnchor)
        return Visibility.Visible(extra.isStartAnchorEnabled, selectedStartHeadPosition)
    }

    private fun Line.toEndHeadAppearanceVisibilityState(): Visibility {
        val selectedEndHeadPosition =
            ShapeExtraManager.getAllPredefinedAnchorChars().indexOf(extra.userSelectedEndAnchor)
        return Visibility.Visible(extra.isEndAnchorEnabled, selectedEndHeadPosition)
    }

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
        return MediatorLiveData<Pair<TextAlignVisibility?, TextAlignVisibility>>(
            null to TextAlignVisibility.Hide
        )
            .apply {
                add(selectedTextAlignLiveData) {
                    value = value.copy(first = it)
                }
                add(defaultTextAlignLiveData) {
                    value = value.copy(second = it)
                }
            }
            .map { it.first ?: it.second }
    }
}
