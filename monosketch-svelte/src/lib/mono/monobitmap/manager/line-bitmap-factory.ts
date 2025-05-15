/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Point } from "$libs/graphics-geo/point";
import { zip } from "$libs/sequence";
import { TRANSPARENT_CHAR } from "$mono/common/character";
import { MonoBitmap } from "$mono/monobitmap/bitmap/monobitmap";
import { PointChar } from "$mono/monobitmap/manager/point-char";
import { PredefinedStraightStrokeStyle } from "$mono/shape/extra/predefined-styles";
import type { LineExtra } from "$mono/shape/extra/shape-extra";
import { AnchorChar, type StraightStrokeStyle } from "$mono/shape/extra/style";

/**
 * A factory class to create a bitmap for a line shape.
 */
export class LineBitmapFactory {
    static toBitmap(jointPoints: Point[], lineExtra: LineExtra): MonoBitmap.Bitmap {
        const bitmapBuilder = BitmapBuilderDecoration.getInstance(jointPoints);

        const dashPattern = lineExtra.dashPattern;
        const strokeStyle = lineExtra.strokeStyle ?? PredefinedStraightStrokeStyle.NO_STROKE;
        this.createCharPoints(jointPoints, strokeStyle).forEach((pointChar, index) => {
            const visualChar = dashPattern.isGap(index) ? ' ' : pointChar.char;
            bitmapBuilder.put(pointChar.top, pointChar.left, visualChar, pointChar.char);
        });

        const startAnchor = lineExtra.startAnchor;
        if (startAnchor) {
            bitmapBuilder.putAnchorPoint(jointPoints[0], jointPoints[1], startAnchor);
        }
        const endAnchor = lineExtra.endAnchor;
        if (endAnchor) {
            bitmapBuilder.putAnchorPoint(jointPoints[jointPoints.length - 1], jointPoints[jointPoints.length - 2], endAnchor);
        }

        return bitmapBuilder.toBitmap();
    }

    private static createCharPoints(jointPoints: Point[], strokeStyle: StraightStrokeStyle): PointChar[] {
        if (jointPoints.length < 2) {
            return [];
        }
        const lines = Array.from(zip(jointPoints, jointPoints.slice(1)));

        const firstPoint = this.createPointChar(lines[0][0], lines[0][1], strokeStyle);

        const linePairs = Array.from(zip(lines, lines.slice(1)));
        const charPoints = linePairs.flatMap(([[p0, p1], [_, p2]]) => {
            const line = this.createLineChar(p0, p1, strokeStyle);
            const connectChar = this.getRightAngleChar(strokeStyle, p0, p1, p2);
            const connectPoint = new PointChar(p1.left, p1.top, connectChar);
            return [...line, connectPoint];
        });

        const lastLine = lines[lines.length - 1];
        const lastLinePoint = this.createLineChar(lastLine[0], lastLine[1], strokeStyle);
        const lastPoint = this.createPointChar(lastLine[1], lastLine[0], strokeStyle);

        return [firstPoint, ...charPoints, ...lastLinePoint, lastPoint];
    }

    private static createLineChar(p0: Point, p1: Point, strokeStyle: StraightStrokeStyle): PointChar[] {
        return isHorizontal(p0, p1)
            ? PointChar.horizontalLine(p0.left, p1.left, p0.top, strokeStyle.horizontal)
            : PointChar.verticalLine(p0.left, p0.top, p1.top, strokeStyle.vertical);
    }

    private static createPointChar(p0: Point, p1: Point, strokeStyle: StraightStrokeStyle): PointChar {
        const char = isHorizontal(p0, p1) ? strokeStyle.horizontal : strokeStyle.vertical;
        return new PointChar(p0.left, p0.top, char);
    }


    private static getRightAngleChar(
        strokeStyle: StraightStrokeStyle,
        point0: Point,
        point1: Point,
        point2: Point,
    ): string {
        const isHorizontal0 = isHorizontal(point0, point1);
        const isHorizontal1 = isHorizontal(point1, point2);
        if (isHorizontal0 === isHorizontal1) {
            // Same line
            return isHorizontal0 ? strokeStyle.horizontal : strokeStyle.vertical;
        }

        const isLeft = point0.left < point1.left || point2.left < point1.left;
        const isUpper = point0.top < point1.top || point2.top < point1.top;

        if (isLeft) {
            return isUpper ? strokeStyle.upLeft : strokeStyle.downLeft;
        } else {
            return isUpper ? strokeStyle.downRight : strokeStyle.upRight;
        }
    }
}

class BitmapBuilderDecoration {
    private builder: MonoBitmap.Builder;

    private constructor(private row0: number, private column0: number, width: number, height: number) {
        this.builder = new MonoBitmap.Builder(width, height);
    }

    static getInstance(jointPoints: Point[]): BitmapBuilderDecoration {
        const boundLeft = Math.min(...jointPoints.map(p => p.left));
        const boundRight = Math.max(...jointPoints.map(p => p.left));
        const boundTop = Math.min(...jointPoints.map(p => p.top));
        const boundBottom = Math.max(...jointPoints.map(p => p.top));

        const boundWidth = boundRight - boundLeft + 1;
        const boundHeight = boundBottom - boundTop + 1;

        return new BitmapBuilderDecoration(boundTop, boundLeft, boundWidth, boundHeight);
    }

    put(row: number, column: number, visualChar: string, directionChar: string) {
        this.builder.put(row - this.row0, column - this.column0, visualChar, directionChar);
    }

    putAnchorPoint(anchor: Point, previousPoint: Point, anchorChar: AnchorChar) {
        const char = isHorizontal(anchor, previousPoint)
            ? (anchor.left < previousPoint.left ? anchorChar.left : anchorChar.right)
            : (anchor.top < previousPoint.top ? anchorChar.top : anchorChar.bottom);
        this.put(anchor.top, anchor.left, char, TRANSPARENT_CHAR);
    }

    toBitmap(): MonoBitmap.Bitmap {
        return this.builder.toBitmap();
    }
}

function isHorizontal(point1: Point, point2: Point): boolean {
    return point1.top === point2.top;
}
