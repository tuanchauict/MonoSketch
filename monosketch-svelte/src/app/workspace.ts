/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import type { Point } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import { DrawingInfo } from "$mono/workspace/drawing-info";

/**
 * An interface for interacting with the workspace.
 */
export interface Workspace {
    drawingOffsetPointPxFlow: Flow<Point>;
    windowBoardBoundFlow: Flow<Rect>;

    getDrawingInfo(): DrawingInfo;

    setDrawingOffset(offsetPx: Point): void;

    draw(): void;
}
