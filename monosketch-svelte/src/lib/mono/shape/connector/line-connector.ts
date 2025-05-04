/*
 * Copyright (c) 2024, tuanchauict
 */


import { DirectedPoint, Direction, Point, PointF } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import type { Identifier } from "$mono/shape/collection/identifier";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { LineAnchor } from "$mono/shape/shape/linehelper";

/**
 * A [Identifier] of Line's connector.
 * This is a minimal version of [LineConnector] that only has required information to identify
 * the connector.
 */
export class ConnectorIdentifier implements Identifier {
    id: string;

    constructor(lineId: string, anchor: LineAnchor) {
        this.id = toId(lineId, anchor);
    }
}

/**
 * A connector for [Line].
 *
 * @property lineId The id of the target of this connector
 * @property anchor The extra information for identifying which head of the line (start or end)
 * @property ratio The relative position of the connector based on the size of the box.
 * @property offset The absolute offset of the connector to the box
 */
export class LineConnector extends ConnectorIdentifier {
    constructor(
        public lineId: string,
        public anchor: LineAnchor,
        public ratio: PointF,
        public offset: Point,
    ) {
        super(lineId, anchor);
    }
}

export enum Around {
    LEFT, TOP, RIGHT, BOTTOM
}

const toId = (lineId: string, anchor: LineAnchor): string => `${lineId}_${anchor}`;

/**
 * A Use case for shape connector
 */
class ShapeConnectorUseCaseImpl {
    private static readonly MAX_DISTANCE = 1;

    getConnectableShape(point: Point, candidates: Iterable<AbstractShape>): AbstractShape | null {
        for (const shape of Array.from(candidates).reverse()) {
            if (this.canConnect(point, shape)) {
                return shape;
            }
        }
        return null;
    }

    private canConnect(point: Point, shape: AbstractShape): boolean {
        if (!shape.canHaveConnectors) {
            return false;
        }
        return this.detectAround(point, shape.bound).some(Boolean);
    }

    getAround(anchorPoint: DirectedPoint, boxBound: Rect): Around | null {
        const [isAroundLeft, isAroundRight, isAroundTop, isAroundBottom] = this.detectAround(anchorPoint.point, boxBound);

        const isHorizontal = anchorPoint.direction === Direction.HORIZONTAL;
        if (isAroundLeft) {
            if (isAroundTop && !isHorizontal) return Around.TOP;
            if (isAroundBottom && !isHorizontal) return Around.BOTTOM;
            return Around.LEFT;
        }

        if (isAroundTop) {
            if (isAroundRight && isHorizontal) return Around.RIGHT;
            return Around.TOP;
        }

        if (isAroundRight) {
            if (isAroundBottom && !isHorizontal) return Around.BOTTOM;
            return Around.RIGHT;
        }

        if (isAroundBottom) return Around.BOTTOM;

        return null;
    }

    private detectAround(point: Point, boxBound: Rect): boolean[] {
        const isAroundLeft = this.isAround(point.left, boxBound.left) &&
            this.isAround(point.top, boxBound.top, boxBound.bottom);
        const isAroundRight = this.isAround(point.left, boxBound.right) &&
            this.isAround(point.top, boxBound.top, boxBound.bottom);
        const isAroundTop = this.isAround(point.top, boxBound.top) &&
            this.isAround(point.left, boxBound.left, boxBound.right);
        const isAroundBottom = this.isAround(point.top, boxBound.bottom) &&
            this.isAround(point.left, boxBound.left, boxBound.right);
        return [isAroundLeft, isAroundRight, isAroundTop, isAroundBottom];
    }

    private isAround(value: number, lower: number, upper: number = lower, distance: number = ShapeConnectorUseCaseImpl.MAX_DISTANCE): boolean {
        return value >= (lower - distance) && value <= (upper + distance);
    }

    calculateRatio(around: Around, anchorPoint: DirectedPoint, boxBound: Rect): PointF {
        const leftRatio = (anchorPoint.left - boxBound.left) / this.adjustSizeValue(boxBound.width);
        const topRatio = (anchorPoint.top - boxBound.top) / this.adjustSizeValue(boxBound.height);
        switch (around) {
            case Around.LEFT:
                return PointF.create({ left: 0.0, top: Math.min(Math.max(topRatio, 0.0), 1.0) });
            case Around.TOP:
                return PointF.create({ left: Math.min(Math.max(leftRatio, 0.0), 1.0), top: 0.0 });
            case Around.RIGHT:
                return PointF.create({ left: 1.0, top: Math.min(Math.max(topRatio, 0.0), 1.0) });
            case Around.BOTTOM:
                return PointF.create({ left: Math.min(Math.max(leftRatio, 0.0), 1.0), top: 1.0 });
        }
    }

    calculateOffset(around: Around, anchorPoint: DirectedPoint, boxBound: Rect): Point {
        switch (around) {
            case Around.LEFT:
                return new Point(anchorPoint.left - boxBound.left, this.offsetToRange(anchorPoint.top, boxBound.top, boxBound.bottom));
            case Around.TOP:
                return new Point(this.offsetToRange(anchorPoint.left, boxBound.left, boxBound.right), anchorPoint.top - boxBound.top);
            case Around.RIGHT:
                return new Point(anchorPoint.left - boxBound.right, this.offsetToRange(anchorPoint.top, boxBound.top, boxBound.bottom));
            case Around.BOTTOM:
                return new Point(this.offsetToRange(anchorPoint.left, boxBound.left, boxBound.right), anchorPoint.top - boxBound.bottom);
        }
    }

    getPointInNewBound(lineConnector: LineConnector, direction: Direction, boxBound: Rect): DirectedPoint {
        return DirectedPoint.ofF(direction, this.getLeftInNewBound(lineConnector, boxBound), this.getTopInNewBound(lineConnector, boxBound));
    }

    private getLeftInNewBound(lineConnector: LineConnector, boxBound: Rect): number {
        return Math.round(boxBound.left + (boxBound.width - 1) * lineConnector.ratio.left + lineConnector.offset.left);
    }

    private getTopInNewBound(lineConnector: LineConnector, boxBound: Rect): number {
        return Math.round(boxBound.top + (boxBound.height - 1) * lineConnector.ratio.top + lineConnector.offset.top);
    }

    private offsetToRange(value: number, lower: number, upper: number): number {
        if (value < lower) return value - lower;
        if (value > upper) return value - upper;
        return 0;
    }

    private adjustSizeValue(value: number): number {
        return Math.max(value - 1, 1);
    }
}

export const ShapeConnectorUseCase = new ShapeConnectorUseCaseImpl();
