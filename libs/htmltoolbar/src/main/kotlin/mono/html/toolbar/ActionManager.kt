package mono.html.toolbar

import mono.keycommand.KeyCommand
import mono.lifecycle.LifecycleOwner
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import mono.livedata.distinctUntilChange

/**
 * A class which gathers UI events and converts them into equivalent command.
 */
class ActionManager(lifecycleOwner: LifecycleOwner, keyCommandLiveData: LiveData<KeyCommand>) {
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
        TODO("Update actions")
    }
}
