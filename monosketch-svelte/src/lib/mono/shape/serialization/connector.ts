/*
 * Copyright (c) 2024, tuanchauict
 */

import { Point, PointF } from "$libs/graphics-geo/point";
import { Jsonizable, Serializer, SerialName } from "$mono/shape/serialization/serializable";
import { AnchorSerializer, PointFSerializer, PointSerializer } from "$mono/shape/serialization/serializers";
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

/* eslint-disable @typescript-eslint/no-explicit-any */
export const SerializableLineConnectorArraySerializer = {
    serialize(value: SerializableLineConnector[]): any[] {
        // @ts-expect-error toJson is attached by Jsonizable
        return value.map(connector => connector.toJson());
    },
    deserialize(value: any[]): SerializableLineConnector[] {
        // @ts-expect-error fromJson is attached by Jsonizable
        return value.map(json => SerializableLineConnector.fromJson(json));
    },
};
/* eslint-enable @typescript-eslint/no-explicit-any */
