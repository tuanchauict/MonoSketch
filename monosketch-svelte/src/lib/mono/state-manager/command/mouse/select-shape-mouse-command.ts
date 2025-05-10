import { Rect } from "$libs/graphics-geo/rect";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { MouseCommand } from "$mono/state-manager/command/mouse/mouse-command";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import { type MousePointer, MousePointerType } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A [MouseCommand] to select shapes.
 */
class SelectShapeMouseCommandImpl implements MouseCommand {
    mouseCursor: MouseCursor = MouseCursor.DEFAULT;

    execute(environment: CommandEnvironment, mousePointer: MousePointer): MouseCommand.CommandResultType {
        switch (mousePointer.type) {
            case MousePointerType.DOWN:
                return MouseCommand.CommandResultType.WORKING;
            case MousePointerType.DRAG:
                environment.setSelectionBound(
                    Rect.byLTRB(
                        mousePointer.mouseDownBoardCoordinate.left,
                        mousePointer.mouseDownBoardCoordinate.top,
                        mousePointer.boardCoordinate.left,
                        mousePointer.boardCoordinate.top,
                    ),
                );
                return MouseCommand.CommandResultType.WORKING;
            case MousePointerType.UP: {
                environment.setSelectionBound(null);
                const area = Rect.byLTRB(
                    mousePointer.mouseDownBoardCoordinate.left,
                    mousePointer.mouseDownBoardCoordinate.top,
                    mousePointer.boardCoordinate.left,
                    mousePointer.boardCoordinate.top,
                );
                // 1 cell is empty
                const isEmpty = area.width * area.height === 1;
                // Only select shapes if the area is not empty, otherwise, this is a click event
                const shapes = !isEmpty ? environment.getAllShapesInZone(area) : [];
                if (!mousePointer.isWithShiftKey) {
                    environment.clearSelectedShapes();
                }
                for (const shape of shapes) {
                    environment.addSelectedShape(shape);
                }
                return MouseCommand.CommandResultType.WORKING;
            }
            case MousePointerType.CLICK: {
                const shapes = environment.getShapes(mousePointer.boardCoordinate);
                if (shapes.length > 0) {
                    const shape = shapes[shapes.length - 1];
                    if (mousePointer.isWithShiftKey) {
                        environment.toggleShapeSelection(shape);
                    } else {
                        environment.clearSelectedShapes();
                        environment.addSelectedShape(shape);
                    }
                }
                return MouseCommand.CommandResultType.DONE;
            }

            case MousePointerType.DOUBLE_CLICK:
            case MousePointerType.MOVE:
            case MousePointerType.IDLE:
                return MouseCommand.CommandResultType.DONE;
        }
    }
}

export const SelectShapeMouseCommand = new SelectShapeMouseCommandImpl();
