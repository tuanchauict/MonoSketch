import type { Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { ShapeExtraManager } from "$mono/shape/extra/extra-manager";
import { RectangleExtra, type ShapeExtra } from "$mono/shape/extra/shape-extra";
import { SerializableRectangle } from "$mono/shape/serialization/shapes";
import { AbstractShape } from "$mono/shape/shape/abstract-shape";

/**
 * A rectangle shape.
 */
export class Rectangle extends AbstractShape {
    private boundInner: Rect;

    constructor(rect: Rect, id: string | null = null, parentId: string | null = null) {
        super(id, parentId);
        this.boundInner = rect;
        this.setExtra(RectangleExtra.create(ShapeExtraManager.defaultRectangleExtra));
    }

    static fromRect({ rect, id = null, parentId = null }: {
        rect: Rect,
        id?: string | null,
        parentId?: string | null
    }): Rectangle {
        return new Rectangle(rect, id, parentId);
    }

    // Constructor that takes startPoint and endPoint
    static fromPoints(startPoint: Point, endPoint: Point, id: string | null, parentId: string | null = null): Rectangle {
        const rect = Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top);
        return new Rectangle(rect, id, parentId);
    }

    // Constructor that takes a serializable rectangle
    static fromSerializable(serializableRectangle: SerializableRectangle, parentId?: string): Rectangle {
        const rectangle = new Rectangle(serializableRectangle.bound, serializableRectangle.actualId, parentId);
        rectangle.setExtra(RectangleExtra.fromSerializable(serializableRectangle.extra));
        rectangle.versionCode = serializableRectangle.versionCode;
        return rectangle;
    }

    get extra(): RectangleExtra {
        return this.extraInner as RectangleExtra;
    }

    setExtra(value: ShapeExtra) {
        if (!(value instanceof RectangleExtra)) {
            throw new Error('New extra is not a RectangleExtra');
        }
        if (this.extraInner.equals(value)) {
            return;
        }
        this.update(() => {
            this.extraInner = value;
            return true;
        });
    }

    get bound(): Rect {
        return this.boundInner;
    }

    setBound(newBound: Rect): void {
        this.update(() => {
            const isUpdated = this.bound !== newBound;
            this.boundInner = newBound;
            return isUpdated;
        });
    }

    toSerializableShape(isIdIncluded: boolean): SerializableRectangle {
        return new SerializableRectangle(
            isIdIncluded ? this.id : null,
            !isIdIncluded,
            this.versionCode,
            this.bound,
            this.extra.toSerializableExtra(),
        );
    }
}
