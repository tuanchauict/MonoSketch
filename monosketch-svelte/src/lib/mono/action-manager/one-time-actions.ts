/*
 * Copyright (c) 2024, tuanchauict
 */

import type { ChangeOrderType } from "$mono/shape/command/shape-manager-commands";
import { type TextHorizontalAlign, TextVerticalAlign } from "$mono/shape/extra/style";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";

/**
 * A type for actions related to project management
 */
type ProjectActionType =
    | { type: 'RenameCurrentProject'; newName: string }
    | { type: 'NewProject' }
    | { type: 'ExportSelectedShapes' }
    | { type: 'SwitchProject'; projectId: string }
    | { type: 'RemoveProject'; projectId: string }
    | { type: 'SaveShapesAs' }
    | { type: 'OpenShapes' };

/**
 * A type for actions related to app-wise controller
 */
type AppSettingActionType =
    | { type: 'ChangeFontSize'; isIncreased: boolean }
    | { type: 'ShowFormatPanel' }
    | { type: 'HideFormatPanel' }
    | { type: 'ShowKeyboardShortcuts' };

type TextAlignmentActionType =
    | { type: 'TextAlignment'; newHorizontalAlign?: TextHorizontalAlign; newVerticalAlign?: TextVerticalAlign };

type ChangeShapeBoundActionType =
    | { type: 'ChangeShapeBound'; newLeft?: number; newTop?: number; newWidth?: number; newHeight?: number };

/**
 * A type which defines all action types which are only have affect once.
 */
export type OneTimeActionType =
    | { type: 'Idle' }
    | ProjectActionType
    | AppSettingActionType
    | { type: 'SelectAllShapes' }
    | { type: 'DeselectShapes' }
    | { type: 'DeleteSelectedShapes' }
    | { type: 'EditSelectedShapes' }
    | { type: 'EditSelectedShape'; shape: AbstractShape | null }
    | TextAlignmentActionType
    | { type: 'MoveShapes'; offsetRow: number; offsetCol: number }
    | ChangeShapeBoundActionType
    | { type: 'ChangeShapeFillExtra'; isEnabled?: boolean; newFillStyleId?: string }
    | { type: 'ChangeShapeBorderExtra'; isEnabled?: boolean; newBorderStyleId?: string }
    | { type: 'ChangeShapeBorderDashPatternExtra'; dash?: number; gap?: number; offset?: number }
    | { type: 'ChangeShapeBorderCornerExtra'; isRoundedCorner: boolean }
    | { type: 'ChangeLineStrokeExtra'; isEnabled?: boolean; newStrokeStyleId?: string }
    | { type: 'ChangeLineStrokeDashPatternExtra'; dash?: number; gap?: number; offset?: number }
    | { type: 'ChangeLineStrokeCornerExtra'; isRoundedCorner: boolean }
    | { type: 'ChangeLineStartAnchorExtra'; isEnabled?: boolean; newHeadId?: string }
    | { type: 'ChangeLineEndAnchorExtra'; isEnabled?: boolean; newHeadId?: string }
    | { type: 'ReorderShape'; orderType: ChangeOrderType }
    | { type: 'Copy'; isRemoveRequired: boolean }
    | { type: 'Duplicate' }
    | { type: 'CopyText' }
    | { type: 'Undo' }
    | { type: 'Redo' };

