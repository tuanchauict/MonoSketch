/*
 * Copyright (c) 2024, tuanchauict
 */

import { describe, it, expect } from 'vitest';
import { DirectedPoint, Direction, Point } from '$libs/graphics-geo/point';
import { LineEdge, LineHelper } from '$mono/shape/shape/linehelper';

describe('LineHelper', () => {
    it('testCreateJointPoints_sameHorizontalLine_HH', () => {
        const p1 = DirectedPoint.of(Direction.HORIZONTAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.HORIZONTAL, 10, 0);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(10, 0),
        ]);
    });

    it('testCreateJointPoints_sameHorizontalLine_HV', () => {
        const p1 = DirectedPoint.of(Direction.HORIZONTAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.VERTICAL, 10, 0);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(10, 0),
        ]);
    });

    it('testCreateJointPoints_sameHorizontalLine_VV', () => {
        const p1 = DirectedPoint.of(Direction.VERTICAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.VERTICAL, 10, 0);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(10, 0),
        ]);
    });

    it('testCreateJointPoints_sameHorizontalLine_VH', () => {
        const p1 = DirectedPoint.of(Direction.VERTICAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.HORIZONTAL, 10, 0);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(10, 0),
        ]);
    });

    it('testCreateJointPoints_sameVerticalLine_HH', () => {
        const p1 = DirectedPoint.of(Direction.HORIZONTAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.HORIZONTAL, 0, 10);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(0, 10),
        ]);
    });

    it('testCreateJointPoints_sameVerticalLine_HV', () => {
        const p1 = DirectedPoint.of(Direction.HORIZONTAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.VERTICAL, 0, 10);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(0, 10),
        ]);
    });

    it('testCreateJointPoints_sameVerticalLine_VV', () => {
        const p1 = DirectedPoint.of(Direction.VERTICAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.VERTICAL, 0, 10);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(0, 10),
        ]);
    });

    it('testCreateJointPoints_sameVerticalLine_VH', () => {
        const p1 = DirectedPoint.of(Direction.VERTICAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.HORIZONTAL, 0, 10);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(0, 10),
        ]);
    });

    it('testCreateJointPoints_differentLines_HH', () => {
        const p1 = DirectedPoint.of(Direction.HORIZONTAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.HORIZONTAL, 10, 10);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(5, 0),
            new Point(5, 10),
            new Point(10, 10),
        ]);
    });

    it('testCreateJointPoints_differentLines_HV', () => {
        const p1 = DirectedPoint.of(Direction.HORIZONTAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.VERTICAL, 10, 10);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(10, 0),
            new Point(10, 10),
        ]);
    });

    it('testCreateJointPoints_differentLines_VV', () => {
        const p1 = DirectedPoint.of(Direction.VERTICAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.VERTICAL, 10, 10);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(0, 5),
            new Point(10, 5),
            new Point(10, 10),
        ]);
    });

    it('testCreateJointPoints_differentLines_VH', () => {
        const p1 = DirectedPoint.of(Direction.VERTICAL, 0, 0);
        const p2 = DirectedPoint.of(Direction.HORIZONTAL, 10, 10);

        expect(LineHelper.createJointPoints([p1, p2])).toEqual([
            new Point(0, 0),
            new Point(0, 10),
            new Point(10, 10),
        ]);
    });

    it('testReduce_noReduced', () => {
        // Empty
        expect(LineHelper.reduce([])).toEqual([]);

        // 1 point
        expect(LineHelper.reduce([new Point(0, 0)])).toEqual([new Point(0, 0)]);

        // 2 points
        expect(LineHelper.reduce([new Point(0, 0), new Point(0, 0)])).toEqual([new Point(0, 0), new Point(0, 0)]);

        // Different lines
        expect(LineHelper.reduce([new Point(0, 0), new Point(10, 0), new Point(10, 10)])).toEqual([new Point(0, 0), new Point(10, 0), new Point(10, 10)]);
    });

    it('testReduce_reduced', () => {
        // Same points: Identical
        expect(LineHelper.reduce([new Point(0, 0), new Point(0, 0), new Point(0, 0)])).toEqual([new Point(0, 0), new Point(0, 0)]);

        // Same points: Same first
        expect(LineHelper.reduce([new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(0, 0), new Point(10, 0)])).toEqual([new Point(0, 0), new Point(10, 0)]);

        // Same points: Same middle
        expect(LineHelper.reduce([new Point(0, 0), new Point(0, 10), new Point(0, 10), new Point(0, 10), new Point(0, 10), new Point(10, 10)])).toEqual([new Point(0, 0), new Point(0, 10), new Point(10, 10)]);

        // Same points: Same last
        expect(LineHelper.reduce([new Point(0, 0), new Point(10, 0), new Point(10, 0), new Point(10, 0), new Point(10, 0)])).toEqual([new Point(0, 0), new Point(10, 0)]);

        // Monotonic
        expect(LineHelper.reduce([new Point(0, 0), new Point(5, 0), new Point(10, 0)])).toEqual([new Point(0, 0), new Point(10, 0)]);

        // Monotonic 2
        expect(LineHelper.reduce([new Point(0, 0), new Point(2, 0), new Point(4, 0), new Point(6, 0), new Point(8, 0), new Point(10, 0)])).toEqual([new Point(0, 0), new Point(10, 0)]);

        // Non monotonic
        expect(LineHelper.reduce([new Point(0, 0), new Point(0, 5), new Point(0, 5), new Point(0, 0), new Point(10, 0)])).toEqual([new Point(0, 0), new Point(10, 0)]);
    });

    it('testIsOnStraightLine_ordered', () => {
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(2, 0), new Point(1, 0), true)).toBe(false);
        expect(LineHelper.isOnStraightLine(new Point(2, 0), new Point(0, 0), new Point(1, 0), true)).toBe(false);
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(0, 2), new Point(0, 1), true)).toBe(false);
        expect(LineHelper.isOnStraightLine(new Point(0, 2), new Point(0, 0), new Point(0, 1), true)).toBe(false);

        // Horizontal
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(1, 0), new Point(2, 0), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(2, 0), new Point(1, 0), new Point(0, 0), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(0, 0), new Point(1, 0), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(1, 0), new Point(1, 0), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(1, 0), new Point(1, 0), new Point(0, 0), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(1, 0), new Point(1, 0), new Point(1, 0), true)).toBe(true);

        // Vertical
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(0, 1), new Point(0, 2), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 2), new Point(0, 1), new Point(0, 0), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(0, 0), new Point(0, 1), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(0, 1), new Point(0, 1), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 1), new Point(0, 1), new Point(0, 0), true)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 1), new Point(0, 1), new Point(0, 1), true)).toBe(true);
    });

    it('testOnStraightLine_notOrdered', () => {
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(2, 0), new Point(1, 0), false)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(2, 0), new Point(0, 0), new Point(1, 0), false)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 0), new Point(0, 2), new Point(0, 1), false)).toBe(true);
        expect(LineHelper.isOnStraightLine(new Point(0, 2), new Point(0, 0), new Point(0, 1), false)).toBe(true);
    });

    it('testCreateEdges', () => {
        const points = [new Point(0, 0), new Point(1, 0), new Point(1, 1)];
        const edges = LineHelper.createEdges(points);
        expect(edges.length).toBe(2);

        expect(edges[0].startPoint).toEqual(new Point(0, 0));
        expect(edges[0].endPoint).toEqual(new Point(1, 0));

        expect(edges[1].startPoint).toEqual(new Point(1, 0));
        expect(edges[1].endPoint).toEqual(new Point(1, 1));
    });
});

