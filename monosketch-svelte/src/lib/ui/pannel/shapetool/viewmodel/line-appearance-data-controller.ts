/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import { singleOrNull } from "$libs/sequence";
import { RetainableActionType } from "$mono/action-manager/retainable-actions";
import { type ILineExtra, ShapeExtraManager } from "$mono/shape/extra/extra-manager";
import type { LineExtra } from "$mono/shape/extra/shape-extra";
import { type StraightStrokeDashPattern } from "$mono/shape/extra/style";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Line } from "$mono/shape/shape/line";
import { selectedOrDefault, type CloudItemSelectionState } from "./models";

/**
 * A class which manages the appearance data of a line related shape.
 */
export class LineAppearanceDataController {
    private singleLineExtraFlow: Flow<LineExtra | null>;
    private defaultLineExtraFlow: Flow<ILineExtra | null>;

    public strokeToolStateFlow: Flow<CloudItemSelectionState | null>;
    public strokeDashPatternFlow: Flow<StraightStrokeDashPattern | null>;
    public strokeRoundedCornerFlow: Flow<boolean | null>;
    public startHeadToolStateFlow: Flow<CloudItemSelectionState | null>;
    public endHeadToolStateFlow: Flow<CloudItemSelectionState | null>;
    public hasAnyVisibleToolFlow: Flow<boolean>;

    constructor(
        shapesFlow: Flow<Set<AbstractShape>>,
        retainableActionFlow: Flow<RetainableActionType>,
    ) {
        this.singleLineExtraFlow = shapesFlow.map((shapes) => {
            const shape = singleOrNull(shapes);
            if (shape instanceof Line) {
                return shape.extra;
            }
            return null;
        });

        this.defaultLineExtraFlow = retainableActionFlow.map((actionType) => {
            if (actionType === RetainableActionType.ADD_LINE) {
                return ShapeExtraManager.defaultLineExtra;
            }
            return null;
        });

        this.strokeToolStateFlow = this.createLineStrokeAppearanceVisibilityFlow();
        this.strokeDashPatternFlow = this.createLineStrokeDashPatternFlow();
        this.strokeRoundedCornerFlow = this.createLineStrokeRoundedCornerFlow();
        this.startHeadToolStateFlow = this.createStartHeadAppearanceVisibilityFlow();
        this.endHeadToolStateFlow = this.createEndHeadAppearanceVisibilityFlow();

        this.hasAnyVisibleToolFlow = Flow.combineList(
            [this.strokeToolStateFlow,
                this.strokeDashPatternFlow,
                this.strokeRoundedCornerFlow,
                this.startHeadToolStateFlow,
                this.endHeadToolStateFlow],
            (list) => list.some((item) => item !== null),
        );
    }

    private createLineStrokeAppearanceVisibilityFlow(): Flow<CloudItemSelectionState | null> {
        return selectedOrDefault({
            selectedFlow: this.singleLineExtraFlow.map(createStrokeState),
            defaultFlow: this.defaultLineExtraFlow.map(createStrokeState),
        });
    }

    private createLineStrokeDashPatternFlow(): Flow<StraightStrokeDashPattern | null> {
        return selectedOrDefault({
            selectedFlow: this.singleLineExtraFlow.map((extra) => extra?.dashPattern ?? null),
            defaultFlow: this.defaultLineExtraFlow.map((extra) => extra?.dashPattern ?? null),
        });
    }

    private createLineStrokeRoundedCornerFlow(): Flow<boolean | null> {
        return selectedOrDefault({
            selectedFlow: this.singleLineExtraFlow.map(createStrokeRoundedCornerState),
            defaultFlow: this.defaultLineExtraFlow.map(createStrokeRoundedCornerState),
        });
    }

    private createStartHeadAppearanceVisibilityFlow(): Flow<CloudItemSelectionState | null> {
        return selectedOrDefault({
            selectedFlow: this.singleLineExtraFlow.map(createStartHeadState),
            defaultFlow: this.defaultLineExtraFlow.map(createStartHeadState),
        });
    }

    private createEndHeadAppearanceVisibilityFlow(): Flow<CloudItemSelectionState | null> {
        return selectedOrDefault({
            selectedFlow: this.singleLineExtraFlow.map(createEndHeadState),
            defaultFlow: this.defaultLineExtraFlow.map(createEndHeadState),
        });
    }
}

function createStrokeState(extra: ILineExtra | null): CloudItemSelectionState | null {
    return extra === null ? null : { isChecked: extra.isStrokeEnabled, selectedId: extra.userSelectedStrokeStyle.id };
}

function createStrokeRoundedCornerState(extra: ILineExtra | null): boolean | null {
    if (extra === null) {
        return null;
    }
    return extra.isStrokeEnabled ? extra.isRoundedCorner : null;
}

function createStartHeadState(extra: ILineExtra | null): CloudItemSelectionState | null {
    return extra === null ? null : {
        isChecked: extra.isStartAnchorEnabled,
        selectedId: extra.userSelectedStartAnchor.id,
    };
}

function createEndHeadState(extra: ILineExtra | null): CloudItemSelectionState | null {
    return extra === null ? null : { isChecked: extra.isEndAnchorEnabled, selectedId: extra.userSelectedEndAnchor.id };
}
