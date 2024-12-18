/*
 * Copyright (c) 2024, tuanchauict
 */

import { MonoBitmap } from "$mono/monobitmap/bitmap/monobitmap";
import type { AbstractShape } from '$mono/shape/shape/abstract-shape';
import { ZoneAddressFactory, type ZoneAddress } from './zone-address';

interface VersionizedZoneAddresses {
    version: number;
    addresses: Set<ZoneAddress>;
}

/**
 * A class to convert a shape to addresses which the shape have pixels on.
 * This class also cache the addresses belong to the shape along with its version.
 */
export class ShapeZoneAddressManager {
    private idToZoneAddressMap: Map<string, VersionizedZoneAddresses> = new Map();

    constructor(private getBitmap: (shape: AbstractShape) => MonoBitmap.Bitmap | null) {
    }

    getZoneAddresses(shape: AbstractShape): Set<ZoneAddress> {
        const cachedAddresses = this.getCachedAddresses(shape);
        if (cachedAddresses) {
            return cachedAddresses;
        }

        const bitmap = this.getBitmap(shape);
        if (!bitmap) {
            this.idToZoneAddressMap.delete(shape.id);
            return new Set();
        }

        const position = shape.bound.position;
        const addresses = new Set<ZoneAddress>();
        for (let ir = 0; ir < bitmap.matrix.length; ir++) {
            for (let {index} of bitmap.matrix[ir].asSequence()) {
                const row = ir + position.row;
                const col = index + position.column;
                const address = ZoneAddressFactory.toAddress(row, col);
                addresses.add(address);
            }
        }

        const versionizedZoneAddresses = {version: shape.versionCode, addresses};
        this.idToZoneAddressMap.set(shape.id, versionizedZoneAddresses);
        return versionizedZoneAddresses.addresses;
    }

    private getCachedAddresses(shape: AbstractShape): Set<ZoneAddress> | null {
        const cached = this.idToZoneAddressMap.get(shape.id);
        return cached && cached.version === shape.versionCode ? cached.addresses : null;
    }
}
