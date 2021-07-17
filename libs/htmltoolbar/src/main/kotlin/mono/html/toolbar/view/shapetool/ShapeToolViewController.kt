package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import mono.html.toolbar.ActionManager
import org.w3c.dom.HTMLElement

class ShapeToolViewController(
    controller: HTMLElement,
    actionManager: ActionManager,
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
    }
}
