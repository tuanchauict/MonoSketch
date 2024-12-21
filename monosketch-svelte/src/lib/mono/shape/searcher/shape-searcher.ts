/*
 * Copyright (c) 2024, tuanchauict
 */

import { Direction, type Point } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import { isTransparentChar } from "$mono/common/character";
import { MonoBitmap } from "$mono/monobitmap/bitmap/monobitmap";
import { ShapeZoneAddressManager } from "$mono/shape/searcher/shape-zone-address-manager";
import { ZoneOwnersManager } from "$mono/shape/searcher/zone-address";
import type { ShapeManager } from "$mono/shape/shape-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Rectangle } from "$mono/shape/shape/rectangle";

/**
 * A model class which optimizes shapes retrieval from a point.
 * A shape is only indexed after it's drawn onto the board. Do not use this for pre-draw check.
 */
export class ShapeSearcher {
    private shapeZoneAddressManager: ShapeZoneAddressManager;
    private zoneOwnersManager: ZoneOwnersManager;

    constructor(
        private shapeManager: ShapeManager,
        private getBitmap: (shape: AbstractShape) => MonoBitmap.Bitmap | null,
    ) {
        this.shapeZoneAddressManager = new ShapeZoneAddressManager(getBitmap);
        this.zoneOwnersManager = new ZoneOwnersManager();
    }

    register(shape: AbstractShape) {
        const addresses = this.shapeZoneAddressManager.getZoneAddresses(shape);
        this.zoneOwnersManager.registerOwnerAddresses(shape.id, addresses);
    }

    clear(bound: Rect) {
        this.zoneOwnersManager.clear(bound);
    }

    /**
     * Returns a list of shapes which have a non-transparent pixel at [point].
     * The order of the list is based on z-index which 1st item is the lowest index.
     * Note: Group shape is not included in the result.
     */
    getShapes(point: Point): AbstractShape[] {
        return this.zoneOwnersManager.getPotentialOwners(point)
            .map(ownerId => this.shapeManager.getShape(ownerId))
            .filter((shape): shape is AbstractShape => !!shape)
            .filter(shape => {
                const position = shape.bound.position;
                const bitmap = this.getBitmap(shape);
                if (!bitmap) {
                    return false;
                }
                const bitmapRow = point.row - position.row;
                const bitmapCol = point.column - position.column;
                const isTransparent = isTransparentChar(bitmap.getVisual(bitmapRow, bitmapCol));
                return !isTransparent;
            });
    }

    getAllShapesInZone(bound: Rect): AbstractShape[] {
        const zoneOwners = Array.from(this.zoneOwnersManager.getAllPotentialOwnersInZone(bound));
        return zoneOwners
            .map(ownerId => this.shapeManager.getShape(ownerId))
            .filter((shape): shape is AbstractShape => shape !== null)
            .filter(shape => shape.isOverlapped(bound));
    }

    /**
     * Gets the edge direction of a shape having bound's edges at [point].
     * The considerable shape types are shapes having static bound such as [Text], [Rectangle].
     * If there are many shapes satisfying the conditions, the first one will be used.
     */
    getEdgeDirection(point: Point): Direction | null {
        const shape = this.zoneOwnersManager.getPotentialOwners(point)
            .map(ownerId => this.shapeManager.getShape(ownerId))
            .filter((shape): shape is AbstractShape => shape !== null)
            .filter(shape => shape instanceof Text || shape instanceof Rectangle)
            .filter(shape => shape.contains(point) && !shape.isVertex(point))
            .find(shape =>
                shape.bound.left === point.left ||
                shape.bound.right === point.left ||
                shape.bound.top === point.top ||
                shape.bound.bottom === point.top,
            );

        if (!shape) {
            return null;
        }
        return shape.bound.left === point.left || shape.bound.right === point.left
            ? Direction.VERTICAL
            : Direction.HORIZONTAL;
    }
}
