import type { ActionManager } from "$mono/action-manager/action-manager";
import { OneTimeAction } from "$mono/action-manager/one-time-actions";
import { RetainableActionType } from "$mono/action-manager/retainable-actions";
import { ShapeFocusType } from "$mono/shape/selected-shape-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { MouseCommandFactory } from "$mono/state-manager/command/mouse-command-factory";
import { HoverShapeManager } from "$mono/state-manager/command/mouse/hover-shape-manager";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { MousePointer, MousePointerType } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A controller class specific for mouse interaction.
 */
export class MouseInteractionController {
    private currentMouseCommand: MouseCommand | null = null;
    private lineConnectHoverShapeManager = HoverShapeManager.forLineConnectHover();
    private hoverShapeManager = HoverShapeManager.forHoverShape();

    constructor(
        private environment: CommandEnvironment,
        private actionManager: ActionManager,
        private requestRedraw: () => void,
    ) {
    }

    get currentRetainableActionType(): RetainableActionType {
        return this.actionManager.retainableActionFlow.value!;
    }

    reset(): void {
        this.lineConnectHoverShapeManager.resetCache();
        this.hoverShapeManager.resetCache();
    }

    onMouseEvent(mousePointer: MousePointer): void {
        if (mousePointer.type === MousePointerType.DOWN || mousePointer.type === MousePointerType.UP) {
            this.reset();
        }
        if (mousePointer.type === MousePointerType.DOUBLE_CLICK) {
            const targetedShape = this.findTargetedShape(mousePointer);
            this.actionManager.setOneTimeAction(OneTimeAction.EditSelectedShape(targetedShape));
            return;
        }

        if (this.currentMouseCommand === null) {
            this.detectHoverShape(mousePointer);
        }

        const mouseCommand =
            MouseCommandFactory.getCommand(this.environment, mousePointer, this.currentRetainableActionType)
            || this.currentMouseCommand;

        if (!mouseCommand) {
            return;
        }

        this.currentMouseCommand = mouseCommand;

        this.environment.enterEditingMode();
        const commandResultType = mouseCommand.execute(this.environment, mousePointer);

        if (commandResultType === MouseCommand.CommandResultType.DONE) {
            this.environment.exitEditingMode(true);
        }

        if (commandResultType === MouseCommand.CommandResultType.DONE ||
            commandResultType === MouseCommand.CommandResultType.WORKING_PHASE2) {
            this.lineConnectHoverShapeManager.resetCache();
            this.currentMouseCommand = null;
            this.requestRedraw();
            setTimeout(() => this.actionManager.setRetainableAction(RetainableActionType.IDLE), 0);
        }
    }

    private detectHoverShape(mousePointer: MousePointer): void {
        const [hoverShape, type] = (() => {
            switch (this.currentRetainableActionType) {
                case RetainableActionType.ADD_LINE: {
                    const hoverShape = this.lineConnectHoverShapeManager.getHoverShape(this.environment, mousePointer.boardCoordinate);
                    return [hoverShape, ShapeFocusType.LINE_CONNECTING];
                }
                case RetainableActionType.IDLE: {
                    const hoverShape = !this.isOnInteractionPoint(mousePointer)
                        ? this.hoverShapeManager.getHoverShape(this.environment, mousePointer.boardCoordinate)
                        : null;
                    return [hoverShape, ShapeFocusType.SELECT_MODE_HOVER];
                }
                case RetainableActionType.ADD_RECTANGLE:
                case RetainableActionType.ADD_TEXT:
                    return [null, null];
            }
        })();

        if (!hoverShape) {
            return;
        }

        this.environment.setFocusingShape(hoverShape, type);
        this.requestRedraw();
    }

    private findTargetedShape(mousePointer: MousePointer): AbstractShape | null {
        const selectedShapes = this.environment.getSelectedShapes();
        return Array.from(selectedShapes).find(shape => shape.contains(mousePointer.boardCoordinate)) ?? null;
    }

    private isOnInteractionPoint(mousePointer: MousePointer): boolean {
        switch (mousePointer.type) {
            case MousePointerType.DOWN: {
                const pointPx = mousePointer.clientCoordinate;
                return this.environment.getInteractionPoint(pointPx) !== null;
            }
            case MousePointerType.MOVE: {
                const pointPx = mousePointer.clientCoordinate;
                return this.environment.getInteractionPoint(pointPx) !== null;
            }
            default:
                return false;
        }
    }
}
