/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar

import kotlinx.dom.addClass
import mono.actionmanager.ActionManager
import mono.actionmanager.RetainableActionType
import mono.html.A
import mono.html.Div
import mono.html.Span
import mono.html.SvgIcon
import mono.html.style
import mono.html.toolbar.view.shapetool.AppearanceDataController
import mono.html.toolbar.view.shapetool.AppearanceSectionViewController
import mono.html.toolbar.view.shapetool.ReorderSectionViewController
import mono.html.toolbar.view.shapetool.Section
import mono.html.toolbar.view.shapetool.TextSectionViewController
import mono.html.toolbar.view.shapetool.TextSectionViewController.TextAlignVisibility
import mono.html.toolbar.view.shapetool.TransformToolViewController
import mono.html.toolbar.view.utils.CssClass
import mono.html.toolbar.view.utils.bindClass
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.combineLiveData
import mono.livedata.map
import mono.shape.ShapeExtraManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Text
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement

class ShapeToolViewController(
    lifecycleOwner: LifecycleOwner,
    container: HTMLElement,
    actionManager: ActionManager,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>,
    shapeToolVisibilityLiveData: LiveData<Boolean>
) {
    private val singleShapeLiveData: LiveData<AbstractShape?> = combineLiveData(
        selectedShapesLiveData,
        shapeManagerVersionLiveData
    ) { selected, _ -> selected.singleOrNull() }

    private val shapesLiveData: LiveData<Set<AbstractShape>> = combineLiveData(
        selectedShapesLiveData,
        shapeManagerVersionLiveData
    ) { selected, _ -> selected }

    private val retainableActionLiveData: LiveData<RetainableActionType> = combineLiveData(
        actionManager.retainableActionLiveData,
        ShapeExtraManager.defaultExtraStateUpdateLiveData
    ) { action, _ -> action }

    private val appearanceDataController = AppearanceDataController(
        selectedShapesLiveData,
        shapeManagerVersionLiveData,
        actionManager
    )

    init {
        val toolContainer = container.Div(classes = "shape-tools__body")

        val reorderSectionViewController = ReorderSectionViewController(
            lifecycleOwner,
            toolContainer,
            singleShapeLiveData,
            actionManager::setOneTimeAction
        )

        val transformToolViewController = TransformToolViewController(
            lifecycleOwner,
            toolContainer,
            singleShapeLiveData,
            actionManager::setOneTimeAction
        )

        val appearanceSectionViewController = AppearanceSectionViewController(
            lifecycleOwner,
            toolContainer,
            appearanceDataController
        )

        val textSectionViewController = TextSectionViewController(
            lifecycleOwner,
            toolContainer,
            createTextAlignLiveData(shapesLiveData, retainableActionLiveData),
            actionManager::setOneTimeAction
        )

        val hasAnyVisibleToolLiveData = combineLiveData(
            reorderSectionViewController.visibilityStateLiveData,
            transformToolViewController.visibilityStateLiveData,
            appearanceSectionViewController.visibilityStateLiveData,
            textSectionViewController.visibilityStateLiveData
        ) { visibilities ->
            visibilities.any { it == true }
        }
        addToolIndicatorView(
            lifecycleOwner,
            toolContainer,
            hasAnyVisibleToolLiveData
        )

        addFooterView(container)

        shapeToolVisibilityLiveData.observe(lifecycleOwner) {
            container.bindClass(CssClass.HIDE, !it)
        }
    }

    private fun addToolIndicatorView(
        lifecycleOwner: LifecycleOwner,
        toolContainer: HTMLDivElement,
        hasAnyVisibleToolLiveData: LiveData<Boolean>
    ) {
        val toolIndicatorView = toolContainer.Section(hasBorderTop = false) {
            Div {
                Span(
                    classes = "indicator-text",
                    text = "Select a shape for updating its properties here"
                )
            }
        }

        toolIndicatorView.addClass("tool-indicator")
        hasAnyVisibleToolLiveData.observe(lifecycleOwner) {
            toolIndicatorView.bindClass(CssClass.HIDE, it)
        }
    }

    private fun addFooterView(container: HTMLElement) {
        container.Div(classes = "shape-tools__footer") {
            A(href = "https://github.com/tuanchauict/MonoSketch") {
                target = "_blank"

                SvgIcon(
                    width = 16,
                    height = 16,
                    viewPortWidth = 32,
                    viewPortHeight = 32,
                    "M16.288 0a16.291 16.291 0 0 0-5.148 31.747c.815.149 1.112-.353 1.112-.785 0-.387-.014-1.411-.022-2.771-4.531.985-5.487-2.183-5.487-2.183a4.315 4.315 0 0 0-1.809-2.383c-1.479-1.011.112-.99.112-.99a3.42 3.42 0 0 1 2.495 1.678 3.468 3.468 0 0 0 4.741 1.354 3.482 3.482 0 0 1 1.034-2.178c-3.617-.411-7.42-1.808-7.42-8.051a6.3 6.3 0 0 1 1.677-4.371 5.852 5.852 0 0 1 .16-4.311s1.367-.438 4.479 1.67a15.448 15.448 0 0 1 8.156 0c3.11-2.108 4.475-1.67 4.475-1.67a5.854 5.854 0 0 1 .163 4.311 6.286 6.286 0 0 1 1.674 4.371c0 6.258-3.809 7.635-7.438 8.038a3.889 3.889 0 0 1 1.106 3.017c0 2.178-.02 3.935-.02 4.469 0 .435.294.942 1.12.783A16.292 16.292 0 0 0 16.288 0z"
                )
                Span(text = "GitHub") {
                    style("margin-left" to "6px")
                }
            }
        }
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
        return combineLiveData(
            selectedTextAlignLiveData,
            defaultTextAlignLiveData
        ) { selected, default -> selected ?: default }
    }
}
