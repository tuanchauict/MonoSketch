import { RetainableActionType } from "$mono/action-manager/retainable-actions";

export enum MouseActionType {
    SELECTION,
    ADD_RECTANGLE,
    ADD_TEXT,
    ADD_LINE,
}

export const mouseActionTypes = [
    MouseActionType.SELECTION,
    MouseActionType.ADD_RECTANGLE,
    MouseActionType.ADD_TEXT,
    MouseActionType.ADD_LINE,
];

interface MouseAction {
    retainableActionType: RetainableActionType;
    iconPath: string;
    title: string;
}

export const MouseActionToContentMap: Record<MouseActionType, MouseAction> = {
    [MouseActionType.SELECTION]: {
        retainableActionType: RetainableActionType.IDLE,
        iconPath: 'M7.436 20.61L7.275 3.914l12.296 11.29-7.165.235-4.97 5.168z',
        title: 'Select (V)',
    },
    [MouseActionType.ADD_RECTANGLE]: {
        retainableActionType: RetainableActionType.ADD_RECTANGLE,
        iconPath: 'M22 19H2V5h20v14zM4 7v10h16V7z',
        title: 'Rectangle (R)',
    },
    [MouseActionType.ADD_TEXT]: {
        retainableActionType: RetainableActionType.ADD_TEXT,
        iconPath:
            'M5.635 21v-2h12.731v2zm3.27-4v-1.12h2.005V4.12H7.425l-.39.44v2.58h-1.4V3h12.731v4.14h-1.4V4.56l-.39-.44h-3.485v11.76h2.005V17z',
        title: 'Text (T)',
    },
    [MouseActionType.ADD_LINE]: {
        retainableActionType: RetainableActionType.ADD_LINE,
        iconPath: 'M18 15v-2H6v2H0V9h6v2h12V9h6v6z',
        title: 'Line (L)',
    },
};

export const RetainableActionTypeToMouseActionTypeMap: Record<RetainableActionType, MouseActionType> = {
    [RetainableActionType.IDLE]: MouseActionType.SELECTION,
    [RetainableActionType.ADD_RECTANGLE]: MouseActionType.ADD_RECTANGLE,
    [RetainableActionType.ADD_TEXT]: MouseActionType.ADD_TEXT,
    [RetainableActionType.ADD_LINE]: MouseActionType.ADD_LINE,
}
