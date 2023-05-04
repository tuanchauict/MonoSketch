/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view

import mono.actionmanager.ActionManager
import mono.html.toolbar.view.shapetool2.FooterView
import mono.html.toolbar.view.shapetool2.IndicatorView
import mono.html.toolbar.view.shapetool2.ReorderSectionView
import mono.html.toolbar.view.shapetool2.ShapeToolViewModel
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
        renderComposable(container) {
            Div(
                attrs = { classes("shape-tools__body") }
            ) {
                ReorderSectionView(isVisible = true, actionManager::setOneTimeAction)
                IndicatorView(isVisible = !viewModel.hasAnyToolState.value)
            }
            FooterView()
        }
    }
}
