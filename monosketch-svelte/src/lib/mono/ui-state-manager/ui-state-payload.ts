/*
 * Copyright (c) 2024, tuanchauict
 */

import { type ScrollMode, ThemeMode } from "$mono/ui-state-manager/states";

export type UiStatePayloadType =
    | { type: "ShapeToolVisibility", isVisible: boolean }
    | { type: "ChangeScrollMode", scrollMode: ScrollMode }
    | { type: "ChangeTheme", themeMode: ThemeMode }
    | { type: "ChangeFontSize", isIncreased: boolean }

export const UiStatePayload = {
    ShapeToolVisibility: (isVisible: boolean): UiStatePayloadType => ({ type: "ShapeToolVisibility", isVisible }),
    ChangeScrollMode: (scrollMode: ScrollMode): UiStatePayloadType => ({ type: "ChangeScrollMode", scrollMode }),
    ChangeTheme: (themeMode: ThemeMode): UiStatePayloadType => ({ type: "ChangeTheme", themeMode }),
    ChangeFontSize: (isIncreased: boolean): UiStatePayloadType => ({ type: "ChangeFontSize", isIncreased }),
}