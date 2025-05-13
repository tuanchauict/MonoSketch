import type { PointF } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import { ScaleInteractionPoint } from "$mono/shape-interaction-bound/interaction-point";
import { ChangeBound } from "$mono/shape/command/general-shape-commands";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { updateConnectors } from "$mono/state-manager/utils/UpdateShapeBoundHelper";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import { type MousePointer, MousePointerType } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A [MouseCommand] for adding a new Line shape.
 */
export class ScaleShapeMouseCommand implements MouseCommand {
    mouseCursor?: MouseCursor = undefined;
    private readonly initialBound: Rect;

    constructor(private shape: AbstractShape, private interactionPoint: ScaleInteractionPoint.Base) {
        this.initialBound = shape.bound;
    }

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        switch (mousePointer.type) {
            case MousePointerType.DRAG:
                this.scale(environment, mousePointer.boardCoordinateF, false);
                break;
            case MousePointerType.UP:
                this.scale(environment, mousePointer.boardCoordinateF, true);
                break;
            case MousePointerType.DOWN:
            case MousePointerType.MOVE:
            case MousePointerType.CLICK:
            case MousePointerType.DOUBLE_CLICK:
            case MousePointerType.IDLE:
            // nothing to do
        }
        return mousePointer.type === MousePointerType.IDLE ? MouseCommand.CommandResultType.DONE : MouseCommand.CommandResultType.WORKING;
    }

    private scale(environment: CommandEnvironment, pointF: PointF, isUpdateConfirmed: boolean) {
        const newBound = this.interactionPoint.createNewShapeBound(this.initialBound, pointF);
        environment.executeShapeManagerCommand(new ChangeBound(this.shape, newBound));

        updateConnectors(environment, this.shape, newBound, isUpdateConfirmed);
        environment.updateInteractionBounds();
    }
}
