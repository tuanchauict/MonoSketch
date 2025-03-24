import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import type { MousePointer } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A [MouseCommand] for moving selected shapes.
 */
export class MoveShapeMouseCommand implements MouseCommand {
    mouseCursor: MouseCursor = MouseCursor.MOVE;

    constructor(
        private shapes: Set<AbstractShape>,
        relatedShapes: AbstractShape[]
    ) {
    }

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        throw new Error("Method not implemented.");
    }
}
