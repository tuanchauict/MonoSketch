import type { Comparable } from "$libs/comparable";
import type { Point } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import type { Identifier } from "$mono/shape/collection/identifier";
import { NoExtra } from "$mono/shape/extra/shape-extra";
import type { ShapeExtra } from "$mono/shape/extra/shape-extra";
import type { AbstractSerializableShape } from "$mono/shape/serialization/shapes";
import { UUID } from "$mono/uuid";

/**
 * An [Identifier] class for shape.
 * This is used for storing a reference to a shape without keeping the real reference to the
 * instance of the shape.
 */
export class ShapeIdentifier implements Identifier {
    constructor(public id: string) {
    }
}

/**
 * An abstract class which is used for defining all kinds of shape which are supported by the app.
 * Each shape will be assigned an id which is automatically generated or manually assigned. Two
 * shapes which have the same ID will be considered identical regardless the other attributes of
 * each kind of shape class.
 *
 * Each shape's attributes might be changed and [versionCode] reflects the update. To ensure the
 * [versionCode]'s value is accurate, all properties modifying must be wrapped inside [update].
 */
export abstract class AbstractShape extends ShapeIdentifier implements Comparable {
    parentId: string | null;
    versionCode: number;

    /**
     * Extra information which is specific to each shape.
     */
    protected extraInner: ShapeExtra = NoExtra;

    /**
     * @param id with null means the id will be automatically generated.
     * @param parentId the id of the parent shape. If the shape is a top-level shape, this value is
     * null.
     */
    protected constructor(id: string | null, parentId: string | null) {
        super(id ?? UUID.generate());
        this.parentId = parentId || null;
        this.versionCode = AbstractShape.nextVersionCode();
    }

    /**
     * A simple version of the shape's identifier although a shape is already a shape identifier.
     * This is used for the case that a shape is used as a key in a map.
     */
    get identifier(): ShapeIdentifier {
        return new ShapeIdentifier(this.id);
    }

    abstract toSerializableShape(isIdIncluded: boolean): AbstractSerializableShape;

    abstract get bound(): Rect;

    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    setBound(newBound: Rect) {
        // Default implementation does nothing, can be overridden
    }

    /**
     * Set extra properties for the shape. Override this method if the shape supports extra properties.
     * @param newExtra
     * @throws Error if the shape does not support extra properties.
     */
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    setExtra(newExtra: ShapeExtra) {
        throw new Error(`${this.constructor.name} does not support extra`);
    }

    get extra(): ShapeExtra {
        return this.extraInner;
    }

    /**
     * Updates properties of the shape by [action]. The [action] returns true if the shape's
     * properties are changed.
     */
    update(action: () => boolean) {
        const isChanged = action();
        if (isChanged) {
            this.versionCode = AbstractShape.nextVersionCode(this.versionCode);
        }
    }

    contains(point: Point): boolean {
        return this.bound.contains(point);
    }

    isVertex(point: Point): boolean {
        return this.bound.isVertex(point);
    }

    isOverlapped(rect: Rect): boolean {
        return this.bound.isOverlapped(rect);
    }

    /**
     * Returns true if the shape can have connectors.
     */
    get canHaveConnectors(): boolean {
        return false;
    }

    /**
     * Returns true if the shape is the same type to [other] and has the same id and version code to the [other].
     * @param other
     */
    equals(other: unknown): boolean {
        if (!(other instanceof AbstractShape)) {
            return false;
        }
        if (this.constructor !== other.constructor) {
            return false;
        }
        return this.id === other.id && this.versionCode === other.versionCode;
    }

    /**
     * Generates a new version code which is different from [excludedValue].
     */
    static nextVersionCode(excludedValue: number = 0): number {
        let nextCode = Math.floor(Math.random() * MAX_INT);
        // The probability of a new number is equal to old number is low, therefore, this loop
        // is short.
        while (nextCode === excludedValue) {
            nextCode = Math.floor(Math.random() * MAX_INT);
        }
        return nextCode;
    }
}

const MAX_INT = 2147483647;
