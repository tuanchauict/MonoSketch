/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Point } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";

/**
 * A data class to identify address of a zone
 */
export interface ZoneAddress {
    readonly row: number;
    readonly column: number;
}

/**
 * A factory of [ZoneAddress].
 */
export class ZoneAddressFactory {
    private static addressMap: Map<number, Map<number, ZoneAddress>> = new Map();

    static {
        for (let left = -4; left <= 20; left++) {
            const columnMap = new Map<number, ZoneAddress>();
            for (let top = -4; top <= 20; top++) {
                columnMap.set(top, { row: top, column: left });
            }
            this.addressMap.set(left, columnMap);
        }
    }

    static toAddress(row: number, column: number): ZoneAddress {
        const rowAddressIndex = toAddressIndex(row);
        const colAddressIndex = toAddressIndex(column);
        return this.get(rowAddressIndex, colAddressIndex);
    }

    static get(rowAddressIndex: number, colAddressIndex: number): ZoneAddress {
        let columnMap = this.addressMap.get(rowAddressIndex);
        if (!columnMap) {
            columnMap = new Map<number, ZoneAddress>();
            this.addressMap.set(rowAddressIndex, columnMap);
        }
        let address = columnMap.get(colAddressIndex);
        if (!address) {
            address = { row: rowAddressIndex, column: colAddressIndex };
            columnMap.set(colAddressIndex, address);
        }
        return address;
    }
}

/**
 * A class to store owners of a zone whose size = 8x8 to fast identify potential candidate
 * owners of a position (row, column).
 * Owners of a zone will be stored in the same order as owners are added if overlapped.
 */
export class ZoneOwnersManager {
    private zoneToOwnersMap: Map<ZoneAddress, string[]> = new Map();

    clear(clearBound: Rect): void {
        const leftIndex = toAddressIndex(clearBound.left);
        const rightIndex = toAddressIndex(clearBound.right);
        const topIndex = toAddressIndex(clearBound.top);
        const bottomIndex = toAddressIndex(clearBound.bottom);

        for (let rowIndex = topIndex; rowIndex <= bottomIndex; rowIndex++) {
            for (let colIndex = leftIndex; colIndex <= rightIndex; colIndex++) {
                const zone = ZoneAddressFactory.get(rowIndex, colIndex);
                this.zoneToOwnersMap.get(zone)?.splice(0);
            }
        }
    }

    registerOwnerAddresses(ownerId: string, addresses: Set<ZoneAddress>): void {
        for (const address of addresses) {
            if (!this.zoneToOwnersMap.has(address)) {
                this.zoneToOwnersMap.set(address, []);
            }
            this.zoneToOwnersMap.get(address)!.push(ownerId);
        }
    }

    getPotentialOwners(point: Point): string[] {
        const address = ZoneAddressFactory.toAddress(point.row, point.column);
        return this.zoneToOwnersMap.get(address) || [];
    }

    getAllPotentialOwnersInZone(zone: Rect): Set<string> {
        const address1 = ZoneAddressFactory.toAddress(zone.top, zone.left);
        const address2 = ZoneAddressFactory.toAddress(zone.bottom, zone.right);
        const owners = new Set<string>();

        for (let addressRow = address1.row; addressRow <= address2.row; addressRow++) {
            for (let addressCol = address1.column; addressCol <= address2.column; addressCol++) {
                const address = ZoneAddressFactory.get(addressRow, addressCol);
                const zoneOwners = this.zoneToOwnersMap.get(address) || [];
                zoneOwners.forEach(owner => owners.add(owner));
            }
        }

        return owners;
    }
}

const toAddressIndex = (number: number): number => number >> 4;
