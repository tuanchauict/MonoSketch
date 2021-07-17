package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.toolbar.ActionManager
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MediatorLiveData
import mono.shape.shape.AbstractShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text
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

            AppearanceSection()
            TextSection()

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
            }
        }
    }
}
