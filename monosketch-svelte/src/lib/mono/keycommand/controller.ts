import { Flow } from '../../libs/flow';
import { type KeyCommand, KeyCommandType } from './interface';
import { getCommandByType, getCommandByKey } from './keycommands';
import { DEBUG_MODE } from '../build_environment';
import { isCommandKeyOn } from '../common/platform';

/**
 * A controller that emits key commands when a key is pressed.
 */
export class KeyCommandController {
    private _keyCommandMutableFlow = new Flow<KeyCommand>();
    keyCommandFlow: Flow<KeyCommand> = this._keyCommandMutableFlow.distinctUntilChanged();

    constructor(private body: HTMLElement) {
        body.onkeydown = this.updateKeyCommand;
        body.onkeyup = this.resetKeyCommand;
    }

    private updateKeyCommand = (e: KeyboardEvent) => {
        // TODO: Resolve keyCode deprecated.
        const keyCommand =
            e.target === this.body
                ? getCommandByKey(e.keyCode, isCommandKeyOn(e), e.shiftKey)
                : getCommandByType(KeyCommandType.IDLE);

        if (!keyCommand.isKeyEventPropagationAllowed) {
            e.stopPropagation();
            e.preventDefault();
        }
        if (DEBUG_MODE) {
            console.log(`Key command: ${keyCommand.command}`);
        }
        this._keyCommandMutableFlow.value = keyCommand;
        if (keyCommand.isRepeatable) {
            this._keyCommandMutableFlow.value = getCommandByType(KeyCommandType.IDLE);
        }
    };

    private resetKeyCommand = () => {
        this._keyCommandMutableFlow.value = getCommandByType(KeyCommandType.IDLE);
    };
}
