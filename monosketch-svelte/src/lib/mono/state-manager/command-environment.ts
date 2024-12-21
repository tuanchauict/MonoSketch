/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import { Direction, type Point } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import type { InteractionPoint } from "$mono/shape/interaction-bound";
import { type FocusingShape, ShapeFocusType } from "$mono/shape/selected-shape-manager";
import { type Command, ShapeManager } from '$mono/shape/shape-manager';
import { ShapeConnector } from '$mono/shape/connector/shape-connector';
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { Group } from "$mono/shape/shape/group";

/**
 * An interface defines apis for command to interact with the environment.
 */
export interface CommandEnvironment {
    // TODO: Remove this property and add api to access apis of shape manager.
    shapeManager: ShapeManager;

    editingModeFlow: Flow<EditingMode>;

    /**
     * The current working parent group, which is the group that is focused, shape actions will be
     * applied to this group.
     *
     * This is similar to the concept of "current directory" in file system.
     */
    workingParentGroup: Group;

    /**
     * The current root group, which is the group that is the root of the shape tree.
     * This is similar to the concept of "root directory" in file system.
     */
    currentRootGroup: Group;

    replaceRoot(newRoot: Group, newShapeConnector: ShapeConnector): void;

    enterEditingMode(): void;

    exitEditingMode(isNewStateAccepted: boolean): void;

    executeShapeManagerCommand(command: Command): void;

    addShape(shape: AbstractShape | null): void;

    removeShape(shape: AbstractShape | null): void;

    getShapes(point: Point): Iterable<AbstractShape>;

    getAllShapesInZone(bound: Rect): Iterable<AbstractShape>;

    getWindowBound(): Rect;

    getInteractionPoint(pointPx: Point): InteractionPoint | null;

    updateInteractionBounds(): void;

    isPointInInteractionBounds(point: Point): boolean;

    setSelectionBound(bound: Rect | null): void;

    selectedShapesFlow: Flow<Set<AbstractShape>>;

    getSelectedShapes(): Set<AbstractShape>;

    addSelectedShape(shape: AbstractShape | null): void;

    toggleShapeSelection(shape: AbstractShape): void;

    setFocusingShape(shape: AbstractShape | null, focusType: ShapeFocusType): void;

    getFocusingShape(): FocusingShape | null;

    selectAllShapes(): void;

    clearSelectedShapes(): void;

    getEdgeDirection(point: Point): Direction | null;

    toXPx(column: number): number;

    toYPx(row: number): number;

    toWidthPx(width: number): number;

    toHeightPx(height: number): number;
}

export class EditingMode {
    private constructor(public isEditing: boolean, public skippedVersion: number | null) {
    }

    private static readonly IDLE = new EditingMode(false, null);
    private static readonly EDIT = new EditingMode(true, null);

    static edit(): EditingMode {
        return this.EDIT;
    }

    static idle(skippedVersion: number | null): EditingMode {
        return skippedVersion === null ? this.IDLE : new EditingMode(false, skippedVersion);
    }
}
