export const DB_VERSION = 2;

/**
 * An object to gather all store keys to avoid conflict.
 */
export const StoreKeys = {
    DB_VERSION: 'DB_VERSION',

    SETTINGS: 'settings',
    THEME_MODE: 'theme-mode',
    FONT_SIZE: 'font-size',

    WORKSPACE: 'workspace',
    LAST_MODIFIED_PROJECT_ID: 'last-open', // last opened object id
    LAST_MODIFIED_TIME: 'last-modified', // last modified timestamp of the workspace

    OBJECT_NAME: 'name',
    OBJECT_CONTENT: 'content',
    OBJECT_CONNECTORS: 'connectors',
    OBJECT_OFFSET: 'offset',
    OBJECT_LAST_MODIFIED: 'last-modified',
    OBJECT_LAST_OPENED: 'last-opened',

    PATH_SEPARATOR: '/',

    getPath(parent: string, key: string): string {
        return parent ? `${parent}${this.PATH_SEPARATOR}${key}` : key;
    },
};
