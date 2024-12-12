import type { Point } from "$libs/graphics-geo/point";
import { TODO } from "$libs/todo";
import { BaseCanvasViewController } from '$mono/workspace/canvas/base-canvas-controller';
import type { ThemeManager } from '$mono/ui-state-manager/theme-manager';
import {
    type InteractionBound,
    InteractionBoundType,
    type InteractionPoint,
    LineInteractionBound,
    ScalableInteractionBound,
} from '$mono/shape/interaction-bound';
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
        private themeManager: ThemeManager,
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
            switch (bound.type) {
                case InteractionBoundType.LINE:
                    this.drawLineInteractionBound(bound as LineInteractionBound);
                    break;
                case InteractionBoundType.SCALABLE_SHAPE:
                    this.drawScalableInteractionBound(bound as ScalableInteractionBound);
                    break;
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
        context.strokeStyle = this.themeManager.getThemedColorCode(
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
        context.strokeStyle = this.themeManager.getThemedColorCode(strokeColor);
        context.fillStyle = this.themeManager.getThemedColorCode(fillColor);
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
        // TODO: Implement this method
        TODO("Implement this method");
        return null;
    }
}
