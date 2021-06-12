package mono.html.toolbar

import mono.keycommand.KeyCommand
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange

/**
 * A class which gathers UI events and converts them into equivalent command.
 */
class ActionManager(
    lifecycleOwner: LifecycleOwner,
    keyCommandLiveData: LiveData<KeyCommand>,
    private val hasAnySelectedShapes: () -> Boolean
) {
    private val retainableActionMutableLiveData: MutableLiveData<RetainableActionType> =
        MutableLiveData(RetainableActionType.IDLE)
    val retainableActionLiveData: LiveData<RetainableActionType> = retainableActionMutableLiveData

    private val oneTimeActionMutableLiveData: MutableLiveData<OneTimeActionType> =
        MutableLiveData(OneTimeActionType.IDLE)
    val oneTimeActionLiveData: LiveData<OneTimeActionType> = oneTimeActionMutableLiveData

    init {
        keyCommandLiveData.distinctUntilChange().observe(lifecycleOwner, listener = ::onKeyEvent)
    }

    private fun onKeyEvent(keyCommand: KeyCommand) {
        when (keyCommand) {
            KeyCommand.IDLE -> Unit
            KeyCommand.ESC ->
                if (hasAnySelectedShapes()) {
                    setOneTimeAction(OneTimeActionType.DESELECT_SHAPES)
                } else {
                    setRetainableAction(RetainableActionType.IDLE)
                }
            KeyCommand.DELETE ->
                setOneTimeAction(OneTimeActionType.DELETE_SELECTED_SHAPES)
            KeyCommand.MOVE_LEFT ->
                setOneTimeAction(OneTimeActionType.MOVE_SELECTED_SHAPES_LEFT)
            KeyCommand.MOVE_UP ->
                setOneTimeAction(OneTimeActionType.MOVE_SELECTED_SHAPES_UP)
            KeyCommand.MOVE_RIGHT ->
                setOneTimeAction(OneTimeActionType.MOVE_SELECTED_SHAPES_RIGHT)
            KeyCommand.MOVE_DOWN ->
                setOneTimeAction(OneTimeActionType.MOVE_SELECTED_SHAPES_DOWN)
            KeyCommand.ADD_RECTANGLE ->
                setRetainableAction(RetainableActionType.ADD_RECTANGLE)
            KeyCommand.ADD_TEXT ->
                setRetainableAction(RetainableActionType.ADD_TEXT)
            KeyCommand.ADD_LINE ->
                setRetainableAction(RetainableActionType.ADD_LINE)
            KeyCommand.ENTER_EDIT_MODE ->
                setOneTimeAction(OneTimeActionType.EDIT_SELECTED_SHAPES)
        }
    }

    fun setRetainableAction(actionType: RetainableActionType) {
        retainableActionMutableLiveData.value = actionType
    }

    fun setOneTimeAction(actionType: OneTimeActionType) {
        oneTimeActionMutableLiveData.value = actionType
        oneTimeActionMutableLiveData.value = OneTimeActionType.IDLE
    }
}
