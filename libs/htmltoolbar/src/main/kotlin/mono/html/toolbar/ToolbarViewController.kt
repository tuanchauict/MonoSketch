package mono.html.toolbar

import kotlinx.browser.document
import kotlinx.html.dom.append
import mono.lifecycle.LifecycleOwner
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement

/**
 * A view controller to manage toolbar.
 */
class ToolbarViewController(
    lifecycleOwner: LifecycleOwner,
    toolbarContainer: HTMLDivElement,
    private val actionManager: ActionManager
) {
    init {
        val actionToInputMap = mapOf(
            initInput("tool_selection", RetainableActionType.IDLE),
            initInput("tool_add_rectangle", RetainableActionType.ADD_RECTANGLE),
            initInput("tool_add_text", RetainableActionType.ADD_TEXT),
            initInput("tool_add_line", RetainableActionType.ADD_LINE)
        )

        actionManager.retainableActionLiveData.observe(lifecycleOwner) {
            actionToInputMap[it]?.checked = true
        }

        toolbarContainer.append {
            RightToolbar { TODO("Export selected shapes") }
        }
    }

    private fun initInput(
        elementId: String,
        retainableActionType: RetainableActionType
    ): Pair<RetainableActionType, HTMLInputElement> {
        val input = document.getElementById(elementId) as HTMLInputElement
        input.onchange = {
            actionManager.setRetainableAction(retainableActionType)
        }
        return retainableActionType to input
    }
}
