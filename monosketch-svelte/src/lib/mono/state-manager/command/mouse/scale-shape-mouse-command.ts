import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { ScaleInteractionPoint } from "$mono/shape-interaction-bound/interaction-point";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import type { MousePointer } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A [MouseCommand] for adding a new Line shape.
 */
export class ScaleShapeMouseCommand implements MouseCommand {
    mouseCursor?: MouseCursor = undefined;

    constructor(private shape: AbstractShape, private interactionPoint: ScaleInteractionPoint.Base) {
    }

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        throw new Error("Method not implemented.");
    }
}
