import type { Char } from '$libs/char';

// TODO: Implement the connectable characters.
const CONNECTABLE_CHARS: Set<Char> = new Set();

export const isConnectableChar = (char: string): boolean => {
    return CONNECTABLE_CHARS.has(char);
}
