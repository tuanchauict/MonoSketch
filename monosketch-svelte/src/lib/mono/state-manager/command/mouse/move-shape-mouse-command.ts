import { Point } from "$libs/graphics-geo/point";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { moveShapes } from "$mono/state-manager/utils/UpdateShapeBoundHelper";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import { type MousePointer, MousePointerType } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A [MouseCommand] for moving selected shapes.
 */
export class MoveShapeMouseCommand implements MouseCommand {
    mouseCursor: MouseCursor = MouseCursor.MOVE;

    private readonly initialPositions: Map<string, Point> = new Map();

    constructor(
        private shapes: AbstractShape[],
        relatedShapes: AbstractShape[]
    ) {
        // Add positions from shapes
        for (const shape of shapes) {
            this.initialPositions.set(shape.id, shape.bound.position);
        }

        // Add positions from relatedShapes
        for (const shape of relatedShapes) {
            this.initialPositions.set(shape.id, shape.bound.position);
        }
    }

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        const offset = this.getOffset(mousePointer);
        const mouseType = mousePointer.type;

        moveShapes(environment, this.shapes, mouseType === MousePointerType.UP, shape => {
            const initialPosition = this.initialPositions.get(shape.id);
            return initialPosition ? initialPosition.plus(offset) : null;
        });
        environment.updateInteractionBounds();

        const isDone = mouseType === MousePointerType.UP || mouseType === MousePointerType.IDLE;
        return isDone ? MouseCommand.CommandResultType.DONE : MouseCommand.CommandResultType.WORKING;
    }

    private getOffset(mousePointer: MousePointer): Point {
        switch (mousePointer.type) {
            case MousePointerType.UP:
                return mousePointer.boardCoordinate.minus(mousePointer.mouseDownBoardCoordinate);
            case MousePointerType.DRAG:
                return mousePointer.boardCoordinate.minus(mousePointer.mouseDownBoardCoordinate);
            case MousePointerType.IDLE:
            case MousePointerType.MOVE:
            case MousePointerType.DOWN:
            case MousePointerType.CLICK:
            case MousePointerType.DOUBLE_CLICK:
                return Point.ZERO;
        }
    }
}
