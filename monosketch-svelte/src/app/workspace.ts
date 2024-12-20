/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import type { Point } from "$libs/graphics-geo/point";
import { DrawingInfo } from "$mono/workspace/drawing-info";

/**
 * An interface for interacting with the workspace.
 */
export interface Workspace {
    getDrawingInfo(): DrawingInfo;

    setDrawingOffset(offsetPx: Point): void;
    drawingOffsetPointPxFlow: Flow<Point>;
}
