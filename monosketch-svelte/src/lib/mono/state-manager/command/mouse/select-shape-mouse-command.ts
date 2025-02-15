import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import  {  MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import type { MousePointer } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A [MouseCommand] to select shapes.
 */
class SelectShapeMouseCommandImpl implements MouseCommand {
    mouseCursor: MouseCursor = MouseCursor.DEFAULT;

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        throw new Error("Method not implemented.");
    }

}

export const SelectShapeMouseCommand = new SelectShapeMouseCommandImpl();
