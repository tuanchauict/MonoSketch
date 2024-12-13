/*
 * Copyright (c) 2024, tuanchauict
 */

import { LifecycleOwner } from '$libs/flow/lifecycleowner';
import { Flow } from '$libs/flow/flow';
import { KeyCommandType } from "$mono/keycommand";
import { RetainableActionType } from './retainable-actions';
import { type OneTimeActionType, OneTimeAction } from './one-time-actions';

/**
 * A class which gathers UI events and converts them into equivalent command.
 */
export class ActionManager {
    private retainableActionFlow: Flow<RetainableActionType> = new Flow(RetainableActionType.IDLE);
    private oneTimeActionFlow: Flow<OneTimeActionType> = new Flow(OneTimeAction.Idle);

    constructor(lifecycleOwner: LifecycleOwner, keyCommandFlow: Flow<KeyCommandType>) {
        keyCommandFlow.distinctUntilChanged().observe(lifecycleOwner, this.onKeyEvent.bind(this));
    }

    private onKeyEvent(keyCommand: KeyCommandType) {
        switch (keyCommand) {
            case KeyCommandType.IDLE:
                break;
            case KeyCommandType.SELECT_ALL:
                this.setOneTimeAction(OneTimeAction.SelectAllShapes);
                break;
            case KeyCommandType.DESELECTION:
                this.setOneTimeAction(OneTimeAction.DeselectShapes);
                break;
            case KeyCommandType.DELETE:
                this.setOneTimeAction(OneTimeAction.DeleteSelectedShapes);
                break;
            case KeyCommandType.MOVE_LEFT:
                this.setOneTimeAction(OneTimeAction.MoveShapes(0, -1));
                break;
            case KeyCommandType.MOVE_UP:
                this.setOneTimeAction(OneTimeAction.MoveShapes(-1, 0));
                break;
            case KeyCommandType.MOVE_RIGHT:
                this.setOneTimeAction(OneTimeAction.MoveShapes(0, 1));
                break;
            case KeyCommandType.MOVE_DOWN:
                this.setOneTimeAction(OneTimeAction.MoveShapes(1, 0));
                break;
            case KeyCommandType.FAST_MOVE_LEFT:
                this.setOneTimeAction(OneTimeAction.MoveShapes(0, -5));
                break;
            case KeyCommandType.FAST_MOVE_UP:
                this.setOneTimeAction(OneTimeAction.MoveShapes(-5, 0));
                break;
            case KeyCommandType.FAST_MOVE_RIGHT:
                this.setOneTimeAction(OneTimeAction.MoveShapes(0, 5));
                break;
            case KeyCommandType.FAST_MOVE_DOWN:
                this.setOneTimeAction(OneTimeAction.MoveShapes(5, 0));
                break;
            case KeyCommandType.ADD_RECTANGLE:
                this.setRetainableAction(RetainableActionType.ADD_RECTANGLE);
                break;
            case KeyCommandType.ADD_TEXT:
                this.setRetainableAction(RetainableActionType.ADD_TEXT);
                break;
            case KeyCommandType.ADD_LINE:
                this.setRetainableAction(RetainableActionType.ADD_LINE);
                break;
            case KeyCommandType.SELECTION_MODE:
                this.setRetainableAction(RetainableActionType.IDLE);
                break;
            case KeyCommandType.ENTER_EDIT_MODE:
                this.setOneTimeAction(OneTimeAction.EditSelectedShapes);
                break;
            case KeyCommandType.COPY:
                this.setOneTimeAction(OneTimeAction.Copy(false));
                break;
            case KeyCommandType.CUT:
                this.setOneTimeAction(OneTimeAction.Copy(true));
                break;
            case KeyCommandType.DUPLICATE:
                this.setOneTimeAction(OneTimeAction.Duplicate);
                break;
            case KeyCommandType.COPY_TEXT:
                this.setOneTimeAction(OneTimeAction.CopyText);
                break;
            case KeyCommandType.UNDO:
                this.setOneTimeAction(OneTimeAction.Undo);
                break;
            case KeyCommandType.REDO:
                this.setOneTimeAction(OneTimeAction.Redo);
                break;
            case KeyCommandType.SHIFT_KEY:
                break;
            default:
                break;
        }
    }

    setRetainableAction(actionType: RetainableActionType) {
        this.retainableActionFlow.value = actionType;
    }

    setOneTimeAction(actionType: OneTimeActionType) {
        this.oneTimeActionFlow.value = actionType;
        this.oneTimeActionFlow.value = OneTimeAction.Idle;
    }

    installDebugCommand() {
        // TODO: Implement this function
        // if (!process.env.DEBUG) {
        //     return;
        // }
        // const debugCommand = new DebugCommandController(this.setOneTimeAction.bind(this));
        // (window as any).cmd = debugCommand.executeCommand.bind(debugCommand);
    }
}
