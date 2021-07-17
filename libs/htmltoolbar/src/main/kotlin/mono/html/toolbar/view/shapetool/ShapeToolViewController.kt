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
            val transformTool = TransformSection(0, 0, 10, 10)

            AppearanceSection()
            TextSection()

            selectedShapesLiveData.observe(lifecycleOwner) {
                val singleShape = it.singleOrNull()
                val isSingle = singleShape != null

                moveTool.setEnabled(isSingle)
                transformTool.setEnabled(isSingle)
                if (singleShape != null) {
                    transformTool.setValue(singleShape.bound)
                }
            }
        }
    }
}
