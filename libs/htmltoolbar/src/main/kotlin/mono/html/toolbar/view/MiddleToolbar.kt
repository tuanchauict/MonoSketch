@file:Suppress("FunctionName")

package mono.html.toolbar.view

import mono.actionmanager.RetainableActionType
import mono.html.Div
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import org.w3c.dom.Element

/**
 * A function to create middle toolbars UI.
 */
internal fun Element.MiddleToolbar(
    lifecycleOwner: LifecycleOwner,
    retainableActionLiveData: LiveData<RetainableActionType>,
    setRetainableAction: (RetainableActionType) -> Unit
) {
    Div("toolbar middle") {
        MouseActionGroup(lifecycleOwner, retainableActionLiveData, setRetainableAction)
    }
}