describe('LineEdge', () => {
    it('testTranslate_horizontal', () => {
        const startPoint = new Point(0, 0);
        const endPoint = new Point(10, 0);
        const edge = LineEdge.of(startPoint, endPoint);
        const translatedEdge = edge.translate(new Point(0, 5));

        expect(translatedEdge.startPoint).toEqual(new Point(0, 5));
        expect(translatedEdge.endPoint).toEqual(new Point(10, 5));
    });

    it('testTranslate_vertical', () => {
        const startPoint = new Point(0, 0);
        const endPoint = new Point(0, 10);
        const edge = LineEdge.of(startPoint, endPoint);
        const translatedEdge = edge.translate(new Point(5, 0));

        expect(translatedEdge.startPoint).toEqual(new Point(5, 0));
        expect(translatedEdge.endPoint).toEqual(new Point(5, 10));
    });

    it('testContains', () => {
        const startPoint = new Point(0, 0);
        const endPoint = new Point(10, 0);
        const edge = LineEdge.of(startPoint, endPoint);

        expect(edge.contains(new Point(5, 0))).toBe(true);
        expect(edge.contains(new Point(10, 0))).toBe(true);
        expect(edge.contains(new Point(0, 0))).toBe(true);
        expect(edge.contains(new Point(5, 5))).toBe(false);
    });

    it('testEquals', () => {
        const startPoint1 = new Point(0, 0);
        const endPoint1 = new Point(10, 0);
        const edge1 = LineEdge.of(startPoint1, endPoint1);

        const startPoint2 = new Point(0, 0);
        const endPoint2 = new Point(10, 0);
        const edge2 = LineEdge.of(startPoint2, endPoint2);

        expect(edge1.equals(edge2)).toBe(true);
    });
});