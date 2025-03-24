/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Workspace } from "$app/workspace";
import { Flow, LifecycleOwner } from "$libs/flow";
import type { Direction, Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { any } from "$libs/sequence";
import { unit, type Unit } from "$libs/unit";
import type { ActionManager } from "$mono/action-manager/action-manager";
import { RetainableActionTypeMouseCursor } from "$mono/action-manager/retainable-actions";
import { DEBUG_MODE } from "$mono/build_environment";
import { MonoBoard } from "$mono/monobitmap/board";
import { HighlightType } from "$mono/monobitmap/board/pixel";
import type { MonoBitmapManager } from "$mono/monobitmap/manager/mono-bitmap-manager";
import type { InteractionPoint } from "$mono/shape-interaction-bound/interaction-point";
import { AddShape, RemoveShape } from "$mono/shape/command/shape-manager-commands";
import type { ShapeConnector } from "$mono/shape/connector/shape-connector";
import { ShapeSearcher } from "$mono/shape/searcher/shape-searcher";
import { type FocusingShape, type SelectedShapeManager, ShapeFocusType } from "$mono/shape/selected-shape-manager";
import type { ShapeClipboardManager } from "$mono/shape/shape-clipboard-manager";
import { type Command, ShapeManager } from "$mono/shape/shape-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Group } from "$mono/shape/shape/group";
import { Text } from "$mono/shape/shape/text";
import { type CommandEnvironment, EditingMode } from "$mono/state-manager/command-environment";
import { MouseInteractionController } from "$mono/state-manager/controller/mouse-interaction-controller";
import { OneTimeActionHandler } from "$mono/state-manager/one-time-action-handler";
import { StateHistoryManager } from "$mono/state-manager/state-history-manager";
import type { WorkspaceDao } from "$mono/store-manager/dao/workspace-dao";
import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";
import { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import { MousePointer, MousePointerType } from "$mono/workspace/mouse/mouse-pointer";

/**
 * A class which connects components in the app.
 */
export class MainStateManager {
    private readonly commandEnvironment: CommandEnvironment;

    private workingParentGroup: Group;

    private readonly shapeSearcher: ShapeSearcher;
    private readonly stateHistoryManager: StateHistoryManager;

    private readonly oneTimeActionHandler: OneTimeActionHandler;

    private readonly redrawRequestMutableFlow: Flow<Unit> = new Flow(unit);
    private readonly mouseInteractionController: MouseInteractionController;

    constructor(
        private readonly mainBoard: MonoBoard,
        private readonly shapeManager: ShapeManager,
        private readonly selectedShapeManager: SelectedShapeManager,
        private readonly bitmapManager: MonoBitmapManager,
        private readonly workspace: Workspace,
        private readonly workspaceDao: WorkspaceDao,
        private readonly actionManager: ActionManager,
        shapeClipboardManager: ShapeClipboardManager,
        appUiStateManager: AppUiStateManager,
        initialRootId: string,
    ) {
        if (DEBUG_MODE) {
            console.log(`Root ID: ${initialRootId}`);
        }

        this.shapeSearcher = new ShapeSearcher(shapeManager, shape => bitmapManager.getBitmap(shape));
        this.commandEnvironment = new CommandEnvironmentImpl({
            mainStateManager: this,
            shapeManager: this.shapeManager,
            selectedShapeManager: this.selectedShapeManager,
            shapeSearcher: this.shapeSearcher,
            workspace: this.workspace,
            workspaceDao: this.workspaceDao,

            getWorkingParentGroup: () => this.workingParentGroup,
            setWorkingParentGroup: (group: Group) => this.workingParentGroup = group,

            replaceRoot: (newRoot: Group, newShapeConnector: ShapeConnector) => {
                this.replaceRoot(newRoot, newShapeConnector);
            },
        });

        this.stateHistoryManager = new StateHistoryManager(this.commandEnvironment, this.workspace, this.workspaceDao);
        this.stateHistoryManager.restoreState(initialRootId);

        this.workingParentGroup = shapeManager.root;

        this.oneTimeActionHandler = new OneTimeActionHandler(
            this.commandEnvironment,
            this.bitmapManager,
            shapeClipboardManager,
            this.stateHistoryManager,
            appUiStateManager,
            this.workspaceDao,
        );

        this.mouseInteractionController = new MouseInteractionController(
            this.commandEnvironment,
            this.actionManager,
            () => this.requestRedraw(),
        );
    }

    get windowBoardBound(): Rect {
        return this.workspace.windowBoardBoundFlow.value ?? Rect.ZERO;
    }

    onStart(lifecycleOwner: LifecycleOwner): void {
        this.workspace.drawingOffsetPointPxFlow.observe(lifecycleOwner, (offsetPointPx) => {
            this.workspaceDao.getObject(this.shapeManager.root.id).offset = offsetPointPx;
        });
        this.workspace.windowBoardBoundFlow.observe(lifecycleOwner, (windowBoardBound) => {
            if (DEBUG_MODE) {
                console.log(`¶ Drawing info: window board size ${windowBoardBound} • pixel size ${this.workspace.getDrawingInfo().boundPx}`);
            }
            this.requestRedraw();
        });

        this.shapeManager.rootIdFlow.observe(lifecycleOwner, (rootId) => {
            this.workspace.setDrawingOffset(this.workspaceDao.getObject(rootId).offset);
        });
        this.shapeManager.versionFlow.observe(lifecycleOwner, (version) => {
            this.requestRedraw();
        });

        this.stateHistoryManager.observeStateChange(lifecycleOwner);
        this.oneTimeActionHandler.observe(lifecycleOwner, this.actionManager.oneTimeActionFlow);

        this.redrawRequestMutableFlow.throttle(0).observe(lifecycleOwner, () => this.redraw());

        this.workspace.mousePointerFlow.distinctUntilChanged().observe(lifecycleOwner, (mousePointer) => {
            this.mouseInteractionController.onMouseEvent(mousePointer);
            this.updateMouseCursor(mousePointer);
        });
    }

    replaceRoot(newRoot: Group, newShapeConnector: ShapeConnector): void {
        const currentRoot = this.shapeManager.root;
        if (currentRoot.id !== newRoot.id) {
            this.workspaceDao.getObject(newRoot.id).lastModifiedTimestampMillis = Date.now();
            this.workspace.setDrawingOffset(
                this.workspaceDao.getObject(newRoot.id).offset,
            );
            this.stateHistoryManager.clear();
        }

        this.shapeManager.replaceRoot(newRoot, newShapeConnector);
        this.workingParentGroup = this.shapeManager.root;
        this.commandEnvironment.clearSelectedShapes();
    }

    private requestRedraw() {
        this.redrawRequestMutableFlow.value = unit;
    }

    private redraw() {
        this.redrawMainBoard();
        this.workspace.draw();
    }

    private redrawMainBoard() {
        const windowBoardBound = this.windowBoardBound;
        this.shapeSearcher.clear(windowBoardBound);
        this.mainBoard.clearAndSetWindow(windowBoardBound);
        this.drawShapeToMainBoard(this.shapeManager.root);
    }

    private drawShapeToMainBoard(shape: AbstractShape) {
        if (shape instanceof Group) {
            for (const child of shape.items) {
                this.drawShapeToMainBoard(child);
            }
            return;
        }

        const bitmap = this.bitmapManager.getBitmap(shape);
        if (!bitmap) {
            return;
        }
        this.mainBoard.fillBitmap(shape.bound.position, bitmap, this.getHighlightType(shape));
        this.shapeSearcher.register(shape);
    }

    private getHighlightType(shape: AbstractShape): HighlightType {
        if (shape instanceof Text && shape.isTextEditing) {
            return HighlightType.TEXT_EDITING;
        }
        if (this.commandEnvironment.getSelectedShapes().has(shape)) {
            return HighlightType.SELECTED;
        }
        const focusingType = this.selectedShapeManager.getFocusingType(shape);
        switch (focusingType) {
            case ShapeFocusType.LINE_CONNECTING:
                return HighlightType.LINE_CONNECT_FOCUSING;
            case ShapeFocusType.SELECT_MODE_HOVER:
                return HighlightType.SELECTED;
            case null:
                return HighlightType.NO;
        }
    }

    private updateMouseCursor(mousePointer: MousePointer) {
        const mouseCursor = (() => {
            switch (mousePointer.type) {
                case MousePointerType.MOVE: {
                    const interactionPoint = this.commandEnvironment.getInteractionPoint(mousePointer.clientCoordinate);
                    return interactionPoint?.mouseCursor
                        ?? RetainableActionTypeMouseCursor[this.mouseInteractionController.currentRetainableActionType];
                }
                case MousePointerType.DRAG: {
                    const mouseCommand = this.mouseInteractionController.activeMouseCommand;
                    return mouseCommand?.mouseCursor ?? MouseCursor.DEFAULT;
                }
                case MousePointerType.UP:
                    return MouseCursor.DEFAULT;
                default:
                    return null;
            }
        })();
        if (mouseCursor) {
            this.workspace.setMouseCursor(mouseCursor);
        }
    }

    updateInteractionBounds(selectedShapes: Set<AbstractShape>): void {
        throw new Error("Method not implemented.");
    }
}

interface DependencyManager {
    readonly mainStateManager: MainStateManager;
    readonly shapeManager: ShapeManager;
    readonly selectedShapeManager: SelectedShapeManager;
    readonly shapeSearcher: ShapeSearcher;
    readonly workspace: Workspace;
    readonly workspaceDao: WorkspaceDao;

    getWorkingParentGroup(): Group;

    setWorkingParentGroup(group: Group): void;

    replaceRoot(newRoot: Group, newShapeConnector: ShapeConnector): void;
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
        this.dependencies.replaceRoot(newRoot, newShapeConnector);
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

    getShapes(point: Point): AbstractShape[] {
        return this.dependencies.shapeSearcher.getShapes(point);
    }

    getAllShapesInZone(bound: Rect): Iterable<AbstractShape> {
        return this.dependencies.shapeSearcher.getAllShapesInZone(bound);
    }

    getWindowBound(): Rect {
        return this.dependencies.workspace.windowBoardBoundFlow.value ?? Rect.ZERO;
    }

    getInteractionPoint(pointPx: Point): InteractionPoint | null {
        return this.dependencies.workspace.getInteractionPoint(pointPx);
    }

    updateInteractionBounds(): void {
        this.dependencies.mainStateManager.updateInteractionBounds(this.dependencies.selectedShapeManager.selectedShapes);
    }

    isPointInInteractionBounds(point: Point): boolean {
        return any(this.dependencies.selectedShapeManager.selectedShapes, shape => shape.contains(point));
    }

    setSelectionBound(bound: Rect | null): void {
        this.dependencies.workspace.drawSelectionBound(bound);
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
        for (const shape of this.dependencies.getWorkingParentGroup().items) {
            this.addSelectedShape(shape);
        }
    }

    clearSelectedShapes(): void {
        this.dependencies.selectedShapeManager.clearSelectedShapes();
    }

    getEdgeDirection(point: Point): Direction | null {
        return this.dependencies.shapeSearcher.getEdgeDirection(point);
    }

    toXPx(column: number): number {
        return this.dependencies.workspace.getDrawingInfo().toXPx(column);
    }

    toYPx(row: number): number {
        return this.dependencies.workspace.getDrawingInfo().toYPx(row);
    }

    toWidthPx(width: number): number {
        return this.dependencies.workspace.getDrawingInfo().toWidthPx(width);
    }

    toHeightPx(height: number): number {
        return this.dependencies.workspace.getDrawingInfo().toHeightPx(height);
    }
}
