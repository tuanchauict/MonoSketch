package mono.app

import mono.common.Key
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.KeyboardEvent

/**
 * A controller class to identify command by keyboard.
 */
class KeyCommandController(private val body: HTMLElement) {
    private val keyCommandMutableLiveData: MutableLiveData<Key.KeyCommand> =
        MutableLiveData(Key.KeyCommand.IDLE)
    val keyCommandLiveData: LiveData<Key.KeyCommand> = keyCommandMutableLiveData

    init {
        body.onkeydown = ::updateCommand
    }

    private fun updateCommand(event: KeyboardEvent) {
        keyCommandMutableLiveData.value = if (event.target == body) {
            Key.getCommandByKey(event.keyCode)
        } else {
            Key.KeyCommand.IDLE
        }
        console.log("Key press ${event.code} : ${event.keyCode}")
        keyCommandMutableLiveData.value = Key.KeyCommand.IDLE
    }
}
