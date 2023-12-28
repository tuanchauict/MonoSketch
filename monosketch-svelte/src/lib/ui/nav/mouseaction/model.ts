export enum MouseActionType {
    SELECTION,
    ADD_RECTANGLE ,
    ADD_TEXT,
    ADD_LINE,
}

export function getTitle(type: MouseActionType): string {
    switch (type) {
        case MouseActionType.SELECTION:
            return "Select (V)"
        case MouseActionType.ADD_RECTANGLE:
            return "Rectangle (R)"
        case MouseActionType.ADD_TEXT:
            return "Text (T)"
        case MouseActionType.ADD_LINE:
            return "Line (L)"
    }
}

export function getIconPath(type: MouseActionType): string {
    switch (type) {
        case MouseActionType.SELECTION:
            return "M7.436 20.61L7.275 3.914l12.296 11.29-7.165.235-4.97 5.168z"
        case MouseActionType.ADD_RECTANGLE:
            return "M22 19H2V5h20v14zM4 7v10h16V7z"
        case MouseActionType.ADD_TEXT:
            return "M5.635 21v-2h12.731v2zm3.27-4v-1.12h2.005V4.12H7.425l-.39.44v2.58h-1.4V3h12.731v4.14h-1.4V4.56l-.39-.44h-3.485v11.76h2.005V17z"
        case MouseActionType.ADD_LINE:
            return "M18 15v-2H6v2H0V9h6v2h12V9h6v6z"
    }
}
