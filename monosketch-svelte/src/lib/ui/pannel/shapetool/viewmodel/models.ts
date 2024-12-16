/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";

export interface CloudItemSelectionState {
    isChecked: boolean;
    selectedId: string;
}

export interface AppearanceOptionItem {
    id: string;
    name: string;
}

export function selectedOrDefault<T>({ selectedFlow, defaultFlow }: {
    selectedFlow: Flow<T | null>,
    defaultFlow: Flow<T | null>
}): Flow<T | null> {
    return Flow.combine2(selectedFlow, defaultFlow, (a, b) => a ?? b);
}
