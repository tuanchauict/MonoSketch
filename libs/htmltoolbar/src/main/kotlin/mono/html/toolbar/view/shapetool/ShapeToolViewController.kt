package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.toolbar.ActionManager
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.shape.shape.AbstractShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
import org.w3c.dom.HTMLElement

class ShapeToolViewController(
    lifecycleOwner: LifecycleOwner,
    controller: HTMLElement,
    actionManager: ActionManager,
    selectedShapesLiveData: LiveData<Set<AbstractShape>>,
) {
    init {
        controller.append {
            val moveTool = ReorderSection(actionManager::setOneTimeAction)
            val transformTool = TransformSection(actionManager::setOneTimeAction)

            AppearanceSection()
            TextSection()

            selectedShapesLiveData.observe(lifecycleOwner) {
                val singleShape = it.singleOrNull()
                val isSingle = singleShape != null
                val isSizeChangeable = singleShape is Rectangle || singleShape is Text

                moveTool.setEnabled(isSingle)

                transformTool.setEnabled(isSingle, isSizeChangeable)
                if (singleShape != null) {
                    transformTool.setValue(singleShape.bound)
                }
            }
        }
    }
}
