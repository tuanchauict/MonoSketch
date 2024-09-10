// Base class for all serializable shapes
import { Point } from "$libs/graphics-geo/point";
import type { DirectedPoint } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";

/**
 * An abstract class for all serializable shapes.
 */
export abstract class AbstractSerializableShape {
    /**
     * The id of this shape.
     * If this is null, the shape does not have id and will be assigned a new id when created.
     * This value is only for serialization and reference purpose, do not read this value directly
     * when creating a shape, instead, use [actualId].
     */
    abstract id: string | null;

    /**
     * A flag indicates that, if this value is true, even if the [id] is not null, the id is still
     * unavailable. This is similar to when id is null but the temporary id is used for function
     * like copy-paste.
     */
    abstract isIdTemporary: boolean;

    abstract versionCode: number;

    get actualId(): string | null {
        return this.id ?? null;
    }
}

// TODO: Define serialize name for each class

/**
 * A serializable class for a rectangle shape.
 */
export class SerializableRectangle extends AbstractSerializableShape {
    constructor(
        public id: string | null,
        public isIdTemporary: boolean,
        public versionCode: number,
        public bound: Rect,
        public extra: SerializableRectExtra,
    ) {
        super();
    }
}

/**
 * A serializable class for extra properties of a rectangle shape.
 */
export class SerializableRectExtra {
    constructor(
        public isFillEnabled: boolean,
        public userSelectedFillStyleId: string,
        public isBorderEnabled: boolean,
        public userSelectedBorderStyleId: string,
        public dashPattern: string,
        public corner: string,
    ) {
    }
}

/**
 * A serializable class for a text shape.
 */
export class SerializableText extends AbstractSerializableShape {
    constructor(
        public id: string | null,
        public isIdTemporary: boolean,
        public versionCode: number,
        public bound: Rect,
        public text: string,
        public extra: SerializableTextExtra,
        public isTextEditable: boolean,
    ) {
        super();
    }
}

/**
 * A serializable class for extra properties of a text shape.
 */
export class SerializableTextExtra {
    constructor(
        public boundExtra: SerializableRectExtra,
        public textHorizontalAlign: number,
        public textVerticalAlign: number,
    ) {
    }
}

/**
 * A serializable class for a line shape.
 */
export class SerializableLine extends AbstractSerializableShape {
    constructor(
        public id: string | null,
        public isIdTemporary: boolean,
        public versionCode: number,
        public startPoint: DirectedPoint,
        public endPoint: DirectedPoint,
        public jointPoints: Point[],
        public extra: SerializableLineExtra,
        public wasMovingEdge: boolean,
    ) {
        super();
    }
}

/**
 * A serializable class for extra properties of a line shape.
 */
export class SerializableLineExtra {
    constructor(
        public isStrokeEnabled: boolean = true,
        public userSelectedStrokeStyleId: string,
        public isStartAnchorEnabled: boolean = false,
        public userSelectedStartAnchorId: string,
        public isEndAnchorEnabled: boolean = false,
        public userSelectedEndAnchorId: string,
        public dashPattern: string,
        public isRoundedCorner: boolean = false,
    ) {
    }
}

/**
 * A serializable class for a group shape.
 */
export class SerializableGroup extends AbstractSerializableShape {
    constructor(
        public id: string | null,
        public isIdTemporary: boolean,
        public versionCode: number,
        public shapes: AbstractSerializableShape[],
    ) {
        super();
    }
}
