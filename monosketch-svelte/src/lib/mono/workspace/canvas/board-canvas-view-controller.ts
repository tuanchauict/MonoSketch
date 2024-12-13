import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";
import { BaseCanvasViewController } from '$mono/workspace/canvas/base-canvas-controller';
import { HighlightType, type Pixel } from '$mono/monobitmap/board/pixel';
import { ThemeColors } from '$mono/ui-state-manager/states';
import type { MonoBoard } from '$mono/monobitmap/board/board';

/**
 * A class for managing the board canvas.
 */
export class BoardCanvasViewController extends BaseCanvasViewController {

    constructor(
        canvas: HTMLCanvasElement,
        private board: MonoBoard,
        private appUiStateManager: AppUiStateManager,
    ) {
        super(canvas);
    }

    protected drawInternal() {
        const drawingInfo = this.drawingInfo;
        this.context.font = drawingInfo.font;
        for (const row of drawingInfo.boardRowRange) {
            for (const column of drawingInfo.boardColumnRange) {
                this.drawPixel(this.board.get(row, column), row, column);
            }
        }
    }

    private drawPixel = (pixel: Pixel, row: number, column: number) => {
        if (pixel.isTransparent) {
            return;
        }
        this.context.fillStyle = this.appUiStateManager.getThemedColorCode(this.getPixelColor(pixel));
        this.drawText(pixel.visualChar, row, column);
    };

    private getPixelColor = (pixel: Pixel) => {
        switch (pixel.highlight) {
            case HighlightType.NO:
                return ThemeColors.Shape;
            case HighlightType.SELECTED:
                return ThemeColors.ShapeSelected;
            case HighlightType.TEXT_EDITING:
                return ThemeColors.ShapeTextEditing;
            case HighlightType.LINE_CONNECT_FOCUSING:
                return ThemeColors.ShapeLineConnectTarget;
            default:
                throw new Error(`Unknown highlight type: ${pixel.highlight}`);
        }
    };
}
