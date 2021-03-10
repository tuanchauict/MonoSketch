package mono.app

import mono.keycommand.KeyCommand
import mono.livedata.LiveData
import mono.livedata.MutableLiveData
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.KeyboardEvent

/**
 * A controller class to identify command by keyboard.
 */
class KeyCommandController(private val body: HTMLElement) {
    private val keyCommandMutableLiveData: MutableLiveData<KeyCommand> =
        MutableLiveData(KeyCommand.IDLE)
    val keyCommandLiveData: LiveData<KeyCommand> = keyCommandMutableLiveData

    init {
        body.onkeydown = ::updateCommand
    }

    private fun updateCommand(event: KeyboardEvent) {
        keyCommandMutableLiveData.value = if (event.target == body) {
            KeyCommand.getCommandByKey(event.keyCode)
        } else {
            KeyCommand.IDLE
        }
        console.log("Key press ${event.code} : ${event.keyCode}")
        keyCommandMutableLiveData.value = KeyCommand.IDLE
    }
}
