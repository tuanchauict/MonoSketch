/*
 * Copyright (c) 2024, tuanchauict
 */

import { MouseCursor } from "$mono/workspace/mouse/cursor-type";

/**
 * An enum which defines all action types which repeatedly have effects after triggered.
 */
export enum RetainableActionType {
    IDLE = 'IDLE',
    ADD_RECTANGLE = 'ADD_RECTANGLE',
    ADD_TEXT = 'ADD_TEXT',
    ADD_LINE = 'ADD_LINE'
}

export const RetainableActionTypeMouseCursor: Record<RetainableActionType, MouseCursor> = {
    [RetainableActionType.IDLE]: MouseCursor.DEFAULT,
    [RetainableActionType.ADD_RECTANGLE]: MouseCursor.CROSSHAIR,
    [RetainableActionType.ADD_TEXT]: MouseCursor.TEXT,
    [RetainableActionType.ADD_LINE]: MouseCursor.CROSSHAIR,
};
