/*
 * Copyright (c) 2024, tuanchauict
 */

import { DrawingInfo } from "$mono/workspace/drawing-info";

/**
 * An interface for interacting with the workspace.
 */
export interface Workspace {
    getDrawingInfo(): DrawingInfo;
}
