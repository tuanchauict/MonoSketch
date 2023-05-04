/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view

import mono.actionmanager.ActionManager
import mono.html.toolbar.view.shapetool2.FooterView
import mono.html.toolbar.view.shapetool2.IndicatorView
import mono.html.toolbar.view.shapetool2.ReorderSectionView
import mono.html.toolbar.view.shapetool2.ShapeToolViewModel
import mono.html.toolbar.view.shapetool2.TransformationToolView
import mono.html.toolbar.view.utils.CssClass
import mono.html.toolbar.view.utils.bindClass
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.shape.AbstractShape
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement

class ShapeToolViewController2(
    lifecycleOwner: LifecycleOwner,
    container: HTMLElement,
    actionManager: ActionManager,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
    shapeManagerVersionLiveData: LiveData<Int>,
    shapeToolVisibilityLiveData: LiveData<Boolean>
) {
    private val viewModel = ShapeToolViewModel(
        lifecycleOwner,
        selectedShapesLiveData,
        shapeManagerVersionLiveData,
        shapeToolVisibilityLiveData
    )

    init {
        shapeToolVisibilityLiveData.observe(lifecycleOwner) {
            container.bindClass(CssClass.HIDE, !it)
        }
        renderComposable(container) {
            Div(
                attrs = { classes("shape-tools__body") }
            ) {
                ReorderSectionView(
                    isVisible = viewModel.reorderToolVisibilityState.value,
                    actionManager::setOneTimeAction
                )
                TransformationToolView(
                    viewModel.singleShapeBoundState.value,
                    viewModel.singleShapeResizeableState.value,
                    actionManager::setOneTimeAction
                )
                IndicatorView(isVisible = !viewModel.hasAnyToolState.value)
            }
            FooterView()
        }
    }
}
