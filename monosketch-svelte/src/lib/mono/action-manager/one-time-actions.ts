/*
 * Copyright (c) 2024, tuanchauict
 */

import type { ChangeOrderType } from "$mono/shape/command/shape-manager-commands";
import { type TextHorizontalAlign, TextVerticalAlign } from "$mono/shape/extra/style";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";

/**
 * A type for actions related to project management
 */
export type ProjectActionType =
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
export type AppSettingActionType =
    | { type: 'ChangeFontSize'; isIncreased: boolean }
    | { type: 'ShowFormatPanel' }
    | { type: 'HideFormatPanel' }
    | { type: 'ShowKeyboardShortcuts' };

type TextAlignmentActionType = {
    type: 'TextAlignment';
    newHorizontalAlign: TextHorizontalAlign;
    newVerticalAlign: TextVerticalAlign;
};

type ChangeShapeBoundActionType = {
    type: 'ChangeShapeBound';
    newLeft: number;
    newTop: number;
    newWidth: number;
    newHeight: number
};

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
    | { type: 'ChangeShapeFillExtra'; isEnabled: boolean; newFillStyleId: string | null }
    | { type: 'ChangeShapeBorderExtra'; isEnabled: boolean; newBorderStyleId: string | null }
    | { type: 'ChangeShapeBorderDashPatternExtra'; dash: number; gap: number; offset: number }
    | { type: 'ChangeShapeBorderCornerExtra'; isRoundedCorner: boolean }
    | { type: 'ChangeLineStrokeExtra'; isEnabled: boolean; newStrokeStyleId: string | null }
    | { type: 'ChangeLineStrokeDashPatternExtra'; dash: number; gap: number; offset: number }
    | { type: 'ChangeLineStrokeCornerExtra'; isRoundedCorner: boolean }
    | { type: 'ChangeLineStartAnchorExtra'; isEnabled: boolean; newHeadId: string | null }
    | { type: 'ChangeLineEndAnchorExtra'; isEnabled: boolean; newHeadId: string | null }
    | { type: 'ReorderShape'; orderType: ChangeOrderType }
    | { type: 'Copy'; isRemoveRequired: boolean }
    | { type: 'Duplicate' }
    | { type: 'CopyText' }
    | { type: 'Undo' }
    | { type: 'Redo' };

