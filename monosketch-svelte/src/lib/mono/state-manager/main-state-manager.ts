/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Workspace } from "$app/workspace";
import { Flow, LifecycleOwner } from "$libs/flow";
import type { Point, Direction } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import { MonoBoard } from "$mono/monobitmap/board";
import type { MonoBitmapManager } from "$mono/monobitmap/manager/mono-bitmap-manager";
import { AddShape, RemoveShape } from "$mono/shape/command/shape-manager-commands";
import type { ShapeConnector } from "$mono/shape/connector/shape-connector";
import type { InteractionPoint } from "$mono/shape/interaction-bound";
import type { ShapeSearcher } from "$mono/shape/searcher/shape-searcher";
import type { FocusingShape, SelectedShapeManager, ShapeFocusType } from "$mono/shape/selected-shape-manager";
import { type Command, ShapeManager } from "$mono/shape/shape-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { Group } from "$mono/shape/shape/group";
import { type CommandEnvironment, EditingMode } from "$mono/state-manager/command-environment";

/**
 * A class which connects components in the app.
 */
export class MainStateManager {
    private editingModeFlow: Flow<EditingMode> = new Flow(EditingMode.idle(null));
    private commandEnvironment: CommandEnvironment;

    private workingParentGroup: Group;

    constructor(
        lifecycleOwner: LifecycleOwner,
        private readonly mainBoard: MonoBoard,
        private readonly shapeManager: ShapeManager,
        private readonly selectedShapeManager: SelectedShapeManager,
        private readonly shapeSearcher: ShapeSearcher,
        private readonly bitmapManager: MonoBitmapManager,
        private readonly workspace: Workspace,
    ) {
        this.commandEnvironment = new CommandEnvironmentImpl({
            mainStateManager: this,
            shapeManager: this.shapeManager,
            selectedShapeManager: this.selectedShapeManager,
            shapeSearcher: this.shapeSearcher,

            getWorkingParentGroup: () => this.workingParentGroup,
            setWorkingParentGroup: (group: Group) => this.workingParentGroup = group,
        });

        this.workingParentGroup = shapeManager.root;
    }
}

interface DependencyManager {
    readonly mainStateManager: MainStateManager;
    readonly shapeManager: ShapeManager;
    readonly selectedShapeManager: SelectedShapeManager;
    readonly shapeSearcher: ShapeSearcher;

    getWorkingParentGroup(): Group;

    setWorkingParentGroup(group: Group): void;
}

class CommandEnvironmentImpl implements CommandEnvironment {
    shapeManager: ShapeManager;

    editingModeFlow: Flow<EditingMode> = new Flow(EditingMode.idle(null));

    constructor(
        private readonly dependencies: DependencyManager,
    ) {
        this.shapeManager = dependencies.shapeManager;
    }


    get workingParentGroup(): Group {
        return this.dependencies.getWorkingParentGroup();
    };

    set workingParentGroup(group: Group) {
        this.dependencies.setWorkingParentGroup(group);
    }

    get currentRootGroup(): Group {
        return this.shapeManager.root;
    };

    replaceRoot(newRoot: Group, newShapeConnector: ShapeConnector): void {
        throw new Error("Method not implemented.");
    }

    enterEditingMode(): void {
        this.editingModeFlow.value = EditingMode.edit();
    }

    exitEditingMode(isNewStateAccepted: boolean): void {
        const skippedVersion = isNewStateAccepted ? null : this.shapeManager.versionFlow.value ?? null;
        this.editingModeFlow.value = EditingMode.idle(skippedVersion);
    }

    executeShapeManagerCommand(command: Command): void {
        this.dependencies.shapeManager.execute(command);
    }

    addShape(shape: AbstractShape | null): void {
        if (shape) {
            this.executeShapeManagerCommand(new AddShape(shape));
        }
    }

    removeShape(shape: AbstractShape | null): void {
        if (shape) {
            this.executeShapeManagerCommand(new RemoveShape(shape));
        }
    }

    getShapes(point: Point): Iterable<AbstractShape> {
        return this.dependencies.shapeSearcher.getShapes(point);
    }

    getAllShapesInZone(bound: Rect): Iterable<AbstractShape> {
        return this.dependencies.shapeSearcher.getAllShapesInZone(bound);
    }

    getWindowBound(): Rect {
        throw new Error("Method not implemented.");
    }

    getInteractionPoint(pointPx: Point): InteractionPoint | null {
        throw new Error("Method not implemented.");
    }

    updateInteractionBounds(): void {
        throw new Error("Method not implemented.");
    }

    isPointInInteractionBounds(point: Point): boolean {
        throw new Error("Method not implemented.");
    }

    setSelectionBound(bound: Rect | null): void {
        throw new Error("Method not implemented.");
    }

    get selectedShapesFlow(): Flow<Set<AbstractShape>> {
        return this.dependencies.selectedShapeManager.selectedShapesFlow;
    }

    getSelectedShapes(): Set<AbstractShape> {
        return this.dependencies.selectedShapeManager.selectedShapes;
    }

    addSelectedShape(shape: AbstractShape | null): void {
        if (shape) {
            this.dependencies.selectedShapeManager.addSelectedShape(shape);
        }
    }

    toggleShapeSelection(shape: AbstractShape): void {
        this.dependencies.selectedShapeManager.toggleSelection(shape);
    }

    setFocusingShape(shape: AbstractShape | null, focusType: ShapeFocusType): void {
        this.dependencies.selectedShapeManager.setFocusingShape(shape, focusType);
    }

    getFocusingShape(): FocusingShape | null {
        return this.dependencies.selectedShapeManager.focusingShape;
    }

    selectAllShapes(): void {
        throw new Error("Method not implemented.");
    }

    clearSelectedShapes(): void {
        this.dependencies.selectedShapeManager.clearSelectedShapes();
    }

    getEdgeDirection(point: Point): Direction | null {
        return this.dependencies.shapeSearcher.getEdgeDirection(point);
    }

    toXPx(column: number): number {
        throw new Error("Method not implemented.");
    }

    toYPx(row: number): number {
        throw new Error("Method not implemented.");
    }

    toWidthPx(width: number): number {
        throw new Error("Method not implemented.");
    }

    toHeightPx(height: number): number {
        throw new Error("Method not implemented.");
    }
}