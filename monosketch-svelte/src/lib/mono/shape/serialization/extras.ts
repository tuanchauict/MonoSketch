/*
 * Copyright (c) 2024, tuanchauict
 */

/**
 * A serializable class for extra properties of a rectangle shape.
 */
export class SerializableRectExtra {
    constructor(
        public isFillEnabled: boolean,
        public userSelectedFillStyleId: string,
        public isBorderEnabled: boolean,
        public userSelectedBorderStyleId: string,
        public dashPattern: string,
        public corner: string,
    ) {
    }
}

/**
 * A serializable class for extra properties of a text shape.
 */
export class SerializableTextExtra {
    constructor(
        public boundExtra: SerializableRectExtra,
        public textHorizontalAlign: number,
        public textVerticalAlign: number,
    ) {
    }
}

/**
 * A serializable class for extra properties of a line shape.
 */
export class SerializableLineExtra {
    constructor(
        public isStrokeEnabled: boolean = true,
        public userSelectedStrokeStyleId: string,
        public isStartAnchorEnabled: boolean = false,
        public userSelectedStartAnchorId: string,
        public isEndAnchorEnabled: boolean = false,
        public userSelectedEndAnchorId: string,
        public dashPattern: string,
        public isRoundedCorner: boolean = false,
    ) {
    }
}