@file:Suppress("FunctionName")

package mono.html.toolbar.view

import kotlinx.html.TagConsumer
import kotlinx.html.div
import mono.html.toolbar.RetainableActionType
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.HTMLElement

/**
 * A function to create middle toolbars UI.
 */
internal fun TagConsumer<HTMLElement>.MiddleToolbar(
    lifecycleOwner: LifecycleOwner,
    retainableActionLiveData: LiveData<RetainableActionType>,
    setRetainableAction: (RetainableActionType) -> Unit
) {
    div("toolbar middle") {
        MouseActionGroup(lifecycleOwner, retainableActionLiveData, setRetainableAction)
    }
}
