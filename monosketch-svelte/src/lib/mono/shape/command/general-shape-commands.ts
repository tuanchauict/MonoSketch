/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Rect } from "$libs/graphics-geo/rect";
import type { ShapeExtra } from "$mono/shape/extra/shape-extra";
import { type Command, ShapeManager } from "$mono/shape/shape-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { Group } from "$mono/shape/shape/group";

/**
 * A [Command] for changing the bound of a shape.
 */
export class ChangeBound implements Command {
    constructor(
        private readonly target: AbstractShape,
        private readonly newBound: Rect,
    ) {
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.target.parentId);
    }

    execute(_shapeManager: ShapeManager, parent: Group) {
        const currentVersion = this.target.versionCode;
        this.target.setBound(this.newBound);
        if (currentVersion === this.target.versionCode) {
            return;
        }

        parent.update(() => true);
    }
}

/**
 * A [Command] for changing the extra of a shape.
 */
export class ChangeExtra implements Command {
    constructor(
        private readonly target: AbstractShape,
        private readonly newExtra: ShapeExtra,
    ) {
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.target.parentId);
    }

    execute(_shapeManager: ShapeManager, parent: Group) {
        const currentVersion = this.target.versionCode;
        this.target.setExtra(this.newExtra);
        if (currentVersion !== this.target.versionCode) {
            parent.update(() => true);
        }
    }
}
