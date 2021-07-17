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
    private val moveSection: SectionViewController

    init {
        var moveSection: SectionViewController? = null
        controller.append {
            moveSection = MoveSection(actionManager::setOneTimeAction)

            TransformSection(0, 0, 10, 10)
            AppearanceSection()
            TextSection()
        }
        this.moveSection = moveSection!!

        selectedShapesLiveData.observe(lifecycleOwner) {
            this.moveSection.setEnabled(it.isNotEmpty())
        }
    }
}
