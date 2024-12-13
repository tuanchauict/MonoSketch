import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";
import { BaseCanvasViewController } from '$mono/workspace/canvas/base-canvas-controller';
import type { Rect } from '$libs/graphics-geo/rect';
import { ThemeColors } from '$mono/ui-state-manager/states';

const DASH_PATTERN = [8, 6];

/**
 * A canvas view controller to render the selection rectangle bound indicator.
 */
export class SelectionCanvasViewController extends BaseCanvasViewController {
    private selectingBound?: Rect;

    constructor(
        canvas: HTMLCanvasElement,
        private appUiStateManager: AppUiStateManager,
    ) {
        super(canvas);
    }

    setSelectingBound = (selectingBound?: Rect) => {
        this.selectingBound = selectingBound;
    };

    protected drawInternal() {
        const bound = this.selectingBound;
        if (!bound) {
            return;
        }

        const context = this.context;
        const drawingInfo = this.drawingInfo;

        const leftPx = drawingInfo.toXPx(bound.left);
        const topPx = drawingInfo.toYPx(bound.top);
        const rightPx = drawingInfo.toXPx(bound.right + 1.0);
        const bottomPx = drawingInfo.toYPx(bound.bottom + 1.0);

        const path = new Path2D();
        path.rect(leftPx, topPx, rightPx - leftPx, bottomPx - topPx);
        context.strokeStyle = this.appUiStateManager.getThemedColorCode(ThemeColors.SelectionAreaStroke);
        context.lineWidth = 1.0;
        context.setLineDash(DASH_PATTERN);
        context.stroke(path);
    }
}
