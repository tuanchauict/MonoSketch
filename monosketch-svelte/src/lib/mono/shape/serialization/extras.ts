/*
 * Copyright (c) 2024, tuanchauict
 */

import { Jsonizable, SerialName } from "$mono/shape/serialization/serializable";

/**
 * A serializable class for extra properties of a rectangle shape.
 */
@Jsonizable
export class SerializableRectExtra {
    @SerialName("fe")
    public isFillEnabled: boolean = false;
    @SerialName("fu")
    public userSelectedFillStyleId: string = "";
    @SerialName("be")
    public isBorderEnabled: boolean = false;
    @SerialName("bu")
    public userSelectedBorderStyleId: string = "";
    @SerialName("du")
    public dashPattern: string = "";
    @SerialName("rc")
    public corner: string = "";

    private constructor() {
    }

    static create(
        {
            isFillEnabled,
            userSelectedFillStyleId,
            isBorderEnabled,
            userSelectedBorderStyleId,
            dashPattern,
            corner,
        }: {
            isFillEnabled: boolean;
            userSelectedFillStyleId: string;
            isBorderEnabled: boolean;
            userSelectedBorderStyleId: string;
            dashPattern: string;
            corner: string;
        },
    ): SerializableRectExtra {
        const result = new SerializableRectExtra();
        result.isFillEnabled = isFillEnabled;
        result.userSelectedFillStyleId = userSelectedFillStyleId;
        result.isBorderEnabled = isBorderEnabled;
        result.userSelectedBorderStyleId = userSelectedBorderStyleId;
        result.dashPattern = dashPattern;
        result.corner = corner;

        return result;
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