/*
 * Copyright (c) 2025, tuanchauict
 */

import type { Point } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";

export function moveShapes(
    environment: CommandEnvironment,
    selectedShapes: AbstractShape[],
    isUpdateConfirmed: boolean,
    newPositionCalculator: (shape: AbstractShape) => Point | null) {
    throw new Error("Method not implemented.");
}

/**
 * Update connectors of a shape after its bound is changed.
 * @param environment
 * @param target
 * @param newBound
 * @param isUpdateConfirmed
 * @returns The ids of the affected lines.
 */
export function updateConnectors(
    environment: CommandEnvironment,
    target: AbstractShape,
    newBound: Rect,
    isUpdateConfirmed: boolean
): string[] {
    throw new Error("Method not implemented.");
}
