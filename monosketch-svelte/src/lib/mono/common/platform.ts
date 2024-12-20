export const isCommandKeySupported = (): boolean => {
    return navigator.userAgent.includes('Mac')
};

export const isCommandKeyOn = (e: KeyboardEvent): boolean => {
    return isCommandKeySupported() ? e.metaKey : e.ctrlKey;
};
