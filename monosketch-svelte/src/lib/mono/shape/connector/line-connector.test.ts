/*
 * Copyright (c) 2024, tuanchauict
 */

import { DirectedPoint, Direction } from "$libs/graphics-geo/point";
import { Around, ShapeConnectorUseCase } from "$mono/shape/connector/line-connector";
import { describe, it, expect } from 'vitest';
import { Rect } from '$libs/graphics-geo/rect';

type PointAroundTuple = [DirectedPoint, Around];

describe('ShapeConnectorUseCase', () => {
    it('testGetAround_notAround', () => {
        const rect = Rect.byLTRB(4, 20, 10, 30);

        const nonAroundPoints: DirectedPoint[] = [
            // Around left edge
            DirectedPoint.ofF(Direction.HORIZONTAL, 2, 25),
            DirectedPoint.ofF(Direction.HORIZONTAL, 6, 25),
            // Around right edge
            DirectedPoint.ofF(Direction.HORIZONTAL, 8, 25),
            DirectedPoint.ofF(Direction.HORIZONTAL, 12, 25),
            // Around top edge
            DirectedPoint.ofF(Direction.HORIZONTAL, 6, 18),
            DirectedPoint.ofF(Direction.HORIZONTAL, 6, 22),
            // Around bottom edge
            DirectedPoint.ofF(Direction.HORIZONTAL, 6, 28),
            DirectedPoint.ofF(Direction.HORIZONTAL, 6, 32),
            // On left edge - outside top
            DirectedPoint.ofF(Direction.HORIZONTAL, 3, 18),
            DirectedPoint.ofF(Direction.HORIZONTAL, 4, 18),
            DirectedPoint.ofF(Direction.HORIZONTAL, 5, 18),
            // On left edge - outside bottom
            DirectedPoint.ofF(Direction.HORIZONTAL, 3, 32),
            DirectedPoint.ofF(Direction.HORIZONTAL, 4, 32),
            DirectedPoint.ofF(Direction.HORIZONTAL, 5, 32),
            // On right edge - outside top
            DirectedPoint.ofF(Direction.HORIZONTAL, 9, 18),
            DirectedPoint.ofF(Direction.HORIZONTAL, 10, 18),
            DirectedPoint.ofF(Direction.HORIZONTAL, 11, 18),
            // On right edge - outside bottom
            DirectedPoint.ofF(Direction.HORIZONTAL, 9, 32),
            DirectedPoint.ofF(Direction.HORIZONTAL, 10, 32),
            DirectedPoint.ofF(Direction.HORIZONTAL, 11, 32),
            // On top edge - outside left
            DirectedPoint.ofF(Direction.HORIZONTAL, 2, 19),
            DirectedPoint.ofF(Direction.HORIZONTAL, 2, 20),
            DirectedPoint.ofF(Direction.HORIZONTAL, 2, 21),
            // On top edge - outside right
            DirectedPoint.ofF(Direction.HORIZONTAL, 12, 19),
            DirectedPoint.ofF(Direction.HORIZONTAL, 12, 20),
            DirectedPoint.ofF(Direction.HORIZONTAL, 12, 21),
            // On bottom edge - outside left
            DirectedPoint.ofF(Direction.HORIZONTAL, 2, 29),
            DirectedPoint.ofF(Direction.HORIZONTAL, 2, 30),
            DirectedPoint.ofF(Direction.HORIZONTAL, 2, 31),
            // On bottom edge - outside right
            DirectedPoint.ofF(Direction.HORIZONTAL, 12, 29),
            DirectedPoint.ofF(Direction.HORIZONTAL, 12, 30),
            DirectedPoint.ofF(Direction.HORIZONTAL, 12, 31),
        ];

        for (const point of nonAroundPoints) {
            console.log(point);
            expect(ShapeConnectorUseCase.getAround(point, rect)).toBeNull();
        }
    })

    it('testGetAround_Left', () => {
        const rect = Rect.byLTRB(4, 20, 10, 30);
        const leftPointsToExpectedAround: PointAroundTuple[] = [
            // No conflict
            [DirectedPoint.ofF(Direction.HORIZONTAL, 5, 25), Around.LEFT],
            [DirectedPoint.ofF(Direction.VERTICAL, 3, 25), Around.LEFT],
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 25), Around.LEFT],
            [DirectedPoint.ofF(Direction.VERTICAL, 5, 25), Around.LEFT],
            // Conflict
            // Horizontal, top edge -> left
            [DirectedPoint.ofF(Direction.HORIZONTAL, 3, 20), Around.LEFT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 4, 20), Around.LEFT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 5, 20), Around.LEFT],
            // Vertical, top edge -> top
            [DirectedPoint.ofF(Direction.VERTICAL, 3, 20), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 20), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 5, 20), Around.TOP],
            // Horizontal, bottom edge -> left
            [DirectedPoint.ofF(Direction.HORIZONTAL, 3, 30), Around.LEFT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 4, 30), Around.LEFT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 5, 30), Around.LEFT],
            // Vertical, bottom edge -> bottom
            [DirectedPoint.ofF(Direction.VERTICAL, 3, 30), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 30), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 5, 30), Around.BOTTOM],
        ];

        for (const [point, expected] of leftPointsToExpectedAround) {
            console.log(point);
            expect(ShapeConnectorUseCase.getAround(point, rect)).toBe(expected);
        }
    });

    it('testGetAround_Right', () => {
        const rect = Rect.byLTRB(4, 20, 10, 30);
        const rightPointsToExpectedAround: PointAroundTuple[] = [
            // No conflict
            [DirectedPoint.ofF(Direction.HORIZONTAL, 9, 25), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 25), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 11, 25), Around.RIGHT],
            [DirectedPoint.ofF(Direction.VERTICAL, 9, 25), Around.RIGHT],
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 25), Around.RIGHT],
            [DirectedPoint.ofF(Direction.VERTICAL, 11, 25), Around.RIGHT],
            // Conflict
            // Horizontal, top edge -> left
            [DirectedPoint.ofF(Direction.HORIZONTAL, 9, 20), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 20), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 11, 20), Around.RIGHT],
            // Vertical, top edge -> top
            [DirectedPoint.ofF(Direction.VERTICAL, 9, 20), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 20), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 11, 20), Around.TOP],
            // Horizontal, bottom edge -> left
            [DirectedPoint.ofF(Direction.HORIZONTAL, 9, 30), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 30), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 11, 30), Around.RIGHT],
            // Vertical, bottom edge -> bottom
            [DirectedPoint.ofF(Direction.VERTICAL, 9, 30), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 30), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 11, 30), Around.BOTTOM],
        ];

        for (const [point, expected] of rightPointsToExpectedAround) {
            console.log(point);
            expect(ShapeConnectorUseCase.getAround(point as DirectedPoint, rect)).toBe(expected);
        }
    });

    it('testGetAround_Top', () => {
        const rect = Rect.byLTRB(4, 20, 10, 30);
        const topPointsToExpectedAround: PointAroundTuple[] = [
            // No conflict
            [DirectedPoint.ofF(Direction.HORIZONTAL, 6, 19), Around.TOP],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 6, 20), Around.TOP],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 6, 21), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 6, 19), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 6, 20), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 6, 21), Around.TOP],
            // Conflict
            // Vertical, left edge -> top
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 19), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 20), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 21), Around.TOP],
            // Horizontal, left edge -> left
            [DirectedPoint.ofF(Direction.HORIZONTAL, 4, 19), Around.LEFT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 4, 20), Around.LEFT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 4, 21), Around.LEFT],
            // Vertical, right edge -> top
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 19), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 20), Around.TOP],
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 21), Around.TOP],
            // Horizontal, right edge -> right
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 19), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 20), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 21), Around.RIGHT],
        ];

        for (const [point, expected] of topPointsToExpectedAround) {
            console.log(point);
            expect(ShapeConnectorUseCase.getAround(point, rect)).toBe(expected);
        }
    });

    it('testGetAround_Bottom', () => {
        const rect = Rect.byLTRB(4, 20, 10, 30);
        const bottomPointsToExpectedAround: PointAroundTuple[] = [
            // No conflict
            [DirectedPoint.ofF(Direction.HORIZONTAL, 6, 29), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 6, 30), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 6, 31), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 6, 29), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 6, 30), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 6, 31), Around.BOTTOM],
            // Conflict
            // Vertical, left edge -> bottom
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 29), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 30), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 4, 31), Around.BOTTOM],
            // Horizontal, left edge -> left
            [DirectedPoint.ofF(Direction.HORIZONTAL, 4, 29), Around.LEFT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 4, 30), Around.LEFT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 4, 31), Around.LEFT],
            // Vertical, right edge -> bottom
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 29), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 30), Around.BOTTOM],
            [DirectedPoint.ofF(Direction.VERTICAL, 10, 31), Around.BOTTOM],
            // Horizontal, right edge -> right
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 29), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 30), Around.RIGHT],
            [DirectedPoint.ofF(Direction.HORIZONTAL, 10, 31), Around.RIGHT],
        ];

        for (const [point, expected] of bottomPointsToExpectedAround) {
            console.log(point);
            expect(ShapeConnectorUseCase.getAround(point, rect)).toBe(expected);
        }
    });

    // TODO: Add unit test for calculateRatio and calculateOffset
});