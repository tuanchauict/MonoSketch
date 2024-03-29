import type { Identifier } from "$mono/shape/collection/identifier";

/**
 * A special map that allows querying by both with key and value.
 * Key is going with only 1 value and 1 value can go with multiple keys.
 * This map supports querying with key and querying with value.
 */
export class TwoWayQuickMap<K extends Identifier, V extends Identifier> {
    private keyToValueMap: Map<string, V> = new Map<string, V>();
    private valueToKeysMap: Map<string, Map<string, K>> = new Map<string, Map<string, K>>();

    set(key: K, value: V): void {
        if (this.keyToValueMap.has(key.id)) {
            this.removeKey(key);
        }
        this.keyToValueMap.set(key.id, value);
        const keysMap = this.valueToKeysMap.get(value.id) || new Map<string, K>();
        keysMap.set(key.id, key);
        this.valueToKeysMap.set(value.id, keysMap);
    }

    removeKey(key: Identifier): void {
        const value = this.keyToValueMap.get(key.id);
        if (!value) {
            return;
        }
        this.keyToValueMap.delete(key.id);
        const keysMap = this.valueToKeysMap.get(value.id);
        if (keysMap) {
            keysMap.delete(key.id);
            if (keysMap.size === 0) {
                this.valueToKeysMap.delete(value.id);
            }
        }
    }

    removeValue(value: Identifier): void {
        const keysMap = this.valueToKeysMap.get(value.id);
        if (!keysMap) {
            return;
        }
        this.valueToKeysMap.delete(value.id);
        for (const keyId of keysMap.keys()) {
            this.keyToValueMap.delete(keyId);
        }
    }

    *getKeys(value: Identifier): IterableIterator<K> {
        const keysMap = this.valueToKeysMap.get(value.id);
        if (keysMap) {
            for (const key of keysMap.values()) {
                yield key;
            }
        }
    }

    get(key: Identifier): V | undefined {
        return this.keyToValueMap.get(key.id);
    }

    getKey(keyId: Identifier): K | undefined {
        const value = this.keyToValueMap.get(keyId.id);
        if (!value) {
            return undefined;
        }
        return this.valueToKeysMap.get(value.id)?.get(keyId.id);
    }

    *asSequence(): IterableIterator<[K, V]> {
        for (const keyId of this.keyToValueMap.keys()) {
            const value = this.keyToValueMap.get(keyId);
            if (value) {
                const key = this.valueToKeysMap.get(value.id)?.get(keyId);
                if (key) {
                    yield [key, value];
                }
            }
        }
    }
}
