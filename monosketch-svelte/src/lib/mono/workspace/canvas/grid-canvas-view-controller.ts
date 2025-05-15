import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";
import { BaseCanvasViewController } from '$mono/workspace/canvas/base-canvas-controller';
import { ThemeColors } from '$mono/ui-state-manager/states';

/**
 * A class for managing the grid canvas.
 */
export class GridCanvasViewController extends BaseCanvasViewController {
    constructor(
        canvas: HTMLCanvasElement,
        private appUiStateManager: AppUiStateManager,
    ) {
        super(canvas);
    }

    protected drawInternal = () => {
        const context = this.context;
        context.strokeStyle = this.appUiStateManager.getThemedColorCode(ThemeColors.GridLine);
        context.lineWidth = 0.25;
        context.stroke(this.createGridPath());
    };

    private createGridPath = (): Path2D => {
        const drawingInfo = this.drawingInfo;
        const zeroX = drawingInfo.toXPx(drawingInfo.boardColumnRange.start - 1.0);
        const maxX = drawingInfo.toXPx(drawingInfo.boardColumnRange.endExclusive + 1.0);
        const zeroY = drawingInfo.toYPx(drawingInfo.boardRowRange.start - 1.0);
        const maxY = drawingInfo.toYPx(drawingInfo.boardRowRange.endExclusive + 1.0);

        const path = new Path2D();

        for (const row of drawingInfo.boardRowRange) {
            const yPx = drawingInfo.toYPx(row);
            this.addHLine(path, zeroX, yPx, maxX - zeroX);
        }

        for (const column of drawingInfo.boardColumnRange) {
            const xPx = drawingInfo.toXPx(column);
            this.addVLine(path, xPx, zeroY, maxY - zeroY);
        }

        return path;
    };
}
