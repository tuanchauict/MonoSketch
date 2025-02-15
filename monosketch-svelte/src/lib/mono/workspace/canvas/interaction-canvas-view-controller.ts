import type { Point } from "$libs/graphics-geo/point";
import {
    type InteractionBound,
    LineInteractionBound,
    ScalableInteractionBound,
} from "$mono/shape-interaction-bound/interaction-bound";
import type { InteractionPoint } from "$mono/shape-interaction-bound/interaction-point";
import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";
import { BaseCanvasViewController } from '$mono/workspace/canvas/base-canvas-controller';
import { type ThemeColor, ThemeColors } from '$mono/ui-state-manager/states';

const DOT_RADIUS = 3.2;
const SMALL_DOT_RADIUS = 1.0;
const FULL_CIRCLE_ARG = 2 * Math.PI;

/**
 * A class for managing the interaction canvas.
 * This class is responsible for drawing interaction bounds and the interaction indicators on the
 * canvas.
 */
export class InteractionCanvasViewController extends BaseCanvasViewController {
    private interactionBounds: InteractionBound[] = [];
    private isMouseMoving = false;

    constructor(
        canvas: HTMLCanvasElement,
        private appUiStateManager: AppUiStateManager,
    ) {
        super(canvas);
        this.context.imageSmoothingQuality = 'high';
    }

    setInteractionBounds = (interactionBounds: InteractionBound[]) => {
        this.interactionBounds = interactionBounds;
    };

    setMouseMoving = (isMouseMoving: boolean) => {
        this.isMouseMoving = isMouseMoving;
    };

    protected drawInternal = () => {
        for (const bound of this.interactionBounds) {
            if (bound instanceof LineInteractionBound) {
                this.drawLineInteractionBound(bound);
            } else if (bound instanceof ScalableInteractionBound) {
                this.drawScalableInteractionBound(bound);
            }
        }
    };

    private drawLineInteractionBound = (bound: LineInteractionBound) => {
        if (this.isMouseMoving) {
            return;
        }

        this.drawInteractionPoints(
            bound.interactionPoints,
            ThemeColors.SelectionBoundStroke,
            ThemeColors.SelectionDotFill,
            DOT_RADIUS,
        );

        // Draw a small dot inside the start dot to make it simper to distinguish start and end dots
        this.drawInteractionPoints(
            [bound.interactionPoints[0]],
            ThemeColors.SelectionBoundStroke,
            ThemeColors.SelectionDotStroke,
            SMALL_DOT_RADIUS,
        );
    };

    private drawScalableInteractionBound = (bound: ScalableInteractionBound) => {
        const context = this.context;
        context.strokeStyle = this.appUiStateManager.getThemedColorCode(
            ThemeColors.SelectionBoundStroke,
        );
        context.lineWidth = 1.0;
        context.stroke(this.createScalableBoundPath(bound));

        if (this.isMouseMoving) {
            return;
        }
        this.drawInteractionPoints(
            bound.interactionPoints,
            ThemeColors.SelectionBoundStroke,
            ThemeColors.SelectionDotFill,
            DOT_RADIUS,
        );
    };

    private drawInteractionPoints = (
        points: InteractionPoint[],
        strokeColor: ThemeColor,
        fillColor: ThemeColor,
        dotRadius: number,
    ) => {
        const context = this.context;
        const drawingInfo = this.drawingInfo;
        const dotPath = new Path2D();
        context.beginPath();
        for (const point of points) {
            const xPx = drawingInfo.toXPx(point.left);
            const yPx = drawingInfo.toYPx(point.top);
            dotPath.moveTo(xPx, yPx);
            dotPath.arc(xPx, yPx, dotRadius, 0, FULL_CIRCLE_ARG);
        }
        context.strokeStyle = this.appUiStateManager.getThemedColorCode(strokeColor);
        context.fillStyle = this.appUiStateManager.getThemedColorCode(fillColor);
        context.imageSmoothingEnabled = true;
        context.stroke(dotPath);
        context.fill(dotPath);
        context.imageSmoothingEnabled = false;
        context.closePath();
    };

    private createScalableBoundPath = (bound: ScalableInteractionBound): Path2D => {
        const drawingInfo = this.drawingInfo;
        const leftPx = drawingInfo.toXPx(bound.left);
        const rightPx = drawingInfo.toXPx(bound.right);
        const topPx = drawingInfo.toYPx(bound.top);
        const bottomPx = drawingInfo.toYPx(bound.bottom);
        const path = new Path2D();
        path.rect(leftPx, topPx, rightPx - leftPx, bottomPx - topPx);
        return path;
    };

    getInteractionPoint = (pointPx: Point): InteractionPoint | null => {
        for (const bound of this.interactionBounds.reverse()) {
            const interactionPoint = this.getClosePointOfBound(bound, pointPx);
            if (interactionPoint) {
                return interactionPoint;
            }
        }
        return null;
    };

    private getClosePointOfBound(bound: InteractionBound, pointPx: Point) {
        for (const interactionPoint of bound.interactionPoints.reverse()) {
            const leftPx = this.drawingInfo.toXPx(interactionPoint.left);
            const topPx = this.drawingInfo.toYPx(interactionPoint.top);
            if (Math.abs(leftPx - pointPx.left) < 6 && Math.abs(topPx - pointPx.top) < 6) {
                return interactionPoint;
            }
        }
    }
}
