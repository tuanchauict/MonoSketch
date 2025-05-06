/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Comparable } from "$libs/comparable";
import { DirectedPoint, Direction, Point } from "$libs/graphics-geo/point";

/**
 * A helper class for line shapes.
 */
class LineHelperImpl {
    createJointPoints(seedPoints: DirectedPoint[]): Point[] {
        const mainPoints = this.createAnchorPoints(seedPoints);
        if (mainPoints.length === 0) {
            return [];
        }
        const directedJointPoints = [mainPoints[0]];
        for (let i = 1; i < mainPoints.length; i++) {
            const startPoint = directedJointPoints[directedJointPoints.length - 1];
            const endPoint = mainPoints[i];
            let middlePoint: DirectedPoint | null = null;
            if (!isPointsOnSameStraightLine(startPoint, endPoint)) {
                if (startPoint.direction === Direction.HORIZONTAL) {
                    middlePoint = DirectedPoint.of(
                        endPoint.direction,
                        endPoint.left,
                        startPoint.top,
                    );
                } else {
                    middlePoint = DirectedPoint.of(
                        endPoint.direction,
                        startPoint.left,
                        endPoint.top,
                    );
                }
            }
            if (middlePoint) {
                directedJointPoints.push(middlePoint);
            }
            directedJointPoints.push(endPoint);
        }
        const jointPoints = directedJointPoints.map(dp => dp.point);
        return this.reduce(jointPoints);
    }

    private createAnchorPoints(initAnchorPoints: DirectedPoint[]): DirectedPoint[] {
        if (initAnchorPoints.length === 0) {
            return [];
        }
        const mainPoints = [initAnchorPoints[0]];
        for (const endPoint of initAnchorPoints.slice(1)) {
            const startPoint = mainPoints[mainPoints.length - 1];
            if (startPoint.direction === endPoint.direction &&
                !isPointsOnSameStraightLine(startPoint, endPoint)) {
                const middleLeft = (startPoint.left + endPoint.left) / 2;
                const middleTop = (startPoint.top + endPoint.top) / 2;
                const middleDirection = toRightAngleDirection(startPoint.direction);
                const middlePoint = DirectedPoint.ofF(middleDirection, middleLeft, middleTop);
                mainPoints.push(middlePoint);
            }
            mainPoints.push(endPoint);
        }
        return mainPoints;
    }

    reduce(points: Point[]): Point[] {
        return this.reduceInner(this.reduceInner(points));
    }

    private reduceInner(points: Point[]): Point[] {
        if (points.length === 0) {
            return points;
        }
        const list: Point[] = [];
        for (let i = 1; i < points.length; i++) {
            const p1 = list[list.length - 2];
            const p2 = list[list.length - 1];
            const p3 = points[i];
            if (!p1 || !p2 || !this.isOnStraightLine(p1, p2, p3, false)) {
                list.push(p3);
            } else {
                list[list.length - 1] = p3;
            }
        }

        const p1 = points[0];
        const p2 = list[0];
        const p3 = list[1];
        if (!p2 || !p3 || !this.isOnStraightLine(p1, p2, p3, false)) {
            list.unshift(p1);
        } else {
            list[0] = p1;
        }
        return list;
    }

    isOnStraightLine(p1: Point, p2: Point, p3: Point, isInOrderedRequired: boolean): boolean {
        if (isInOrderedRequired) {
            return isNumbersEquals(p1.left, p2.left, p3.left) && isNumbersMonotonic(p1.top, p2.top, p3.top) ||
                isNumbersEquals(p1.top, p2.top, p3.top) && isNumbersMonotonic(p1.left, p2.left, p3.left);
        } else {
            return isNumbersEquals(p1.left, p2.left, p3.left) || isNumbersEquals(p1.top, p2.top, p3.top);
        }
    }

    createEdges(jointPoints: Point[]): LineEdge[] {
        const edges: LineEdge[] = [];
        for (let i = 0; i < jointPoints.length - 1; i++) {
            const startPoint = jointPoints[i];
            const endPoint = jointPoints[i + 1];
            edges.push(LineEdge.of(startPoint, endPoint));
        }
        return edges;
    }

    isHorizontal(p1: Point, p2: Point): boolean {
        return p1.top === p2.top;
    }
}

export const LineHelper = new LineHelperImpl();

export class LineEdge implements Comparable {
    middleLeft: number;
    middleTop: number;
    isHorizontal: boolean;

    constructor(
        public id: number,
        public startPoint: Point,
        public endPoint: Point,
    ) {
        this.middleLeft = (startPoint.left + endPoint.left) / 2.0;
        this.middleTop = (startPoint.top + endPoint.top) / 2.0;
        this.isHorizontal = LineHelper.isHorizontal(startPoint, endPoint);
    }

    translate(point: Point): LineEdge {
        const newStartPoint = this.isHorizontal
            ? this.startPoint.copy({ top: point.top })
            : this.startPoint.copy({ left: point.left });
        const newEndPoint = this.isHorizontal
            ? this.endPoint.copy({ top: point.top })
            : this.endPoint.copy({ left: point.left });
        return new LineEdge(this.id, newStartPoint, newEndPoint);
    }

    contains(point: Point): boolean {
        return LineHelper.isOnStraightLine(this.startPoint, point, this.endPoint, true);
    }

    equals(other: unknown): boolean {
        if (!(other instanceof LineEdge)) {
            return false;
        }
        return this.startPoint.equals(other.startPoint) && this.endPoint.equals(other.endPoint);
    }

    copy({
        id = this.id,
        startPoint = this.startPoint,
        endPoint = this.endPoint,
    }: {
        id?: number,
        startPoint?: Point,
        endPoint?: Point,
    } = {}): LineEdge {
        return new LineEdge(id, startPoint, endPoint);
    }

    static of(startPoint: Point, endPoint: Point): LineEdge {
        return new LineEdge(this.nextId(), startPoint, endPoint);
    }

    static lastUsedId = 0;

    private static nextId(): number {
        return ++LineEdge.lastUsedId;
    }
}

export enum LineAnchor {
    START, END
}

export interface LineAnchorPointUpdate {
    anchor: LineAnchor;
    point: DirectedPoint;
}

const isNumbersEquals = (a: number, b: number, c: number): boolean =>
    a === b && b === c;

const isNumbersMonotonic = (a: number, b: number, c: number): boolean =>
    (b >= a && b <= c) || (b <= a && b >= c);

const isPointsOnSameStraightLine = (p1: DirectedPoint, p2: DirectedPoint): boolean =>
    p1.left === p2.left || p1.top === p2.top;

const toRightAngleDirection = (direction: Direction): Direction => {
    return direction === Direction.HORIZONTAL ? Direction.VERTICAL : Direction.HORIZONTAL;
};
