import { BaseCanvasViewController } from '$mono/workspace/canvas/base-canvas-controller';
import { type ThemeManager } from '$mono/ui-state-manager/theme-manager';
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
        private themeManager: ThemeManager,
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
        this.context.fillStyle = this.themeManager.getThemedColorCode(this.getPixelColor(pixel));
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
