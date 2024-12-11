/*
 * Copyright (c) 2024, tuanchauict
 */

import type { SerializableLine } from "$mono/shape/serialization/shapes";
import { describe, it, expect } from 'vitest';
import { DirectedPoint, Direction, Point } from '$libs/graphics-geo/point';
import { Line } from '$mono/shape/shape/line';
import { LineAnchor, LineHelper } from '$mono/shape/shape/linehelper';

describe('Line', () => {
    const PARENT_ID = "1";

    it('testSerialization_init', () => {
        const startPoint = DirectedPoint.of(Direction.VERTICAL, 1, 2);
        const endPoint = DirectedPoint.of(Direction.HORIZONTAL, 3, 4);
        const line = new Line(startPoint, endPoint, undefined, PARENT_ID);

        const serializableLine = line.toSerializableShape(true) as SerializableLine;
        expect(serializableLine.startPoint).toEqual(startPoint);
        expect(serializableLine.endPoint).toEqual(endPoint);
        expect(LineHelper.reduce(serializableLine.jointPoints)).toEqual(line.reducedJoinPoints);
        expect(serializableLine.extra).toEqual(line.extra.toSerializableExtra());
        expect(serializableLine.wasMovingEdge).toBe(false);
    });

    it('testSerialization_moveAnchorPoints', () => {
        const startPoint = DirectedPoint.of(Direction.VERTICAL, 1, 2);
        const endPoint = DirectedPoint.of(Direction.HORIZONTAL, 3, 4);
        const line = new Line(startPoint, endPoint, undefined, PARENT_ID);

        const newStartPoint = DirectedPoint.of(Direction.HORIZONTAL, 4, 5);
        const newEndPoint = DirectedPoint.of(Direction.VERTICAL, 7, 8);
        line.moveAnchorPoint(
            { anchor: LineAnchor.START, point: newStartPoint },
            true,
            false,
        );
        line.moveAnchorPoint(
            { anchor: LineAnchor.END, point: newEndPoint },
            true,
            false,
        );

        const serializableLine = line.toSerializableShape(true) as SerializableLine;
        expect(serializableLine.startPoint).toEqual(newStartPoint);
        expect(serializableLine.endPoint).toEqual(newEndPoint);
        expect(LineHelper.reduce(serializableLine.jointPoints)).toEqual(line.reducedJoinPoints);
        expect(serializableLine.extra).toEqual(line.extra.toSerializableExtra());
        expect(serializableLine.wasMovingEdge).toBe(false);
    });

    it('testSerialization_moveEdge', () => {
        const startPoint = DirectedPoint.of(Direction.VERTICAL, 1, 2);
        const endPoint = DirectedPoint.of(Direction.HORIZONTAL, 3, 4);
        const line = new Line(startPoint, endPoint, undefined, PARENT_ID);
        line.moveEdge(line.edges[0].id, new Point(10, 10), true);

        const serializableLine = line.toSerializableShape(true) as SerializableLine;
        expect(serializableLine.startPoint).toEqual(startPoint);
        expect(serializableLine.endPoint).toEqual(endPoint);
        expect(LineHelper.reduce(serializableLine.jointPoints)).toEqual(line.reducedJoinPoints);
        expect(serializableLine.extra).toEqual(line.extra.toSerializableExtra());
        expect(serializableLine.wasMovingEdge).toBe(true);
    });

    it('testSerialization_restoreInit', () => {
        const startPoint = DirectedPoint.of(Direction.VERTICAL, 1, 2);
        const endPoint = DirectedPoint.of(Direction.HORIZONTAL, 3, 4);
        const line = new Line(startPoint, endPoint, undefined, PARENT_ID);

        const serializableLine = line.toSerializableShape(true) as SerializableLine;
        const line2 = Line.fromSerializable(serializableLine, PARENT_ID);
        expect(line2.getDirection(LineAnchor.START)).toEqual(line.getDirection(LineAnchor.START));
        expect(line2.getDirection(LineAnchor.END)).toEqual(line.getDirection(LineAnchor.END));
        expect(line2.reducedJoinPoints).toEqual(line.reducedJoinPoints);
        expect(line2.extra).toEqual(line.extra);
        expect(line2.wasMovingEdge()).toEqual(line.wasMovingEdge());
    });

    it('testSerialization_restoreAfterMovingAnchorPoints', () => {
        const startPoint = DirectedPoint.of(Direction.VERTICAL, 1, 2);
        const endPoint = DirectedPoint.of(Direction.HORIZONTAL, 3, 4);
        const line = new Line(startPoint, endPoint, undefined, PARENT_ID);

        const newStartPoint = DirectedPoint.of(Direction.HORIZONTAL, 4, 5);
        const newEndPoint = DirectedPoint.of(Direction.VERTICAL, 7, 8);
        line.moveAnchorPoint(
            { anchor: LineAnchor.START, point: newStartPoint },
            true,
            false,
        );
        line.moveAnchorPoint(
            { anchor: LineAnchor.END, point: newEndPoint },
            true,
            false,
        );

        const serializableLine = line.toSerializableShape(true) as SerializableLine;
        const line2 = Line.fromSerializable(serializableLine, PARENT_ID);
        expect(line2.getDirection(LineAnchor.START)).toEqual(line.getDirection(LineAnchor.START));
        expect(line2.getDirection(LineAnchor.END)).toEqual(line.getDirection(LineAnchor.END));
        expect(line2.reducedJoinPoints).toEqual(line.reducedJoinPoints);
        expect(line2.extra).toEqual(line.extra);
        expect(line2.wasMovingEdge()).toEqual(line.wasMovingEdge());
    });

    it('testSerialization_restoreAfterMovingEdge', () => {
        const startPoint = DirectedPoint.of(Direction.VERTICAL, 1, 2);
        const endPoint = DirectedPoint.of(Direction.HORIZONTAL, 3, 4);
        const line = new Line(startPoint, endPoint, undefined, PARENT_ID);
        line.moveEdge(line.edges[0].id, new Point(10, 10), true);

        const serializableLine = line.toSerializableShape(true) as SerializableLine;
        const line2 = Line.fromSerializable(serializableLine, PARENT_ID);
        expect(line2.getDirection(LineAnchor.START)).toEqual(line.getDirection(LineAnchor.START));
        expect(line2.getDirection(LineAnchor.END)).toEqual(line.getDirection(LineAnchor.END));
        expect(line2.reducedJoinPoints).toEqual(line.reducedJoinPoints);
        expect(line2.extra).toEqual(line.extra);
        expect(line2.wasMovingEdge()).toEqual(line.wasMovingEdge());
    });
});