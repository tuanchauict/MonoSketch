export const isCommandKeyOn = (e: KeyboardEvent): boolean  => {
    // TODO: Resolve platform deprecated.
    return navigator.platform.startsWith("Mac") ? e.metaKey : e.ctrlKey
}
