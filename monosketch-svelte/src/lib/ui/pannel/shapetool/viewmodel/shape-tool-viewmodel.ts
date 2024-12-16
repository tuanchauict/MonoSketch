/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import type { Rect } from "$libs/graphics-geo/rect";
import { singleOrNull } from "$libs/sequence";
import type { ActionManager } from "$mono/action-manager/action-manager";
import { RetainableActionType } from "$mono/action-manager/retainable-actions";
import { ShapeExtraManager } from "$mono/shape/extra/extra-manager";
import type { StraightStrokeDashPattern, TextAlign } from "$mono/shape/extra/style";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Rectangle } from "$mono/shape/shape/rectangle";
import { Text } from "$mono/shape/shape/text";
import { selectedOrDefault, type CloudItemSelectionState, type AppearanceOptionItem } from "./models";
import { LineAppearanceDataController } from "./line-appearance-data-controller";
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
    public readonly lineStrokeDashTypeFlow: Flow<StraightStrokeDashPattern | null>;
    public readonly lineStrokeRoundedCornerFlow: Flow<boolean | null>;

    public readonly lineStartHeadFlow: Flow<CloudItemSelectionState | null>;
    public readonly lineEndHeadFlow: Flow<CloudItemSelectionState | null>;

    public readonly appearanceVisibilityFlow: Flow<boolean>;

    public readonly textAlignFlow: Flow<TextAlign | null>;

    public readonly hasAnyToolFlow: Flow<boolean>;

    public readonly fillOptions: AppearanceOptionItem[];
    public readonly strokeOptions: AppearanceOptionItem[];
    public readonly headOptions: AppearanceOptionItem[];

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

        const textAlignFlow = this.createTextAlignFlow(singleShapeFlow, retainableActionFlow);
        this.textAlignFlow = textAlignFlow;

        this.hasAnyToolFlow = Flow.combineList(
            [
                singleShapeFlow.map((shape) => shape !== null),
                this.rectangleAppearanceDataController.hasAnyVisibleToolFlow,
                this.lineAppearanceDataController.hasAnyVisibleToolFlow,
                textAlignFlow.map((align) => align !== null),
            ],
            (states) => states.some((state) => state === true),
        );

        this.fillOptions = ShapeExtraManager.getAllPredefinedRectangleFillStyles()
            .map((style) => ({ id: style.id, name: style.displayName }));
        this.strokeOptions = ShapeExtraManager.getAllPredefinedStrokeStyles()
            .map((style) => ({ id: style.id, name: style.displayName }));
        this.headOptions = ShapeExtraManager.getAllPredefinedAnchorChars()
            .map((char) => ({ id: char.id, name: char.displayName }));
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
