import { StoreKeys, DB_VERSION } from './consts';
import type { Migration } from './migrations/interface';
import { MigrateTo2 } from './migrations/migrate2';

/**
 * An interface for observing storage change.
 */
export interface StoreObserver {
    onChange(key: string, oldValue: string | null, newValue: string | null): void;
}

/**
 * A class for managing storage.
 */
export class StoreManager {
    private static instance: StoreManager | null = null;

    public static getInstance(): StoreManager {
        if (!StoreManager.instance) {
            StoreManager.instance = new StoreManager();
        }
        return StoreManager.instance;
    }

    private keyToObserverMap: { [key: string]: StoreObserver } = {};

    private migrations: { [targetVersion: number]: Migration } = {
        2: MigrateTo2,
    };

    private constructor() {
        this.migrate();

        window.onstorage = this.onStorageChange;
    }

    public getKeys(predicate: (key: string) => boolean): string[] {
        const keys: string[] = [];
        for (let index = 0; index < localStorage.length; index++) {
            const key = localStorage.key(index);
            if (key && predicate(key)) {
                keys.push(key);
            }
        }
        return keys;
    }

    private migrate(): void {
        const currentDbVersion = parseInt(this.get(StoreKeys.DB_VERSION) ?? '1');
        for (let version = currentDbVersion + 1; version <= DB_VERSION; version++) {
            const migration = this.migrations[version];
            if (migration) {
                migration.migrate(this);
            }
        }
        this.set(StoreKeys.DB_VERSION, DB_VERSION.toString());
    }

    public set(key: string, json: string): void {
        localStorage.setItem(key, json);
    }

    public get(key: string): string | null {
        return localStorage.getItem(key);
    }

    public remove(key: string): void {
        localStorage.removeItem(key);
    }

    public setObserver(key: string, observer: StoreObserver): void {
        if (this.keyToObserverMap[key]) {
            throw new Error(`${key}'s observer is already set!`);
        }
        this.keyToObserverMap[key] = observer;
    }

    public removeObserver(key: string): void {
        delete this.keyToObserverMap[key];
    }

    private onStorageChange = (event: StorageEvent) => {
        const key = event.key;
        if (key && this.keyToObserverMap[key]) {
            this.keyToObserverMap[key].onChange(key, event.oldValue, event.newValue);
        }
    };
}

/**
 * A storage document for a specific path.
 * This helps the storage have a structure similar to a directory in the file system.
 */
export class StorageDocument {
    public static get(path: string): StorageDocument {
        return (
            StorageDocument.documents[path] ??
            (StorageDocument.documents[path] = new StorageDocument(
                path,
                StoreManager.getInstance(),
            ))
        );
    }

    private static documents: { [path: string]: StorageDocument } = {};

    private constructor(
        private readonly path: string,
        private storeManager: StoreManager,
    ) {}

    private getFullPath(key: string): string {
        return StoreKeys.getPath(this.path, key);
    }

    public childDocument(key: string): StorageDocument {
        return StorageDocument.get(this.getFullPath(key));
    }

    public get(key: string, defaultValue: string | null = null): string | null {
        return this.storeManager.get(this.getFullPath(key)) ?? defaultValue;
    }

    public set(key: string, value: string): void {
        this.storeManager.set(this.getFullPath(key), value);
    }

    public remove(key: string): void {
        this.storeManager.remove(this.getFullPath(key));
    }

    public setObserver(key: string, observer: StoreObserver): void {
        this.storeManager.setObserver(this.getFullPath(key), observer);
    }

    public removeObserver(key: string): void {
        this.storeManager.removeObserver(this.getFullPath(key));
    }

    public getKeys(predicate: (key: string) => boolean): string[] {
        return this.storeManager.getKeys(predicate);
    }
}
