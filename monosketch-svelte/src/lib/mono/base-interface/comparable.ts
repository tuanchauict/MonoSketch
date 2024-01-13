/**
 * Interface for objects that can be compared for equality.
 */
export interface Comparable {
    equals(other: unknown): boolean;
}
