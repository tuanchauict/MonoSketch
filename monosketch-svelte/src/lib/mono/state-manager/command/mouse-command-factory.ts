import { RetainableActionType } from "$mono/action-manager/retainable-actions";
import { type FocusingShape, ShapeFocusType } from "$mono/shape/selected-shape-manager";
import { Line } from "$mono/shape/shape/line";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { AddLineMouseCommand } from "$mono/state-manager/command/mouse/add-line-mouse-command";
import { AddTextMouseCommand } from "$mono/state-manager/command/mouse/add-text-mouse-command";
import { LineInteractionMouseCommand } from "$mono/state-manager/command/mouse/line-interaction-mouse-command";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { MoveShapeMouseCommand } from "$mono/state-manager/command/mouse/move-shape-mouse-command";
import { ScaleShapeMouseCommand } from "$mono/state-manager/command/mouse/scale-shape-mouse-command";
import { SelectShapeMouseCommand } from "$mono/state-manager/command/mouse/select-shape-mouse-command";
import {
    InteractionPoint,
    LineInteractionPoint,
    ScaleInteractionPoint,
} from "$mono/shape-interaction-bound/interaction-point";
import { MousePointer, type MousePointerDown, MousePointerType } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A factory of [MouseCommand].
 */
export class MouseCommandFactory {
    static getCommand(
        commandEnvironment: CommandEnvironment,
        mousePointer: MousePointer,
        commandType: RetainableActionType
    ): MouseCommand | null {
        switch (mousePointer.type) {
            case MousePointerType.DOWN:
                return this.detectMouseCommandWithMouseDown(commandEnvironment, mousePointer, commandType);
            case MousePointerType.CLICK:
                return commandType === RetainableActionType.IDLE ? SelectShapeMouseCommand : null;
            case MousePointerType.DOUBLE_CLICK:
            case MousePointerType.MOVE:
            case MousePointerType.DRAG:
            case MousePointerType.UP:
            case MousePointerType.IDLE:
                return null;
        }
    }

    private static detectMouseCommandWithMouseDown(
        environment: CommandEnvironment,
        mousePointer: MousePointerDown,
        commandType: RetainableActionType
    ): MouseCommand {
        const interactionCommand = this.detectInteractionCommand(environment, mousePointer);
        if (interactionCommand) {
            return interactionCommand;
        }

        switch (commandType) {
            case RetainableActionType.ADD_RECTANGLE:
                return new AddTextMouseCommand(false);
            case RetainableActionType.ADD_TEXT:
                return new AddTextMouseCommand(true);
            case RetainableActionType.ADD_LINE:
                return new AddLineMouseCommand();
            case RetainableActionType.IDLE:
                return SelectShapeMouseCommand;
        }
    }

    private static detectInteractionCommand(
        environment: CommandEnvironment,
        mousePointer: MousePointerDown
    ): MouseCommand | null {
        const focusingShape = this.getHoveringFocusingShape(environment);

        if (!mousePointer.isWithShiftKey && focusingShape && !environment.getSelectedShapes().has(focusingShape.shape)) {
            environment.clearSelectedShapes();
            environment.addSelectedShape(focusingShape.shape);
        }

        const selectedShapes = Array.from(environment.getSelectedShapes());
        if (selectedShapes.length === 0) {
            return null;
        }

        const sharpBoundInteractionCommand = this.createShapeBoundInteractionMouseCommandIfValid(
            environment,
            environment.getInteractionPoint(mousePointer.clientCoordinate)
        );
        if (sharpBoundInteractionCommand) {
            return sharpBoundInteractionCommand;
        }

        if (!mousePointer.isWithShiftKey && environment.isPointInInteractionBounds(mousePointer.boardCoordinate)) {
            const relatedShapes = Array.from(selectedShapes)
                .flatMap(shape => environment.shapeManager.shapeConnector.getConnectors(shape))
                .map(connector => environment.shapeManager.getShape(connector.lineId))
                .filter(shape => shape !== undefined);
            return new MoveShapeMouseCommand(selectedShapes, relatedShapes);
        }
        return null;
    }

    private static getHoveringFocusingShape(environment: CommandEnvironment): FocusingShape | null {
        const focusingShape = environment.getFocusingShape();
        if (focusingShape?.focusType !== ShapeFocusType.SELECT_MODE_HOVER) {
            return null;
        }
        return focusingShape;
    }

    private static createShapeBoundInteractionMouseCommandIfValid(
        commandEnvironment: CommandEnvironment,
        interactionPoint: InteractionPoint | null
    ): MouseCommand | null {
        if (!interactionPoint) {
            return null;
        }
        const shape = commandEnvironment.shapeManager.getShape(interactionPoint.shapeId);
        if (!shape) {
            return null;
        }
        if (interactionPoint instanceof ScaleInteractionPoint.Base) {
            return new ScaleShapeMouseCommand(shape, interactionPoint);
        }
        if (interactionPoint instanceof LineInteractionPoint.Base && shape instanceof Line) {
            return new LineInteractionMouseCommand(shape, interactionPoint);
        }
        return null;
    }
}
