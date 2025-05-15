// Base class for all serializable shapes
import { Point } from "$libs/graphics-geo/point";
import { DirectedPoint } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import {
    SerializableLineExtra,
    SerializableRectExtra,
    SerializableTextExtra,
} from "$mono/shape/serialization/extras";
import { Jsonizable, Serializer, SerialName } from "$mono/shape/serialization/serializable";
import { DirectedPointSerializer, PointArraySerializer, RectSerializer } from "$mono/shape/serialization/serializers";

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

    /**
     * A hint for type selection when serializing and deserializing.
     */
    abstract type: string;

    /**
     * The id of this shape after adjusting the [isIdTemporary] flag.
     */
    get actualId(): string | null {
        return !this.isIdTemporary ? (this.id ?? null) : null;
    }

    protected constructor() {
    }
}

/**
 * A serializable class for a rectangle shape.
 */
@Jsonizable
export class SerializableRectangle extends AbstractSerializableShape {
    @SerialName("type")
    public type: string = "R";

    @SerialName("i")
    public id: string | null = null;
    @SerialName("idtemp")
    public isIdTemporary: boolean = false;
    @SerialName("v")
    public versionCode: number = 0;
    @SerialName("b")
    @Serializer(RectSerializer)
    public bound: Rect = Rect.ZERO;
    @SerialName("e")
    public extra: SerializableRectExtra = SerializableRectExtra.EMPTY;

    private constructor() {
        super();
    }

    static EMPTY: SerializableRectangle = new SerializableRectangle();

    static create(
        {
            id,
            isIdTemporary,
            versionCode,
            bound,
            extra,
        }: {
            id: string | null;
            isIdTemporary: boolean;
            versionCode: number;
            bound: Rect;
            extra: SerializableRectExtra;
        },
    ): SerializableRectangle {
        const result = new SerializableRectangle();
        result.id = id;
        result.isIdTemporary = isIdTemporary;
        result.versionCode = versionCode;
        result.bound = bound;
        result.extra = extra;

        return result;
    }
}

/**
 * A serializable class for a text shape.
 */
@Jsonizable
export class SerializableText extends AbstractSerializableShape {
    @SerialName("type")
    public type: string = "T";

    @SerialName("i")
    public id: string | null = null;
    @SerialName("idtemp")
    public isIdTemporary: boolean = false;
    @SerialName("v")
    public versionCode: number = 0;
    @SerialName("b")
    @Serializer(RectSerializer)
    public bound: Rect = Rect.ZERO;
    @SerialName("t")
    public text: string = "";
    @SerialName("e")
    public extra: SerializableTextExtra = SerializableTextExtra.EMPTY;
    @SerialName("te")
    public isTextEditable: boolean = false;

    private constructor() {
        super();
    }

    static create(
        {
            id,
            isIdTemporary,
            versionCode,
            bound,
            text,
            extra,
            isTextEditable,
        }: {
            id: string | null;
            isIdTemporary: boolean;
            versionCode: number;
            bound: Rect;
            text: string;
            extra: SerializableTextExtra;
            isTextEditable: boolean;
        },
    ): SerializableText {
        const result = new SerializableText();
        result.id = id;
        result.isIdTemporary = isIdTemporary;
        result.versionCode = versionCode;
        result.bound = bound;
        result.text = text;
        result.extra = extra;
        result.isTextEditable = isTextEditable;

        return result;
    }
}


/**
 * A serializable class for a line shape.
 */
@Jsonizable
export class SerializableLine extends AbstractSerializableShape {
    @SerialName("type")
    public type: string = "L";

    @SerialName("i")
    public id: string | null = null;
    @SerialName("idtemp")
    public isIdTemporary: boolean = false;
    @SerialName("v")
    public versionCode: number = 0;
    @SerialName("ps")
    @Serializer(DirectedPointSerializer)
    public startPoint: DirectedPoint = DirectedPoint.ZERO;
    @SerialName("pe")
    @Serializer(DirectedPointSerializer)
    public endPoint: DirectedPoint = DirectedPoint.ZERO;
    @SerialName("jps")
    @Serializer(PointArraySerializer)
    public jointPoints: Point[] = [];
    @SerialName("e")
    public extra: SerializableLineExtra = SerializableLineExtra.EMPTY;
    @SerialName("em")
    public wasMovingEdge: boolean = true;

    private constructor() {
        super();
    }

    static create(
        {
            id,
            isIdTemporary,
            versionCode,
            startPoint,
            endPoint,
            jointPoints,
            extra,
            wasMovingEdge,
        }: {
            id: string | null;
            isIdTemporary: boolean;
            versionCode: number;
            startPoint: DirectedPoint;
            endPoint: DirectedPoint;
            jointPoints: Point[];
            extra: SerializableLineExtra;
            wasMovingEdge: boolean;
        },
    ): SerializableLine {
        const result = new SerializableLine();
        result.id = id;
        result.isIdTemporary = isIdTemporary;
        result.versionCode = versionCode;
        result.startPoint = startPoint;
        result.endPoint = endPoint;
        result.jointPoints = jointPoints;
        result.extra = extra;
        result.wasMovingEdge = wasMovingEdge;

        return result;
    }
}

/* eslint-disable @typescript-eslint/no-explicit-any */
export const ShapeArraySerializer = {
    serialize: (value: AbstractSerializableShape[]): any[] => {
        // @ts-expect-error toJson is attached by Jsonizable
        return value.map((shape) => shape.toJson());
    },

    deserialize: (value: any[]): AbstractSerializableShape[] => {
        return value.map(deserializeShape);
    },
};

export function deserializeShape(shape: any): AbstractSerializableShape {
    const type = shape["type"];
    switch (type) {
        case "R":
            // @ts-expect-error fromJson is attached by Jsonizable
            return SerializableRectangle.fromJson(shape);
        case "T":
            // @ts-expect-error fromJson is attached by Jsonizable
            return SerializableText.fromJson(shape);
        case "L":
            // @ts-expect-error fromJson is attached by Jsonizable
            return SerializableLine.fromJson(shape);
        case "G":
            // @ts-expect-error fromJson is attached by Jsonizable
            return SerializableGroup.fromJson(shape);
        default:
            throw new Error(`Unrecognizable type ${type}`);
    }
}

/* eslint-enable @typescript-eslint/no-explicit-any */

/**
 * A serializable class for a group shape.
 */
@Jsonizable
export class SerializableGroup extends AbstractSerializableShape {
    @SerialName("type")
    public type: string = "G";

    @SerialName("i")
    public id: string | null = null;
    @SerialName("idtemp")
    public isIdTemporary: boolean = false;
    @SerialName("v")
    public versionCode: number = 0;
    @SerialName("ss")
    @Serializer(ShapeArraySerializer)
    public shapes: AbstractSerializableShape[] = [];

    private constructor() {
        super();
    }

    copy({ isIdTemporary = this.isIdTemporary, }: { isIdTemporary: boolean; }): SerializableGroup {
        return SerializableGroup.create({
            id: this.id,
            isIdTemporary,
            versionCode: this.versionCode,
            shapes: this.shapes,
        });
    }

    static EMPTY: SerializableGroup = new SerializableGroup();

    static create(
        {
            id,
            isIdTemporary,
            versionCode,
            shapes,
        }: {
            id: string | null;
            isIdTemporary: boolean;
            versionCode: number;
            shapes: AbstractSerializableShape[];
        },
    ): SerializableGroup {
        const result = new SerializableGroup();
        result.id = id;
        result.isIdTemporary = isIdTemporary;
        result.versionCode = versionCode;
        result.shapes = shapes;

        return result;
    }
}
