import type { Identifier } from "$mono/shape/collection/identifier";
import { beforeEach, describe, expect, test } from "vitest";
import { TwoWayQuickMap } from './two-way-quick-map'; // Adjust the import path as necessary

class Key implements Identifier {
    id: string;

    constructor(id: string) {
        this.id = id;
    }
}

class Value implements Identifier {
    id: string;

    constructor(id: string) {
        this.id = id;
    }
}

const K1 = new Key('k1');
const K2 = new Key('k2');
const K3 = new Key('k3');
const V1 = new Value('v1');
const V2 = new Value('v2');

describe('TwoWayQuickMap', () => {
    let target: TwoWayQuickMap<Key, Value>;

    beforeEach(() => {
        target = new TwoWayQuickMap<Key, Value>();
    });

    test('add', () => {
        target.set(K1, V1);
        expect(target.get(new Key('k1'))).toEqual(V1);
        expect(Array.from(target.getKeys(new Value('v1')))).toEqual([K1]);
        expect(target.get(K2)).toBeUndefined();
        expect(Array.from(target.getKeys(new Value('v2')))).toEqual([]);

        target.set(K2, V1);
        expect(target.get(new Key('k1'))).toEqual(V1);
        expect(target.get(new Key('k2'))).toEqual(V1);
        expect(Array.from(target.getKeys(new Value('v1')))).toEqual([K1, K2]);
    });

    test('add - conflict', () => {
        target.set(K1, V1);
        target.set(K1, V1);
        target.set(K2, V1);
        target.set(K3, V2);

        target.set(K1, V2);
        expect(target.get(K1)).toEqual(V2);
        expect(target.get(K2)).toEqual(V1);
        expect(target.get(K3)).toEqual(V2);

        expect(Array.from(target.getKeys(V2))).toEqual([K3, K1]);
        expect(Array.from(target.getKeys(V1))).toEqual([K2]);
    });

    test('removeKey', () => {
        target.removeKey(K1);

        target.set(K1, V1);
        target.set(K2, V2);
        target.removeKey(K1);
        expect(target.get(K1)).toBeUndefined();
        expect(Array.from(target.getKeys(V1))).toEqual([]);
        expect(target.get(K2)).toEqual(V2);
    });

    test('removeValue', () => {
        target.removeValue(V1);

        target.set(K1, V1);
        target.set(K2, V1);
        target.set(K3, V2);

        target.removeValue(V1);
        expect(Array.from(target.getKeys(V1))).toEqual([]);
        expect(target.get(K1)).toBeUndefined();
        expect(target.get(K2)).toBeUndefined();
        expect(target.get(K3)).toEqual(V2);
        expect(Array.from(target.getKeys(V2))).toEqual([K3]);
    });

    test('getKey', () => {
        target.set(K1, V1);

        expect(target.getKey(new Key('k1'))).toEqual(K1);
    });
});
