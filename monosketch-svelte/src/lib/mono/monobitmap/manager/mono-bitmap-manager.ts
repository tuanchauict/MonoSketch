/*
 * Copyright (c) 2024, tuanchauict
 */

import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Group } from "$mono/shape/shape/group";
import { Line } from "$mono/shape/shape/line";
import { MockShape } from "$mono/shape/shape/mock-shape";
import { Rectangle } from "$mono/shape/shape/rectangle";
import { Text } from "$mono/shape/shape/text";
import { LineBitmapFactory } from './line-bitmap-factory';
import { RectangleBitmapFactory } from './rectangle-bitmap-factory';
import { TextBitmapFactory } from './text-bitmap-factory';
import { MonoBitmap } from '$mono/monobitmap/bitmap/monobitmap';
import Bitmap = MonoBitmap.Bitmap;

/**
 * A model class which manages and caches bitmap of shapes.
 * Cache-hit when both id and version of the shape valid in the cache, otherwise, cache-miss.
 */
export class MonoBitmapManager {
    private idToBitmapMap: Map<string, VersionizedBitmap> = new Map();

    getBitmap(shape: AbstractShape): MonoBitmap.Bitmap | null {
        const cachedBitmap = this.getCacheBitmap(shape);
        if (cachedBitmap) {
            return cachedBitmap;
        }

        let bitmap = this.createBitmap(shape);

        if (bitmap) {
            this.idToBitmapMap.set(shape.id, { versionCode: shape.versionCode, bitmap });
        }
        return bitmap;
    }

    private createBitmap(shape: AbstractShape): Bitmap | null {
        if (shape instanceof Rectangle) {
            return RectangleBitmapFactory.toBitmap(shape.bound.size, shape.extra);
        } else if (shape instanceof Text) {
            return TextBitmapFactory.toBitmap(
                shape.bound.size,
                shape.renderableText.getRenderableText(),
                shape.extra,
                shape.isTextEditing,
            );
        } else if (shape instanceof Line) {
            return LineBitmapFactory.toBitmap(shape.reducedJoinPoints, shape.extra);
        } else if (shape instanceof Group || shape instanceof MockShape) {
            // No draw group since it changes very frequently.
            // No draw mock shape since it is only for testing.
            return null;
        } else {
            throw new Error(`Unsupported shape type: ${shape.constructor.name}`);
        }
    }

    private getCacheBitmap(shape: AbstractShape): MonoBitmap.Bitmap | null {
        const versionizedBitmap = this.idToBitmapMap.get(shape.id);
        return versionizedBitmap && versionizedBitmap.versionCode === shape.versionCode
            ? versionizedBitmap.bitmap
            : null;
    }
}

interface VersionizedBitmap {
    versionCode: number;
    bitmap: MonoBitmap.Bitmap;
}