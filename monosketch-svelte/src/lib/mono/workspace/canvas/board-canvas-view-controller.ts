import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";
import { BaseCanvasViewController } from '$mono/workspace/canvas/base-canvas-controller';
import { HighlightType, type Pixel } from '$mono/monobitmap/board/pixel';
import { type ThemeColor, ThemeColors } from '$mono/ui-state-manager/states';
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
        const pixelColor = HighlightTypeToThemeColor[pixel.highlight];
        this.context.fillStyle = this.appUiStateManager.getThemedColorCode(pixelColor);
        this.drawText(pixel.visualChar, row, column);
    };
}

const HighlightTypeToThemeColor: Record<HighlightType, ThemeColor> = {
    [HighlightType.NO]: ThemeColors.Shape,
    [HighlightType.SELECTED]: ThemeColors.ShapeSelected,
    [HighlightType.TEXT_EDITING]: ThemeColors.ShapeTextEditing,
    [HighlightType.LINE_CONNECT_FOCUSING]: ThemeColors.ShapeLineConnectTarget,
};
