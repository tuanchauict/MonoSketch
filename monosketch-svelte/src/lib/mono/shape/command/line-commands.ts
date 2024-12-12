/*
 * Copyright (c) 2024, tuanchauict
 */


import type { Point } from "$libs/graphics-geo/point";
import { type Command, ShapeManager } from "$mono/shape/shape-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { Group } from "$mono/shape/shape/group";
import type { Line } from "$mono/shape/shape/line";
import type { LineAnchorPointUpdate } from "$mono/shape/shape/linehelper";

/**
 * A [Command] for changing Line shape's Anchors.
 *
 * @param isUpdateConfirmed The flag for running Line's points reduction. If this is true, merging
 * same line points process will be conducted.
 */
export class MoveLineAnchor implements Command {
    constructor(
        private readonly target: Line,
        private readonly anchorPointUpdate: LineAnchorPointUpdate,
        private readonly isUpdateConfirmed: boolean,
        private readonly justMoveAnchor: boolean,
        private readonly connectShape: AbstractShape | null,
    ) {
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.target.parentId);
    }

    execute(shapeManager: ShapeManager, parent: Group) {
        const currentVersion = this.target.versionCode;
        this.target.moveAnchorPoint(
            this.anchorPointUpdate,
            this.isUpdateConfirmed,
            this.justMoveAnchor,
        );
        if (currentVersion === this.target.versionCode) {
            return;
        }

        if (this.connectShape) {
            shapeManager.shapeConnector.addConnector(this.target, this.anchorPointUpdate.anchor, this.connectShape);
        } else {
            shapeManager.shapeConnector.removeConnector(this.target, this.anchorPointUpdate.anchor);
        }
        parent.update(() => true);
    }
}

/**
 * A [Command] for updating Line shape's edges.
 *
 * @param isUpdateConfirmed The flag for running Line's points reduction. If this is true, merging
 *  * same line points process will be conducted.
 */
export class MoveLineEdge implements Command {
    constructor(
        private readonly target: Line,
        private readonly edgeId: number,
        private readonly point: Point,
        private readonly isUpdateConfirmed: boolean,
    ) {
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.target.parentId);
    }

    execute(shapeManager: ShapeManager, parent: Group) {
        const currentVersion = this.target.versionCode;
        this.target.moveEdge(this.edgeId, this.point, this.isUpdateConfirmed);
        parent.update(() => currentVersion !== this.target.versionCode);
    }
}
