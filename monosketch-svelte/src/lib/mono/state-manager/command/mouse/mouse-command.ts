import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import type { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import type { MousePointer } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A strategy interface for mouse interaction command happens on [MainStateManager]
 */
export interface MouseCommand {
    /**
     * CSS mouse cursor value to show during command execution.
     */
    mouseCursor?: MouseCursor;

    execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer,
    ): MouseCommand.CommandResultType;
}


export namespace MouseCommand {
    export enum CommandResultType {
        WORKING,
        WORKING_PHASE2,
        DONE,
        UNKNOWN
    }
}
