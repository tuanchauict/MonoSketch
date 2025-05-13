/*
 * Copyright (c) 2025, tuanchauict
 */

import type { ProjectDataViewModel } from "$ui/nav/project/project-data-viewmodel";
import { getContext } from "svelte";

export const PROJECT_CONTEXT = "project-context";

export function getProjectDataViewModel(): ProjectDataViewModel {
    return getContext(PROJECT_CONTEXT);
}
