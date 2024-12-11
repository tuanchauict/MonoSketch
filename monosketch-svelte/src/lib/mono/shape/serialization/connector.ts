/*
 * Copyright (c) 2024, tuanchauict
 */

import { Point, PointF } from "$libs/graphics-geo/point";
import { Jsonizable, Serializer, SerialName } from "$mono/shape/serialization/serializable";
import { AnchorSerializer, PointFSerializer, PointSerializer } from "$mono/shape/serialization/serializer";
import { LineAnchor } from "$mono/shape/shape/linehelper";

@Jsonizable
export class SerializableLineConnector {
    @SerialName("i")
    public lineId: string = '';
    @SerialName("a")
    @Serializer(AnchorSerializer)
    public anchor: LineAnchor = LineAnchor.START;
    @SerialName("t")
    public targetId: string = '';
    @SerialName("r")
    @Serializer(PointFSerializer)
    public ratio: PointF = PointF.ZERO;
    @SerialName("o")
    @Serializer(PointSerializer)
    public offset: Point = Point.ZERO;

    private constructor() {
    }

    static create(
        {
            lineId,
            anchor,
            targetId,
            ratio,
            offset,
        }: {
            lineId: string;
            anchor: LineAnchor;
            targetId: string;
            ratio: PointF;
            offset: Point;
        }): SerializableLineConnector {
        const result = new SerializableLineConnector();
        result.lineId = lineId;
        result.anchor = anchor;
        result.targetId = targetId;
        result.ratio = ratio;
        result.offset = offset;
        return result;
    }
}
