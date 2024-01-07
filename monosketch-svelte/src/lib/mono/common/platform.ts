export const isCommandKeySupported = (): boolean => {
    // TODO: Resolve platform deprecated.
    return navigator.platform.startsWith('Mac');
};

export const isCommandKeyOn = (e: KeyboardEvent): boolean => {
    return isCommandKeySupported() ? e.metaKey : e.ctrlKey;
};
