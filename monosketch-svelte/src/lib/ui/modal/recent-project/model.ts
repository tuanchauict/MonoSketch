export enum FileAction {
    Open,
    OpenInNewTab,
    Remove,
    RemoveConfirmed,
    CancelRemove,
}

export interface FileItem {
    id: string;
    name: string;
}

export const sampleFileItems: FileItem[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((i) => ({
    id: `id:${i}`,
    name: `File ${i}`,
}));
