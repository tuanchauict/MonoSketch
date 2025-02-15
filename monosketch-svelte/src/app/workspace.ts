/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import type { Point } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import type { InteractionPoint } from "$mono/shape-interaction-bound/interaction-point";
import { DrawingInfo } from "$mono/workspace/drawing-info";
import type { MouseCursor } from "$mono/workspace/mouse/cursor-type";
import type { MousePointer } from "$mono/workspace/mouse/mouse-pointer";

/**
 * An interface for interacting with the workspace.
 */
export interface Workspace {
    drawingOffsetPointPxFlow: Flow<Point>;
    windowBoardBoundFlow: Flow<Rect>;
    mousePointerFlow: Flow<MousePointer>;

    getDrawingInfo(): DrawingInfo;

    setDrawingOffset(offsetPx: Point): void;

    setMouseCursor(mouseCursor: MouseCursor): void;

    getInteractionPoint(pointPx: Point): InteractionPoint | null;

    draw(): void;
}
