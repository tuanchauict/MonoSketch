package mono.html.toolbar

import mono.common.exhaustive
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
    keyCommandLiveData: LiveData<KeyCommand>
) {
    private val retainableActionMutableLiveData: MutableLiveData<RetainableActionType> =
        MutableLiveData(RetainableActionType.IDLE)
    val retainableActionLiveData: LiveData<RetainableActionType> = retainableActionMutableLiveData

    private val oneTimeActionMutableLiveData: MutableLiveData<OneTimeActionType> =
        MutableLiveData(OneTimeActionType.Idle)
    val oneTimeActionLiveData: LiveData<OneTimeActionType> = oneTimeActionMutableLiveData

    init {
        keyCommandLiveData.distinctUntilChange().observe(lifecycleOwner, listener = ::onKeyEvent)
    }

    private fun onKeyEvent(keyCommand: KeyCommand) {
        when (keyCommand) {
            KeyCommand.IDLE -> Unit
            KeyCommand.SELECT_ALL ->
                setOneTimeAction(OneTimeActionType.SelectAllShapes)
            KeyCommand.DESELECTION ->
                setOneTimeAction(OneTimeActionType.DeselectShapes)

            KeyCommand.DELETE ->
                setOneTimeAction(OneTimeActionType.DeleteSelectedShapes)

            KeyCommand.MOVE_LEFT ->
                setOneTimeAction(OneTimeActionType.MoveShapes(0, -1))
            KeyCommand.MOVE_UP ->
                setOneTimeAction(OneTimeActionType.MoveShapes(-1, 0))
            KeyCommand.MOVE_RIGHT ->
                setOneTimeAction(OneTimeActionType.MoveShapes(0, 1))
            KeyCommand.MOVE_DOWN ->
                setOneTimeAction(OneTimeActionType.MoveShapes(1, 0))

            KeyCommand.ADD_RECTANGLE ->
                setRetainableAction(RetainableActionType.ADD_RECTANGLE)
            KeyCommand.ADD_TEXT ->
                setRetainableAction(RetainableActionType.ADD_TEXT)
            KeyCommand.ADD_LINE ->
                setRetainableAction(RetainableActionType.ADD_LINE)

            KeyCommand.SELECTION_MODE ->
                setRetainableAction(RetainableActionType.IDLE)
            KeyCommand.ENTER_EDIT_MODE ->
                setOneTimeAction(OneTimeActionType.EditSelectedShapes)

            KeyCommand.COPY -> setOneTimeAction(OneTimeActionType.Copy(false))
            KeyCommand.CUT -> setOneTimeAction(OneTimeActionType.Copy(true))
            KeyCommand.DUPLICATE -> setOneTimeAction(OneTimeActionType.Duplicate)

            KeyCommand.COPY_TEXT -> setOneTimeAction(OneTimeActionType.CopyText)
        }.exhaustive
    }

    fun setRetainableAction(actionType: RetainableActionType) {
        retainableActionMutableLiveData.value = actionType
    }

    fun setOneTimeAction(actionType: OneTimeActionType) {
        oneTimeActionMutableLiveData.value = actionType
        oneTimeActionMutableLiveData.value = OneTimeActionType.Idle
    }
}
