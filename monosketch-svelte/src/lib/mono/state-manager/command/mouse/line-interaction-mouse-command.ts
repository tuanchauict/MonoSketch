import { DirectedPoint, Direction, type Point } from "$libs/graphics-geo/point";
import { LineInteractionPoint } from "$mono/shape-interaction-bound/interaction-point";
import { MoveLineAnchor, MoveLineEdge } from "$mono/shape/command/line-commands";
import { ShapeFocusType } from "$mono/shape/selected-shape-manager";
import type { Line } from "$mono/shape/shape/line";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { HoverShapeManager } from "$mono/state-manager/command/mouse/hover-shape-manager";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import { type MousePointer, MousePointerType } from "$mono/workspace/mouse/mouse-pointer";
import CommandResultType = MouseCommand.CommandResultType;

/**
 * A [MouseCommand] for adding a new Line shape.
 */
export class LineInteractionMouseCommand implements MouseCommand {
    mouseCursor?: MouseCursor;
    private readonly hoverShapeManager: HoverShapeManager = HoverShapeManager.forLineConnectHover();


    constructor(private shape: Line, private interactionPoint: LineInteractionPoint.Base) {
        this.mouseCursor = interactionPoint instanceof LineInteractionPoint.Anchor
            ? MouseCursor.CROSSHAIR
            : undefined;
    }

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        switch (mousePointer.type) {
            case MousePointerType.DRAG:
                this.move(environment, mousePointer.boardCoordinate, false, !mousePointer.isWithShiftKey);
                break
            case MousePointerType.UP:
                this.move(environment, mousePointer.boardCoordinate, true, !mousePointer.isWithShiftKey);
                break
        }

        return mousePointer.type === MousePointerType.IDLE ? CommandResultType.DONE : CommandResultType.WORKING;
    }

    private move(
        environment: CommandEnvironment,
        point: Point,
        isUpdateConfirmed: boolean,
        justMoveAnchor: boolean,
    ): void {
        if (this.interactionPoint instanceof LineInteractionPoint.Anchor) {
            this.moveAnchor(
                environment,
                this.interactionPoint,
                point,
                isUpdateConfirmed,
                justMoveAnchor,
            );
        } else if (this.interactionPoint instanceof LineInteractionPoint.Edge) {
            this.moveEdge(
                environment,
                this.interactionPoint,
                point,
                isUpdateConfirmed,
            );
        }
    }

    private moveAnchor(
        environment: CommandEnvironment,
        interactionPoint: LineInteractionPoint.Anchor,
        point: Point,
        isUpdateConfirmed: boolean,
        justMoveAnchor: boolean,
    ): void {
        const edgeDirection = environment.getEdgeDirection(point);
        const direction = edgeDirection ? Direction.normalizedDirection(edgeDirection) :
            this.shape.getDirection(interactionPoint.anchor);

        const anchorPointUpdate = {
            anchor: interactionPoint.anchor,
            point: new DirectedPoint(direction, point),
        };

        const connectShape = this.hoverShapeManager.getHoverShape(
            environment,
            anchorPointUpdate.point.point,
        );

        environment.setFocusingShape(
            isUpdateConfirmed ? null : connectShape,
            ShapeFocusType.LINE_CONNECTING,
        );

        environment.shapeManager.execute(
            new MoveLineAnchor(
                this.shape,
                anchorPointUpdate,
                isUpdateConfirmed,
                justMoveAnchor,
                connectShape,
            ),
        );

        environment.updateInteractionBounds();
    }

    private moveEdge(
        environment: CommandEnvironment,
        interactionPoint: LineInteractionPoint.Edge,
        point: Point,
        isUpdateConfirmed: boolean,
    ): void {
        environment.shapeManager.execute(
            new MoveLineEdge(
                this.shape,
                interactionPoint.edgeId,
                point,
                isUpdateConfirmed,
            ),
        );

        environment.updateInteractionBounds();
    }
}