export const OneTimeAction = {
    Idle: { type: 'Idle' },

    ProjectAction: {
        RenameCurrentProject: (newName: string) => ({ type: 'RenameCurrentProject', newName }),
        NewProject: { type: 'NewProject' },
        ExportSelectedShapes: { type: 'ExportSelectedShapes' },
        SwitchProject: (projectId: string) => ({ type: 'SwitchProject', projectId }),
        RemoveProject: (projectId: string) => ({ type: 'RemoveProject', projectId }),
        SaveShapesAs: { type: 'SaveShapesAs' },
        OpenShapes: { type: 'OpenShapes' },
    },

    AppSettingAction: {
        ChangeFontSize: (isIncreased: boolean) => ({ type: 'ChangeFontSize', isIncreased }),
        ShowFormatPanel: { type: 'ShowFormatPanel' },
        HideFormatPanel: { type: 'HideFormatPanel' },
        ShowKeyboardShortcuts: { type: 'ShowKeyboardShortcuts' },
    },

    SelectAllShapes: { type: 'SelectAllShapes' },
    DeselectShapes: { type: 'DeselectShapes' },
    DeleteSelectedShapes: { type: 'DeleteSelectedShapes' },
    EditSelectedShapes: { type: 'EditSelectedShapes' },
    EditSelectedShape: (shape: AbstractShape | null) => ({ type: 'EditSelectedShape', shape }),

    TextAlignment: ({ newHorizontalAlign = null, newVerticalAlign = null }: {
        newHorizontalAlign?: TextHorizontalAlign | null;
        newVerticalAlign?: TextVerticalAlign | null
    }) => ({
        type: 'TextAlignment',
        newHorizontalAlign,
        newVerticalAlign,
    }),

    MoveShapes: (offsetRow: number, offsetCol: number) => ({ type: 'MoveShapes', offsetRow, offsetCol }),
    ChangeShapeBound: ({ newLeft = null, newTop = null, newWidth = null, newHeight = null }: {
        newLeft?: number | null;
        newTop?: number | null;
        newWidth?: number | null;
        newHeight?: number | null;
    }) => ({
        type: 'ChangeShapeBound',
        newLeft,
        newTop,
        newWidth,
        newHeight,
    }),
    ChangeShapeFillExtra: ({ isEnabled = null, newFillStyleId = null }: {
        isEnabled?: boolean | null;
        newFillStyleId?: string | null
    }) => ({
        type: 'ChangeShapeFillExtra',
        isEnabled,
        newFillStyleId,
    }),
    ChangeShapeBorderExtra: ({ isEnabled = null, newBorderStyleId = null }: {
        isEnabled?: boolean | null;
        newBorderStyleId?: string | null
    }) => ({
        type: 'ChangeShapeBorderExtra',
        isEnabled,
        newBorderStyleId,
    }),

    ChangeShapeBorderDashPatternExtra: (dash?: number, gap?: number, offset?: number) => ({
        type: 'ChangeShapeBorderDashPatternExtra',
        dash,
        gap,
        offset,
    }),
    ChangeShapeBorderCornerExtra: (isRoundedCorner: boolean) => ({
        type: 'ChangeShapeBorderCornerExtra',
        isRoundedCorner,
    }),

    ChangeLineStrokeExtra: ({ isEnabled = null, newStrokeStyleId = null }: {
        isEnabled?: boolean | null;
        newStrokeStyleId?: string | null
    }) => ({
        type: 'ChangeLineStrokeExtra',
        isEnabled,
        newStrokeStyleId,
    }),
    ChangeLineStrokeDashPatternExtra: (dash?: number, gap?: number, offset?: number) => ({
        type: 'ChangeLineStrokeDashPatternExtra',
        dash,
        gap,
        offset,
    }),
    ChangeLineStrokeCornerExtra: (isRoundedCorner: boolean) => ({
        type: 'ChangeLineStrokeCornerExtra',
        isRoundedCorner,
    }),

    ChangeLineStartAnchorExtra: ({ isEnabled = null, newHeadId = null }: {
        isEnabled?: boolean | null;
        newHeadId?: string | null
    }) => ({
        type: 'ChangeLineStartAnchorExtra',
        isEnabled,
        newHeadId,
    }),

    ChangeLineEndAnchorExtra: ({ isEnabled = null, newHeadId = null }: {
        isEnabled?: boolean | null;
        newHeadId?: string | null
    }) => ({
        type: 'ChangeLineEndAnchorExtra',
        isEnabled,
        newHeadId,
    }),

    ReorderShape: (orderType: ChangeOrderType) => ({ type: 'ReorderShape', orderType }),
    Copy: (isRemoveRequired: boolean) => ({ type: 'Copy', isRemoveRequired }),
    Duplicate: { type: 'Duplicate' },

    CopyText: { type: 'CopyText' },

    Undo: { type: 'Undo' },
    Redo: { type: 'Redo' },
};
