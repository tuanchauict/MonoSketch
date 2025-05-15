/*
 * Copyright (c) 2025, tuanchauict
 */

export enum ProjectAction {
    Open,
    OpenInNewTab,
    Remove,
    RemoveConfirmed,
    CancelRemove,
}

export interface ProjectItem {
    id: string;
    name: string;
}
