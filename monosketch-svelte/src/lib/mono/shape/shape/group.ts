/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Comparable } from "$libs/comparable";
import { Rect } from "$libs/graphics-geo/rect";
import { AddPosition, MoveActionType, QuickList } from "$mono/shape/collection/quick-list";
import {
    AbstractSerializableShape,
    SerializableGroup,
    SerializableLine,
    SerializableRectangle,
    SerializableText,
} from "$mono/shape/serialization/shapes";
import { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Line } from "$mono/shape/shape/line";
import { Rectangle } from "$mono/shape/shape/rectangle";
import { Text } from "$mono/shape/shape/text";

/**
 * A special shape which manages a collection of shapes.
 */
export class Group extends AbstractShape implements Comparable {
    private quickList: QuickList<AbstractShape> = new QuickList();
    items: Iterable<AbstractShape> = this.quickList;

    get itemCount(): number {
        return this.quickList.size;
    }

    constructor(id: string | null = null, parentId: string | null = null) {
        super(id, parentId);
    }

    get bound(): Rect {
        if (this.quickList.isEmpty()) {
            return Rect.ZERO;
        }
        let left = Infinity;
        let right = -Infinity;
        let top = Infinity;
        let bottom = -Infinity;
        for (const item of this.quickList) {
            left = Math.min(left, item.bound.left);
            right = Math.max(right, item.bound.right);
            top = Math.min(top, item.bound.top);
            bottom = Math.max(bottom, item.bound.bottom);
        }
        return Rect.byLTRB(left, top, right, bottom);
    }

    static fromSerializable(serializableGroup: SerializableGroup, parentId: string | null = null): Group {
        const group = new Group(serializableGroup.actualId, parentId);
        for (const serializableShape of serializableGroup.shapes) {
            group.addInternal(Group.toShape(group.id, serializableShape));
        }
        group.versionCode = serializableGroup.versionCode;
        return group;
    }

    toSerializableShape(isIdIncluded: boolean): SerializableGroup {
        return SerializableGroup.create({
            id: this.id,
            isIdTemporary: !isIdIncluded,
            versionCode: this.versionCode,
            shapes: this.mapItems(item => item.toSerializableShape(isIdIncluded)),
        });
    }

    add(shape: AbstractShape, position: AddPosition = AddPosition.Last): void {
        this.update(() => this.addInternal(shape, position));
    }

    private addInternal(shape: AbstractShape, position: AddPosition = AddPosition.Last): boolean {
        if (shape.parentId !== null && shape.parentId !== this.id) {
            return false;
        }
        shape.parentId = this.id;
        this.quickList.add(shape, position);
        return true;
    }

    remove(shape: AbstractShape): void {
        this.update(() => this.quickList.remove(shape) !== null);
    }

    changeOrder(shape: AbstractShape, moveActionType: MoveActionType): void {
        this.update(() => this.quickList.move(shape, moveActionType));
    }

    toString(): string {
        return `Group(${this.id})`;
    }

    static toShape(parentId: string, serializableShape: AbstractSerializableShape): AbstractShape {
        switch (serializableShape.constructor) {
            case SerializableRectangle:
                return Rectangle.fromSerializable(serializableShape as SerializableRectangle, parentId);
            case SerializableText:
                return Text.fromSerializable(serializableShape as SerializableText, parentId);
            case SerializableLine:
                return Line.fromSerializable(serializableShape as SerializableLine, parentId);
            case SerializableGroup:
                return Group.fromSerializable(serializableShape as SerializableGroup, parentId);
            default:
                throw new Error("Unknown shape type");
        }
    }

    private mapItems<T>(callback: (item: AbstractShape) => T): T[] {
        return Array.from(this.items).map(callback);
    }

    equals(other: any): boolean {
        if (this === other) {
            return true;
        }
        if (!(other instanceof Group)) {
            return false;
        }
        return this.id === other.id && this.versionCode === other.versionCode && this.parentId === other.parentId;
    }
}

/**
 * A special [Group] for the root group of the file.
 * This group contains some extra information for storing and restoring from file.
 */
export function RootGroup(id: string | null): Group;
export function RootGroup(serializableGroup: SerializableGroup): Group;

export function RootGroup(idOrSerializableGroup: string | null | SerializableGroup = null): Group {
    if (idOrSerializableGroup instanceof SerializableGroup) {
        return Group.fromSerializable(idOrSerializableGroup);
    } else {
        return new Group(idOrSerializableGroup, null);
    }
}

