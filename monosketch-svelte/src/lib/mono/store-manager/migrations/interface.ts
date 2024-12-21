import type { StoreManager } from '../store-manager';

/**
 * An interface for migrating local storage into [targetVersion]
 */
export abstract class Migration {
    public targetVersion: number;

    protected constructor(targetVersion: number) {
        this.targetVersion = targetVersion;
    }

    /**
     * Migrates storage to [targetVersion].
     *
     * Note: Inside [migrate], only use [storageManager] for get/set values
     */
    public abstract migrate(storageManager: StoreManager): void;
}
