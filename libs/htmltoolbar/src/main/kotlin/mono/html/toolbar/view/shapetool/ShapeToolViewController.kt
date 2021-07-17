package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.toolbar.ActionManager
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.shape.AbstractShape
import org.w3c.dom.HTMLElement

class ShapeToolViewController(
    lifecycleOwner: LifecycleOwner,
    controller: HTMLElement,
    actionManager: ActionManager,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>
) {
    init {
        controller.append {
            val moveTool = ReorderSection(actionManager::setOneTimeAction)

            TransformSection(0, 0, 10, 10)
            AppearanceSection()
            TextSection()

            selectedShapesLiveData.observe(lifecycleOwner) {
                moveTool.setEnabled(it.size == 1)
            }
        }
    }
}
