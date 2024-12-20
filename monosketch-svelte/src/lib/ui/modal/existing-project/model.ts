/*
 * Copyright (c) 2024, tuanchauict
 */

export interface ExistingProjectModel {
    projectName: string;
    lastEditedTimeMillis: number;

    onKeepBoth: () => void;
    onReplace: () => void;
}
