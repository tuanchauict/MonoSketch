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

    static EMPTY: SerializableRectExtra = new SerializableRectExtra();
}

/**
 * A serializable class for extra properties of a text shape.
 */
@Jsonizable
export class SerializableTextExtra {
    @SerialName("be")
    public boundExtra: SerializableRectExtra = SerializableRectExtra.EMPTY;
    @SerialName("tha")
    public textHorizontalAlign: number = 0;
    @SerialName("tva")
    public textVerticalAlign: number = 0;

    private constructor() {
    }

    static create(
        {
            boundExtra,
            textHorizontalAlign,
            textVerticalAlign,
        }: {
            boundExtra: SerializableRectExtra,
            textHorizontalAlign: number,
            textVerticalAlign: number,
        },
    ): SerializableTextExtra {
        const result = new SerializableTextExtra();
        result.boundExtra = boundExtra;
        result.textHorizontalAlign = textHorizontalAlign;
        result.textVerticalAlign = textVerticalAlign;

        return result;
    }
}

/**
 * A serializable class for extra properties of a line shape.
 */
@Jsonizable
export class SerializableLineExtra {
    @SerialName("se")
    public isStrokeEnabled: boolean = true;
    @SerialName("su")
    public userSelectedStrokeStyleId: string = "";
    @SerialName("ase")
    public isStartAnchorEnabled: boolean = false;
    @SerialName("asu")
    public userSelectedStartAnchorId: string = "";
    @SerialName("aee")
    public isEndAnchorEnabled: boolean = false;
    @SerialName("aeu")
    public userSelectedEndAnchorId: string = "";
    @SerialName("du")
    public dashPattern: string = "";
    @SerialName("rc")
    public isRoundedCorner: boolean = false;

    private constructor() {
    }

    static create(
        {
            isStrokeEnabled,
            userSelectedStrokeStyleId,
            isStartAnchorEnabled,
            userSelectedStartAnchorId,
            isEndAnchorEnabled,
            userSelectedEndAnchorId,
            dashPattern,
            isRoundedCorner,
        }: {
            isStrokeEnabled: boolean;
            userSelectedStrokeStyleId: string;
            isStartAnchorEnabled: boolean;
            userSelectedStartAnchorId: string;
            isEndAnchorEnabled: boolean;
            userSelectedEndAnchorId: string;
            dashPattern: string;
            isRoundedCorner: boolean;
        },
    ): SerializableLineExtra {
        const result = new SerializableLineExtra();
        result.isStrokeEnabled = isStrokeEnabled;
        result.userSelectedStrokeStyleId = userSelectedStrokeStyleId;
        result.isStartAnchorEnabled = isStartAnchorEnabled;
        result.userSelectedStartAnchorId = userSelectedStartAnchorId;
        result.isEndAnchorEnabled = isEndAnchorEnabled;
        result.userSelectedEndAnchorId = userSelectedEndAnchorId;
        result.dashPattern = dashPattern;
        result.isRoundedCorner = isRoundedCorner;

        return result;
    }
}