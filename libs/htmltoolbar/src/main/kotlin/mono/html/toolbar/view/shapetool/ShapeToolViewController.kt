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
    private val moveTool: ToolViewController

    init {
        var moveTool: ToolViewController? = null
        controller.append {
            moveTool = MoveSection(actionManager::setOneTimeAction)

            TransformSection(0, 0, 10, 10)
            AppearanceSection()
            TextSection()
        }
        this.moveTool = moveTool!!

        selectedShapesLiveData.observe(lifecycleOwner) {
            this.moveTool.setEnabled(it.isNotEmpty())
        }
    }
}
