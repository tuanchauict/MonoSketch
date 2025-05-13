/*
 * Copyright (c) 2024, tuanchauict
 */

import type { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
import { getContext } from "svelte";

export const SHAPE_TOOL_VIEWMODEL = 'shape-tool-viewmodel';

export function getShapeToolViewModel(): ShapeToolViewModel {
    return getContext(SHAPE_TOOL_VIEWMODEL);
}
