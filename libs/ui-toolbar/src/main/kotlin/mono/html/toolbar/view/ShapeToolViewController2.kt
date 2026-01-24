/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.html.toolbar.view

import mono.actionmanager.ActionManager
import mono.html.toolbar.view.shapetool2.ShapeToolContentView
import mono.html.toolbar.view.shapetool2.ShapeToolViewModel
import mono.html.toolbar.view.utils.CssClass
import mono.html.toolbar.view.utils.bindClass
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.shape.AbstractShape
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
        actionManager
    )

    init {
        shapeToolVisibilityLiveData.observe(lifecycleOwner) {
            container.bindClass(CssClass.HIDE, !it)
        }
        renderComposable(container) {
            ShapeToolContentView(viewModel, actionManager::setOneTimeAction)
        }
    }
}
