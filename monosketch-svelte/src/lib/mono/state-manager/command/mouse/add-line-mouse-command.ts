import { DirectedPoint, Direction, Point } from "$libs/graphics-geo/point";
import { MoveLineAnchor } from "$mono/shape/command/line-commands";
import { ShapeConnectorUseCase } from "$mono/shape/connector/line-connector";
import { ShapeFocusType } from "$mono/shape/selected-shape-manager";
import { Line } from "$mono/shape/shape/line";
import { LineAnchor } from "$mono/shape/shape/linehelper";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { HoverShapeManager } from "$mono/state-manager/command/mouse/hover-shape-manager";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import { type MousePointer, type MousePointerDown, MousePointerType } from "$mono/workspace/mouse/mouse-pointer";
import CommandResultType = MouseCommand.CommandResultType;

/**
 * A [MouseCommand] for adding a new Line shape.
 */
export class AddLineMouseCommand implements MouseCommand {
    mouseCursor: MouseCursor = MouseCursor.CROSSHAIR;

    private workingShape: Line | null = null;
    private readonly hoverShapeManager: HoverShapeManager = HoverShapeManager.forLineConnectHover();

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        switch (mousePointer.type) {
            case MousePointerType.DOWN: {
                this.workingShape = this.createLineAndAdjustStartAnchor(environment, mousePointer);
                environment.clearSelectedShapes();
                return CommandResultType.WORKING;
            }
            case MousePointerType.DRAG: {
                this.changeEndAnchor(environment, mousePointer.boardCoordinate, mousePointer.isWithShiftKey, false);
                return CommandResultType.WORKING;
            }
            case MousePointerType.UP: {
                this.changeEndAnchor(environment, mousePointer.boardCoordinate, mousePointer.isWithShiftKey, true);
                environment.addSelectedShape(this.workingShape);
                return CommandResultType.DONE;
            }
            case MousePointerType.MOVE:
            case MousePointerType.CLICK:
            case MousePointerType.DOUBLE_CLICK:
            case MousePointerType.IDLE:
                return CommandResultType.UNKNOWN;
        }
    }

    private createLineAndAdjustStartAnchor(
        environment: CommandEnvironment,
        mousePointer: MousePointerDown,
    ): Line {
        const edgeDirection = environment.getEdgeDirection(mousePointer.boardCoordinate);
        const direction = edgeDirection !== null ? Direction.normalizedDirection(edgeDirection) : Direction.HORIZONTAL;

        const line = Line.fromPoints(
            {
                startPoint: new DirectedPoint(direction, mousePointer.boardCoordinate),
                endPoint: new DirectedPoint(Direction.VERTICAL, mousePointer.boardCoordinate),
                parentId: environment.workingParentGroup.id,
            },
        );

        environment.addShape(line);

        const connectShape = ShapeConnectorUseCase.getConnectableShape(
            line.startPoint.point,
            environment.getShapes(mousePointer.boardCoordinate),
        );

        if (connectShape !== null) {
            environment.shapeManager.shapeConnector.addConnector(line, LineAnchor.START, connectShape);
        }
        return line;
    }

    private changeEndAnchor(
        environment: CommandEnvironment,
        point: Point,
        isStraightLineMode: boolean,
        isUpdateConfirmed: boolean,
    ): void {
        const line = this.workingShape;
        if (!line) return;

        const endPoint = this.adjustEndPoint(line.startPoint.point, point, isStraightLineMode);
        const edgeDirection = environment.getEdgeDirection(point);
        const direction =
            edgeDirection !== null ? Direction.normalizedDirection(edgeDirection) : line.getDirection(LineAnchor.END);

        const anchorPointUpdate = {
            anchor: LineAnchor.END,
            point: new DirectedPoint(direction, endPoint),
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
                line,
                anchorPointUpdate,
                isUpdateConfirmed,
                false, // justMoveAnchor
                connectShape,
            ),
        );
    }

    private adjustEndPoint(
        startPoint: Point,
        candidateEndPoint: Point,
        isStraightLineMode: boolean,
    ): Point {
        if (!isStraightLineMode) {
            return candidateEndPoint;
        }

        const width = Math.abs(startPoint.left - candidateEndPoint.left);
        const height = Math.abs(startPoint.top - candidateEndPoint.top);

        if (width > height) {
            return new Point(candidateEndPoint.left, startPoint.top);
        } else {
            return new Point(startPoint.left, candidateEndPoint.top);
        }
    }
}
