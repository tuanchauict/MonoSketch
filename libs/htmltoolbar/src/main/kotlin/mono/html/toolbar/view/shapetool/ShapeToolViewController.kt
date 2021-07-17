package mono.html.toolbar.view.shapetool

import kotlinx.html.dom.append
import org.w3c.dom.HTMLElement

class ShapeToolViewController(controller: HTMLElement) {
    init {
        controller.append {
            MoveSection()
            TransformSection(0, 0, 10, 10)
            AppearanceSection()
            TextSection()
        }
    }
}
