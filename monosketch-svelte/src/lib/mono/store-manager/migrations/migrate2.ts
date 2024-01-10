import type { Migration } from './interface';
import type { StoreManager } from '../store-manager';
import { StoreKeys } from '../consts';

export const MigrateTo2: Migration = {
    targetVersion: 2,
    migrate: (storageManager: StoreManager): void => {
        migrateSettings(storageManager);
        migrateWorkspace(storageManager);
    },
};

function migrateSettings(storageManager: StoreManager): void {
    console.dir('migrate settings');
    // Theme mode
    migrateAndRemove(
        storageManager,
        'local-theme-mode',
        StoreKeys.getPath(StoreKeys.SETTINGS, StoreKeys.THEME_MODE),
        'DARK',
    );
}

function migrateWorkspace(storageManager: StoreManager): void {
    const lastOpenId = storageManager.get('last-open');
    storageManager.remove('last-open');

    const backupShapeKeyPrefix = 'backup-shapes:';
    const shapeIds = storageManager
        .getKeys((key) => key.startsWith(backupShapeKeyPrefix))
        .map((key) => key.substring(backupShapeKeyPrefix.length));
    for (const id of shapeIds) {
        const objectKey = StoreKeys.getPath(StoreKeys.WORKSPACE, id);

        migrateAndRemove(
            storageManager,
            `${backupShapeKeyPrefix}${id}`,
            StoreKeys.getPath(objectKey, StoreKeys.OBJECT_CONTENT),
        );

        migrateAndRemove(
            storageManager,
            `offset:${id}`,
            StoreKeys.getPath(objectKey, StoreKeys.OBJECT_OFFSET),
            '0|0',
        );

        migrateAndRemove(
            storageManager,
            `name:${id}`,
            StoreKeys.getPath(objectKey, StoreKeys.OBJECT_NAME),
            'Undefined',
        );

        migrateAndRemove(
            storageManager,
            `last-modified:${id}`,
            StoreKeys.getPath(objectKey, StoreKeys.OBJECT_LAST_MODIFIED),
            '0',
        );

        migrateAndRemove(
            storageManager,
            `last-opened:${id}`,
            StoreKeys.getPath(objectKey, StoreKeys.OBJECT_LAST_OPENED),
            id === lastOpenId ? '1' : '0',
        );
    }
}

function migrateAndRemove(
    storageManager: StoreManager,
    oldKey: string,
    newKey: string,
    defaultValue?: string,
): void {
    const currentContent = storageManager.get(oldKey) ?? defaultValue;
    if (currentContent) {
        storageManager.set(newKey, currentContent);
    }
    storageManager.remove(oldKey);
}