export const OneTimeAction = {
    Idle: { type: 'Idle' } as OneTimeActionType,

    ProjectAction: {
        RenameCurrentProject: (newName: string): OneTimeActionType => ({ type: 'RenameCurrentProject', newName }),
        NewProject: { type: 'NewProject' } as OneTimeActionType,
        ExportSelectedShapes: { type: 'ExportSelectedShapes' } as OneTimeActionType,
        SwitchProject: (projectId: string): OneTimeActionType => ({ type: 'SwitchProject', projectId }),
        RemoveProject: (projectId: string): OneTimeActionType => ({ type: 'RemoveProject', projectId }),
        SaveShapesAs: { type: 'SaveShapesAs' } as OneTimeActionType,
        OpenShapes: { type: 'OpenShapes' } as OneTimeActionType,
    },

    AppSettingAction: {
        ChangeFontSize: (isIncreased: boolean): OneTimeActionType => ({ type: 'ChangeFontSize', isIncreased }),
        ShowFormatPanel: { type: 'ShowFormatPanel' } as OneTimeActionType,
        HideFormatPanel: { type: 'HideFormatPanel' } as OneTimeActionType,
        ShowKeyboardShortcuts: { type: 'ShowKeyboardShortcuts' } as OneTimeActionType,
    },

    SelectAllShapes: { type: 'SelectAllShapes' } as OneTimeActionType,
    DeselectShapes: { type: 'DeselectShapes' } as OneTimeActionType,
    DeleteSelectedShapes: { type: 'DeleteSelectedShapes' } as OneTimeActionType,
    EditSelectedShapes: { type: 'EditSelectedShapes' } as OneTimeActionType,
    EditSelectedShape: (shape: AbstractShape | null): OneTimeActionType => ({ type: 'EditSelectedShape', shape }),

    TextAlignment: ({ newHorizontalAlign, newVerticalAlign }: {
        newHorizontalAlign: TextHorizontalAlign;
        newVerticalAlign: TextVerticalAlign
    }): OneTimeActionType => ({
        type: 'TextAlignment',
        newHorizontalAlign,
        newVerticalAlign,
    }),

    MoveShapes: (offsetRow: number, offsetCol: number): OneTimeActionType => ({
        type: 'MoveShapes',
        offsetRow,
        offsetCol,
    }),
    ChangeShapeBound: ({ newLeft, newTop, newWidth, newHeight }: {
        newLeft: number;
        newTop: number;
        newWidth: number;
        newHeight: number;
    }): OneTimeActionType => ({
        type: 'ChangeShapeBound',
        newLeft,
        newTop,
        newWidth,
        newHeight,
    }),
    ChangeShapeFillExtra: ({ isEnabled, newFillStyleId = null }: {
        isEnabled: boolean;
        newFillStyleId: string | null
    }): OneTimeActionType => ({
        type: 'ChangeShapeFillExtra',
        isEnabled,
        newFillStyleId,
    }),
    ChangeShapeBorderExtra: ({ isEnabled, newBorderStyleId = null }: {
        isEnabled: boolean;
        newBorderStyleId: string | null
    }): OneTimeActionType => ({
        type: 'ChangeShapeBorderExtra',
        isEnabled,
        newBorderStyleId,
    }),

    ChangeShapeBorderDashPatternExtra: ({ dash, gap, offset }: {
        dash: number,
        gap: number,
        offset: number
    }): OneTimeActionType => ({
        type: 'ChangeShapeBorderDashPatternExtra',
        dash,
        gap,
        offset,
    }),
    ChangeShapeBorderCornerExtra: (isRoundedCorner: boolean): OneTimeActionType => ({
        type: 'ChangeShapeBorderCornerExtra',
        isRoundedCorner,
    }),

    ChangeLineStrokeExtra: ({ isEnabled, newStrokeStyleId = null }: {
        isEnabled: boolean;
        newStrokeStyleId?: string | null
    }): OneTimeActionType => ({
        type: 'ChangeLineStrokeExtra',
        isEnabled,
        newStrokeStyleId,
    }),
    ChangeLineStrokeDashPatternExtra: ({ dash, gap, offset }: {
        dash: number,
        gap: number,
        offset: number
    }): OneTimeActionType => ({
        type: 'ChangeLineStrokeDashPatternExtra',
        dash,
        gap,
        offset,
    }),
    ChangeLineStrokeCornerExtra: (isRoundedCorner: boolean): OneTimeActionType => ({
        type: 'ChangeLineStrokeCornerExtra',
        isRoundedCorner,
    }),

    ChangeLineStartAnchorExtra: ({ isEnabled, newHeadId = null }: {
        isEnabled: boolean;
        newHeadId?: string | null
    }): OneTimeActionType => ({
        type: 'ChangeLineStartAnchorExtra',
        isEnabled,
        newHeadId,
    }),

    ChangeLineEndAnchorExtra: ({ isEnabled, newHeadId = null }: {
        isEnabled: boolean;
        newHeadId?: string | null
    }): OneTimeActionType => ({
        type: 'ChangeLineEndAnchorExtra',
        isEnabled,
        newHeadId,
    }),

    ReorderShape: (orderType: ChangeOrderType): OneTimeActionType => ({ type: 'ReorderShape', orderType }),
    Copy: (isRemoveRequired: boolean): OneTimeActionType => ({ type: 'Copy', isRemoveRequired }),
    Duplicate: { type: 'Duplicate' } as OneTimeActionType,

    CopyText: { type: 'CopyText' } as OneTimeActionType,

    Undo: { type: 'Undo' } as OneTimeActionType,
    Redo: { type: 'Redo' } as OneTimeActionType,
};
