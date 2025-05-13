/*
 * Copyright (c) 2024, tuanchauict
 */

import { Point } from "$libs/graphics-geo/point";
import {
    type SerializableLineConnector,
    SerializableLineConnectorArraySerializer,
} from "$mono/shape/serialization/connector";
import { Jsonizable, Serializer, SerialName } from "$mono/shape/serialization/serializable";
import { SerializableGroup } from "$mono/shape/serialization/shapes";

/**
 * Version of the mono file
 *
 * # Version 0
 * No mono file format. It was just a serialization of the root group
 *
 * # Version 1
 * The first version of MonoFile.
 * - root: root group content
 * - extra:
 *     - name
 *     - offset
 * - version: 1
 * - modified_timestamp_millis: timestamp in millisecond (local time)
 *
 * # Version 2
 * Include `connectors`
 * - connectors: list of serialization of line connectors
 */
const MONO_FILE_VERSION = 2;

/**
 * A data class for serializing shape to Json and load shape from Json.
 */
@Jsonizable
export class MonoFile {
    @SerialName("root")
        root: SerializableGroup = SerializableGroup.EMPTY;
    @SerialName("extra")
        extra: Extra = Extra.create("", Point.ZERO);
    @SerialName("version")
        version: number = 0;
    @SerialName("modified_timestamp_millis")
        modifiedTimestampMillis: number = 0;
    @SerialName("connectors")
    @Serializer(SerializableLineConnectorArraySerializer)
        connectors: SerializableLineConnector[] = [];

    static create(
        root: SerializableGroup,
        connectors: SerializableLineConnector[],
        extra: Extra,
        modifiedTimestampMillis: number = Date.now(),
    ): MonoFile {
        const file = new MonoFile();
        file.root = root;
        file.connectors = connectors;
        file.extra = extra;
        file.version = MONO_FILE_VERSION;
        file.modifiedTimestampMillis = modifiedTimestampMillis;
        return file;
    }
}

@Jsonizable
export class Extra {
    @SerialName("name")
        name: string = "";
    @SerialName("offset")
        offset: Point = Point.ZERO;

    private constructor() {
    }

    static create(name: string, offset: Point): Extra {
        const extra = new Extra();
        extra.name = name;
        extra.offset = offset;
        return extra;
    }
}
