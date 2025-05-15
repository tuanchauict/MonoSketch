import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";
import { DrawingInfo } from '$mono/workspace/drawing-info';
import { Size } from '$libs/graphics-geo/size';
import { DEFAULT_FONT } from '$mono/workspace/drawing-info/drawing-info-controller';
import { ThemeColors } from '$mono/ui-state-manager/states';
import { BaseCanvasViewController } from '$mono/workspace/canvas/base-canvas-controller';

const AXIS_RULER_SIZE = 12.0;
export const AXIS_Y_WIDTH = 33.0;
export const AXIS_X_HEIGHT = 18.0;

export class AxisCanvasViewController extends BaseCanvasViewController {
    constructor(
        canvas: HTMLCanvasElement,
        private appUiStateManager: AppUiStateManager,
    ) {
        super(canvas);
    }

    setDrawingInfo = (drawingInfo: DrawingInfo) => {
        const canvasSizePx = Size.of(
            drawingInfo.canvasSizePx.width + AXIS_Y_WIDTH,
            drawingInfo.canvasSizePx.height + AXIS_X_HEIGHT,
        );
        const isSizeChanged = !this.drawingInfo.canvasSizePx.equals(canvasSizePx);
        if (isSizeChanged) {
            this.resizeCanvas(canvasSizePx.width, canvasSizePx.height);
        }
        this.drawingInfo = drawingInfo;
        if (isSizeChanged) {
            this.draw();
        }
    };

    protected drawInternal = () => {
        this.context.font = `normal normal normal 10.5px ${DEFAULT_FONT}`;
        this.drawAxis();
    };

    private drawAxis = () => {
        const context = this.context;
        const themeManager = this.appUiStateManager;
        const drawingInfo = this.drawingInfo;

        const cellSizePx = this.drawingInfo.cellSizePx;
        const canvasSizePx = this.drawingInfo.canvasSizePx;

        const xAxisHeight = AXIS_X_HEIGHT;
        const yAxisWidth = AXIS_Y_WIDTH;
        const canvasWidth = canvasSizePx.width + yAxisWidth;
        const canvasHeight = canvasSizePx.height + xAxisHeight;

        const path = new Path2D();

        context.lineWidth = 1.0;
        context.fillStyle = themeManager.getThemedColorCode(ThemeColors.AxisBackground);
        context.fillRect(0, xAxisHeight, yAxisWidth, canvasHeight);

        context.fillStyle = themeManager.getThemedColorCode(ThemeColors.AxisText);
        context.textAlign = 'right';

        this.addHLine(path, 0, xAxisHeight, canvasWidth);

        for (const row of drawingInfo.boardRowRange) {
            const text = `${row}`;
            const yPx = xAxisHeight + drawingInfo.toYPx(row) + 3;
            const xPx = yAxisWidth - 0.5 * cellSizePx.width;
            context.fillText(text, xPx, yPx);
        }

        context.fillStyle = themeManager.getThemedColorCode(ThemeColors.AxisBackground);
        context.fillRect(0, 0, canvasWidth, xAxisHeight);

        context.fillStyle = themeManager.getThemedColorCode(ThemeColors.AxisText);
        context.textAlign = 'left';

        this.addVLine(path, yAxisWidth, xAxisHeight, canvasWidth);

        for (const column of drawingInfo.boardColumnRange) {
            if (column % 20 !== 0) {
                continue;
            }
            const text = `${column}`;
            const xPx = drawingInfo.toXPx(column) + AXIS_Y_WIDTH;
            context.fillText(text, xPx + 2, 7.0);

            this.addVLine(path, xPx, xAxisHeight - AXIS_RULER_SIZE, AXIS_RULER_SIZE);
        }

        context.strokeStyle = themeManager.getThemedColorCode(ThemeColors.AxisRule);
        context.stroke(path);
    };
}
