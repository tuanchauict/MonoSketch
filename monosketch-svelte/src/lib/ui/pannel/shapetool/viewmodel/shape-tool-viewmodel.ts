/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import type { Rect } from "$libs/graphics-geo/rect";
import { singleOrNull } from "$libs/sequence";
import type { ActionManager } from "$mono/action-manager/action-manager";
import type { OneTimeActionType } from "$mono/action-manager/one-time-actions";
import { RetainableActionType } from "$mono/action-manager/retainable-actions";
import { ShapeExtraManager } from "$mono/shape/extra/extra-manager";
import type { StraightStrokeDashPattern, TextAlign } from "$mono/shape/extra/style";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Rectangle } from "$mono/shape/shape/rectangle";
import { Text } from "$mono/shape/shape/text";
import { LineAppearanceDataController } from "./line-appearance-data-controller";
import { type AppearanceOptionItem, type CloudItemSelectionState, type DashPattern, selectedOrDefault } from "./models";
import { RectangleAppearanceDataController } from "./rectangle-appearance-data-controller";

/**
 * A view model for the shape tool panel.
 */
export class ShapeToolViewModel {
    private readonly shapesFlow: Flow<Set<AbstractShape>>;
    private readonly retainableActionTypeFlow: Flow<RetainableActionType>;

    private readonly rectangleAppearanceDataController: RectangleAppearanceDataController;
    private readonly lineAppearanceDataController: LineAppearanceDataController;

    public readonly reorderToolVisibilityFlow: Flow<boolean>;
    public readonly singleShapeBoundFlow: Flow<Rect | null>;
    public readonly singleShapeResizeableFlow: Flow<boolean>;

    public readonly shapeFillTypeFlow: Flow<CloudItemSelectionState | null>;
    public readonly shapeBorderTypeFlow: Flow<CloudItemSelectionState | null>;
    public readonly shapeBorderDashTypeFlow: Flow<StraightStrokeDashPattern | null>;
    public readonly shapeBorderRoundedCornerFlow: Flow<boolean | null>;

    public readonly lineStrokeTypeFlow: Flow<CloudItemSelectionState | null>;
    public readonly lineStrokeDashTypeFlow: Flow<DashPattern | null>;
    public readonly lineStrokeRoundedCornerFlow: Flow<boolean | null>;

    public readonly lineStartHeadFlow: Flow<CloudItemSelectionState | null>;
    public readonly lineEndHeadFlow: Flow<CloudItemSelectionState | null>;

    public readonly appearanceVisibilityFlow: Flow<boolean>;

    public readonly textAlignFlow: Flow<TextAlign | null>;

    public readonly fillOptions: AppearanceOptionItem[] =
        ShapeExtraManager.getAllPredefinedRectangleFillStyles().map(({ id, displayName }) => ({
            id,
            name: displayName,
        }));

    public readonly strokeOptions: AppearanceOptionItem[] =
        ShapeExtraManager.getAllPredefinedStrokeStyles().map(({ id, displayName }) => ({
            id,
            name: displayName,
        }));
    public readonly headOptions: AppearanceOptionItem[] =
        ShapeExtraManager.getAllPredefinedAnchorChars().map(({ id, displayName }) => ({
            id,
            name: displayName,
        }));

    constructor(
        selectedShapesFlow: Flow<Set<AbstractShape>>,
        shapeManagerVersionFlow: Flow<number>,
        actionManager: ActionManager,
    ) {
        this.shapesFlow = Flow.combine2(
            selectedShapesFlow,
            shapeManagerVersionFlow,
            (selected, _) => selected,
        );

        this.retainableActionTypeFlow = Flow.combine2(
            actionManager.retainableActionFlow,
            ShapeExtraManager.defaultExtraStateUpdateFlow,
            (action, _) => action,
        );

        this.rectangleAppearanceDataController = new RectangleAppearanceDataController(
            this.shapesFlow,
            this.retainableActionTypeFlow,
        );

        this.lineAppearanceDataController = new LineAppearanceDataController(
            this.shapesFlow,
            this.retainableActionTypeFlow,
        );

        const singleShapeFlow = Flow.combine2(
            selectedShapesFlow,
            shapeManagerVersionFlow,
            (selected) => singleOrNull(selected),
        );

        const retainableActionFlow = Flow.combine2(
            actionManager.retainableActionFlow,
            ShapeExtraManager.defaultExtraStateUpdateFlow,
            (action, _) => action,
        );

        this.reorderToolVisibilityFlow = singleShapeFlow.map((shape) => shape !== null);
        this.singleShapeBoundFlow = singleShapeFlow.map((shape) => shape?.bound ?? null);
        this.singleShapeResizeableFlow = singleShapeFlow.map((shape) => shape instanceof Rectangle || shape instanceof Text);

        this.shapeFillTypeFlow = this.rectangleAppearanceDataController.fillToolStateFlow;
        this.shapeBorderTypeFlow = this.rectangleAppearanceDataController.borderToolStateFlow;
        this.shapeBorderDashTypeFlow = this.rectangleAppearanceDataController.borderDashPatternFlow;
        this.shapeBorderRoundedCornerFlow = this.rectangleAppearanceDataController.borderRoundedCornerFlow;

        this.lineStrokeTypeFlow = this.lineAppearanceDataController.strokeToolStateFlow;
        this.lineStrokeDashTypeFlow = this.lineAppearanceDataController.strokeDashPatternFlow;
        this.lineStrokeRoundedCornerFlow = this.lineAppearanceDataController.strokeRoundedCornerFlow;
        this.lineStartHeadFlow = this.lineAppearanceDataController.startHeadToolStateFlow;
        this.lineEndHeadFlow = this.lineAppearanceDataController.endHeadToolStateFlow;

        this.appearanceVisibilityFlow = Flow.combine2(
            this.rectangleAppearanceDataController.hasAnyVisibleToolFlow,
            this.lineAppearanceDataController.hasAnyVisibleToolFlow,
            (isRectAvailable, isLineAvailable) => isRectAvailable || isLineAvailable,
        );

        this.textAlignFlow = this.createTextAlignFlow(singleShapeFlow, retainableActionFlow);
    }

    update(action: OneTimeActionType) {
        // TODO: Handle action
        console.log("update action", action);
    }

    private createTextAlignFlow(
        selectedShapeFlow: Flow<AbstractShape | null>,
        retainableActionTypeFlow: Flow<RetainableActionType>,
    ): Flow<TextAlign | null> {
        const selectedTextAlignFlow = selectedShapeFlow.map((shape) => {
            const text = shape as Text | null;
            return text?.isTextEditable ? text.extra.textAlign : null;
        });

        const defaultTextAlignFlow = retainableActionTypeFlow.map((actionType) => {
            return actionType === RetainableActionType.ADD_TEXT ? ShapeExtraManager.defaultTextAlign : null;
        });

        return selectedOrDefault({
            selectedFlow: selectedTextAlignFlow,
            defaultFlow: defaultTextAlignFlow,
        });
    }
}
