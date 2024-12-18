/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from '$libs/flow/flow';
import type { AbstractShape } from '$mono/shape/shape/abstract-shape';

/**
 * A class to manage selected shapes.
 */
export class SelectedShapeManager {
    private selectedShapesMutableFlow: Flow<Set<AbstractShape>> = new Flow(new Set());
    public selectedShapesFlow: Flow<Set<AbstractShape>> = this.selectedShapesMutableFlow;

    private focusingShapeMutableFlow: Flow<FocusingShape | null> = new Flow(null);
    public focusingShapeFlow: Flow<FocusingShape | null> = this.focusingShapeMutableFlow;

    get selectedShapes(): Set<AbstractShape> {
        return this.selectedShapesFlow.value ?? new Set();
    }

    get focusingShape(): FocusingShape | null {
        return this.focusingShapeFlow.value ?? null;
    }

    addSelectedShape(shape: AbstractShape) {
        const selectedShapes = new Set(this.selectedShapes);
        selectedShapes.add(shape);
        this.selectedShapesMutableFlow.value = selectedShapes;
    }

    toggleSelection(shape: AbstractShape) {
        const selectedShapes = new Set(this.selectedShapes);
        if (selectedShapes.has(shape)) {
            selectedShapes.delete(shape);
        } else {
            selectedShapes.add(shape);
        }
        this.selectedShapesMutableFlow.value = selectedShapes;
    }

    clearSelectedShapes() {
        this.selectedShapesMutableFlow.value = new Set();
    }

    setFocusingShape(shape: AbstractShape | null, focusType: ShapeFocusType) {
        this.focusingShapeMutableFlow.value = shape ? { shape, focusType } : null;
    }

    getFocusingType(shape: AbstractShape | null): ShapeFocusType | null {
        const focusingShape = this.focusingShapeFlow.value;
        return focusingShape && focusingShape.shape === shape ? focusingShape.focusType : null;
    }
}

/**
 * An enum class defines the type of focus.
 */
export enum ShapeFocusType {
    LINE_CONNECTING,
    SELECT_MODE_HOVER
}

/**
 * A model interface representing the focusing shape and its focusing type.
 */
export interface FocusingShape {
    shape: AbstractShape;
    focusType: ShapeFocusType;
}
