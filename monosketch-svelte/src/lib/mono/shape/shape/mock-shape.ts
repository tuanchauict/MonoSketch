/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Rect } from "$libs/graphics-geo/rect";
import type { AbstractSerializableShape } from "$mono/shape/serialization/shapes";
import { AbstractShape } from "$mono/shape/shape/abstract-shape";

/**
 * A simple shape for testing purpose
 */
export class MockShape extends AbstractShape {
    private boundInner: Rect;

    constructor(rect: Rect, parentId: string | null = null) {
        super(null, parentId);
        this.boundInner = rect;
    }

    toSerializableShape(_isIdIncluded: boolean): AbstractSerializableShape {
        throw new Error('Not yet implemented');
    }

    get bound(): Rect {
        return this.boundInner;
    }

    set bound(value: Rect) {
        this.update(() => {
            const isUpdated = this.boundInner !== value;
            this.boundInner = value;
            return isUpdated;
        });
    }
}