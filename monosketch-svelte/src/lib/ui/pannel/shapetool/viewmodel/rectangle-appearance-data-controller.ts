/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import { singleOrNull } from "$libs/sequence";
import { RetainableActionType } from "$mono/action-manager/retainable-actions";
import { type IRectangleExtra, ShapeExtraManager } from "$mono/shape/extra/extra-manager";
import type { RectangleExtra } from "$mono/shape/extra/shape-extra";
import { RectangleBorderCornerPattern, type StraightStrokeDashPattern } from "$mono/shape/extra/style";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Rectangle } from "$mono/shape/shape/rectangle";
import { Text } from "$mono/shape/shape/text";
import { type CloudItemSelectionState, selectedOrDefault } from "$ui/pannel/shapetool/viewmodel/models";

/**
 * A class which manages the appearance data of a rectangle related shape.
 */
export class RectangleAppearanceDataController {
    private singleRectExtraFlow: Flow<RectangleExtra | null>;
    private defaultRectangleExtraFlow: Flow<IRectangleExtra | null>;

    public fillToolStateFlow: Flow<CloudItemSelectionState | null>;
    public borderToolStateFlow: Flow<CloudItemSelectionState | null>;
    public borderDashPatternFlow: Flow<StraightStrokeDashPattern | null>;
    public borderRoundedCornerFlow: Flow<boolean | null>;
    public hasAnyVisibleToolFlow: Flow<boolean>;

    constructor(
        shapesFlow: Flow<Set<AbstractShape>>,
        retainableActionFlow: Flow<RetainableActionType>,
    ) {
        this.singleRectExtraFlow = shapesFlow.map((shapes) => {
            const shape = singleOrNull(shapes);
            if (shape instanceof Rectangle) {
                return shape.extra;
            } else if (shape instanceof Text) {
                return shape.extra.boundExtra;
            }
            return null;
        });

        this.defaultRectangleExtraFlow = retainableActionFlow.map((actionType) => {
            if (actionType === RetainableActionType.ADD_RECTANGLE || actionType === RetainableActionType.ADD_TEXT) {
                return ShapeExtraManager.defaultRectangleExtra;
            }
            return null;
        });

        this.fillToolStateFlow = this.createFillAppearanceVisibilityFlow();
        this.borderToolStateFlow = this.createBorderAppearanceVisibilityFlow();
        this.borderDashPatternFlow = this.createBorderDashPatternFlow();
        this.borderRoundedCornerFlow = this.createBorderRoundedCornerFlow();

        this.hasAnyVisibleToolFlow = Flow.combineList(
            [
                this.fillToolStateFlow,
                this.borderToolStateFlow,
                this.borderDashPatternFlow,
                this.borderRoundedCornerFlow,
            ],
            (array) => array.some((item) => item !== null),
        );
    }

    private createFillAppearanceVisibilityFlow(): Flow<CloudItemSelectionState | null> {
        return selectedOrDefault(
            {
                selectedFlow: this.singleRectExtraFlow.map(createFillAppearanceVisibilityState),
                defaultFlow: this.defaultRectangleExtraFlow.map(createFillAppearanceVisibilityState),
            },
        );
    }

    private createBorderAppearanceVisibilityFlow(): Flow<CloudItemSelectionState | null> {
        return selectedOrDefault({
            selectedFlow: this.singleRectExtraFlow.map(createBorderState),
            defaultFlow: this.defaultRectangleExtraFlow.map(createBorderState),
        });
    }

    private createBorderDashPatternFlow(): Flow<StraightStrokeDashPattern | null> {
        return selectedOrDefault({
            selectedFlow: this.singleRectExtraFlow.map((extra) => extra?.dashPattern ?? null),
            defaultFlow: this.defaultRectangleExtraFlow.map((extra) => extra?.dashPattern ?? null),
        });
    }

    private createBorderRoundedCornerFlow(): Flow<boolean | null> {
        return selectedOrDefault({
            selectedFlow: this.singleRectExtraFlow.map(createBorderRoundedCornerState),
            defaultFlow: this.defaultRectangleExtraFlow.map(createBorderRoundedCornerState),
        });
    }
}

function createFillAppearanceVisibilityState(extra: IRectangleExtra | null): CloudItemSelectionState | null {
    return extra === null ? null : { isChecked: extra.isFillEnabled, selectedId: extra.userSelectedFillStyle.id };
}

function createBorderState(extra: IRectangleExtra | null): CloudItemSelectionState | null {
    return extra === null ? null : { isChecked: extra.isBorderEnabled, selectedId: extra.userSelectedBorderStyle.id };
}

function createBorderRoundedCornerState(extra: IRectangleExtra | null): boolean | null {
    if (extra === null) {
        return null;
    }
    return extra.isBorderEnabled ? extra.corner === RectangleBorderCornerPattern.ENABLED : null;
}
