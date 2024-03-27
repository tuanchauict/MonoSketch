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

export const sampleProjectItems: ProjectItem[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((i) => ({
    id: `id:${i}`,
    name: `File ${i}`,
}));
