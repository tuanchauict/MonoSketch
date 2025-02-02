import type { Rect } from '$libs/graphics-geo/rect';

export interface CurrentProjectModel {
    id: string;
    targetBounds: Rect;
}

export enum CurrentProjectMenuAction {
    RENAME,
    SAVE_AS,
    EXPORT,
}
