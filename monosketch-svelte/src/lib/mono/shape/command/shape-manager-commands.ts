/*
 * Copyright (c) 2024, tuanchauict
 */

import { AddPosition, MoveActionType } from "$mono/shape/collection/quick-list";
import { type Command, ShapeManager } from "$mono/shape/shape-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Group } from "$mono/shape/shape/group";

/**
 * A [Command] for adding new shape into [ShapeManager].
 */
export class AddShape implements Command {
    constructor(private readonly shape: AbstractShape) {
        this.shape = shape;
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.shape.parentId);
    }

    execute(shapeManager: ShapeManager, parent: Group) {
        parent.add(this.shape);
        this.shape.parentId = parent.id;
        shapeManager.register(this.shape);
    }
}

/**
 * A [Command] for removing a shape from [ShapeManager].
 */
export class RemoveShape implements Command {
    constructor(private readonly shape: AbstractShape) {
        this.shape = shape;
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.shape.parentId);
    }

    execute(shapeManager: ShapeManager, parent: Group) {
        parent.remove(this.shape);
        shapeManager.unregister(this.shape);

        if (parent === shapeManager.root) {
            return;
        }
        switch (parent.itemCount) {
            case 1:
                shapeManager.execute(new Ungroup(parent));
                break;
            case 0:
                shapeManager.execute(new RemoveShape(parent));
                break;
        }
    }
}

/**
 * A [Command] for grouping shapes.
 */
export class GroupShapes implements Command {
    constructor(private readonly sameParentShapes: AbstractShape[]) {
        this.sameParentShapes = sameParentShapes;
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        if (this.sameParentShapes.length < 2) {
            return null;
        }
        const parentId = this.sameParentShapes[0].parentId;
        if (this.sameParentShapes.some(shape => shape.parentId !== parentId)) {
            return null;
        }
        return shapeManager.getGroup(parentId);
    }

    execute(shapeManager: ShapeManager, parent: Group) {
        const group = Group.create({ parentId: parent.id });
        parent.add(group, AddPosition.After(this.sameParentShapes[this.sameParentShapes.length - 1]));
        shapeManager.register(group);

        for (const shape of this.sameParentShapes) {
            parent.remove(shape);
            shape.parentId = group.id;
            group.add(shape);
        }
    }
}

/**
 * A [Command] for decomposing a [Group].
 */
export class Ungroup implements Command {
    constructor(private readonly group: Group) {
        this.group = group;
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.group.parentId);
    }

    execute(shapeManager: ShapeManager, parent: Group) {
        const items = this.group.itemArray.reverse();

        for (const shape of items) {
            this.group.remove(shape);
            shape.parentId = null;
            parent.add(shape, AddPosition.After(this.group));
        }
        shapeManager.execute(new RemoveShape(this.group));
    }
}

/**
 * A [Command] for changing order of a shape.
 */
export class ChangeOrder implements Command {

    constructor(private readonly shape: AbstractShape, private readonly changeOrderType: ChangeOrderType) {
        this.shape = shape;
        this.changeOrderType = changeOrderType;
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.shape.parentId);
    }

    execute(_shapeManager: ShapeManager, parent: Group) {
        parent.changeOrder(this.shape, ChangeOrderTypeToMoveActionType[this.changeOrderType]);
    }
}

export enum ChangeOrderType {
    FORWARD,
    BACKWARD,
    FRONT,
    BACK,
}

const ChangeOrderTypeToMoveActionType: Record<ChangeOrderType, MoveActionType> = {
    [ChangeOrderType.FORWARD]: MoveActionType.DOWN,
    [ChangeOrderType.BACKWARD]: MoveActionType.UP,
    [ChangeOrderType.FRONT]: MoveActionType.BOTTOM,
    [ChangeOrderType.BACK]: MoveActionType.TOP,
};
