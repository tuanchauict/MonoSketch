import type { Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { Size } from "$libs/graphics-geo/size";
import { ChangeBound, ChangeExtra } from "$mono/shape/command/general-shape-commands";
import { UpdateTextEditingMode } from "$mono/shape/command/text-commands";
import { TextExtra } from "$mono/shape/extra/shape-extra";
import { TextHorizontalAlign, TextVerticalAlign } from "$mono/shape/extra/style";
import { Text } from "$mono/shape/shape/text";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { EditTextShapeHelper } from "$mono/state-manager/command/text/edit-text-shape-helper";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import { type MousePointer, MousePointerType, type MousePointerUp } from "$mono/workspace/mouse/mouse-pointer";
import { MouseCommand } from "./mouse-command";

/**
 * A [MouseCommand] to add new text shape.
 * This command does two jobs:
 * 1. Identify the initial bound for the text shape
 * 2. Open a modal for entering text content when mouse up.
 */
export class AddTextMouseCommand implements MouseCommand {
    mouseCursor: MouseCursor = MouseCursor.CROSSHAIR;

    private workingShape: Text | null = null;

    constructor(private isTextEditable: boolean) {
    }

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        switch (mousePointer.type) {
            case MousePointerType.DOWN:
                const shape = Text.fromPoints(
                    mousePointer.boardCoordinate,
                    mousePointer.boardCoordinate,
                    null,
                    environment.workingParentGroup.id,
                    this.isTextEditable,
                );
                this.workingShape = shape;
                environment.addShape(shape);
                environment.clearSelectedShapes();
                return MouseCommand.CommandResultType.WORKING;

            case MousePointerType.DRAG:
                this.changeShapeBound(environment, mousePointer.mouseDownBoardCoordinate, mousePointer.boardCoordinate);
                return MouseCommand.CommandResultType.WORKING;

            case MousePointerType.UP:
                this.onMouseUp(environment, mousePointer);
                return MouseCommand.CommandResultType.DONE;

            case MousePointerType.MOVE:
            case MousePointerType.CLICK:
            case MousePointerType.DOUBLE_CLICK:
            case MousePointerType.IDLE:
                return MouseCommand.CommandResultType.UNKNOWN;
        }
    }

    private onMouseUp(environment: CommandEnvironment, mousePointer: MousePointerUp) {
        const text = this.workingShape;
        if (!text) return;

        this.changeShapeBound(environment, mousePointer.mouseDownBoardCoordinate, mousePointer.boardCoordinate);

        const isFreeText = this.isFreeText(text);
        if (isFreeText) {
            this.makeTextFree(environment, text);
        } else {
            environment.addSelectedShape(text);
        }

        if (this.isTextEditable) {
            this.enterTextTypeMode(environment, text, isFreeText);
        }
    }

    private isFreeText(text: Text): boolean {
        return this.isTextEditable && text.bound.width === 1 && text.bound.height === 1;
    }

    private makeTextFree(environment: CommandEnvironment, text: Text) {
        const boundExtra = text.extra.boundExtra.copy({ isFillEnabled: false, isBorderEnabled: false });
        const textAlignExtra = text.extra.textAlign.copy({
            horizontalAlign: TextHorizontalAlign.LEFT,
            verticalAlign: TextVerticalAlign.TOP,
        });
        const changeExtraCommand = new ChangeExtra(text, new TextExtra(boundExtra, textAlignExtra));
        environment.shapeManager.execute(changeExtraCommand);
    }

    private enterTextTypeMode(environment: CommandEnvironment, text: Text, isFreeText: boolean) {
        environment.enterEditingMode();
        environment.shapeManager.execute(new UpdateTextEditingMode(text, true));

        EditTextShapeHelper.showEditTextDialog(environment, text, isFreeText, (newText) => {
            environment.shapeManager.execute(new UpdateTextEditingMode(text, false));
            this.adjustTextShape(environment, text, isFreeText);

            environment.exitEditingMode(newText.length > 0);
            this.workingShape = null;
        });
    }

    private adjustTextShape(environment: CommandEnvironment, text: Text, isFreeText: boolean) {
        if (isFreeText && text.text.length === 0) {
            environment.removeShape(text);
            return;
        }

        const newSize = isFreeText ? this.getFreeTextActualSize(text) : this.getNormalTextActualSize(text);
        const newBound = text.bound.copy({ size: newSize });
        environment.shapeManager.execute(new ChangeBound(text, newBound));
        environment.addSelectedShape(text);
    }

    private getFreeTextActualSize(text: Text): Size {
        const lines = text.text.split('\n');
        const maxWidth = Math.max(...lines.map(line => line.length));
        return new Size(maxWidth, lines.length);
    }

    private getNormalTextActualSize(text: Text): Size {
        const height = text.extra.hasBorder()
            ? text.renderableText.getRenderableText().length + 2
            : text.renderableText.getRenderableText().length;
        const newHeight = Math.max(height, text.bound.height);
        return new Size(text.bound.width, newHeight);
    }

    private changeShapeBound(environment: CommandEnvironment, point1: Point, point2: Point) {
        const currentShape = this.workingShape;
        if (!currentShape) return;

        const rect = Rect.byLTRB(point1.left, point1.top, point2.left, point2.top);
        environment.shapeManager.execute(new ChangeBound(currentShape, rect));
    }
}
