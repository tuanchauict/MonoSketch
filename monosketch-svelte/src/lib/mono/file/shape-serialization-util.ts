/*
 * Copyright (c) 2024, tuanchauict
 */

import { SerializableGroup, AbstractSerializableShape, deserializeShape } from '$mono/shape/serialization/shapes';
import {
    SerializableLineConnector,
    SerializableLineConnectorArraySerializer,
} from '$mono/shape/serialization/connector';
import { Point } from '$libs/graphics-geo/point';
import { MonoFile, Extra } from './mono-file';

export class ShapeSerializationUtil {
    static toShapeJson(serializableShape: AbstractSerializableShape): string {
        // @ts-expect-error toJson is attached by Jsonizable
        return JSON.stringify(serializableShape.toJson());
    }

    static fromShapeJson(jsonString: string): AbstractSerializableShape | null {
        try {
            const json = JSON.parse(jsonString);
            return deserializeShape(json);
        } catch (e) {
            console.error("Error while restoring shapes");
            console.error(e);
            return null;
        }
    }

    static toConnectorsJson(connectors: SerializableLineConnector[]): string {
        return JSON.stringify(SerializableLineConnectorArraySerializer.serialize(connectors));
    }
    /* eslint-disable @typescript-eslint/no-explicit-any */
    static fromConnectorsJson(jsonString: string): SerializableLineConnector[] {
        try {
            const json = JSON.parse(jsonString) as any[];
            return SerializableLineConnectorArraySerializer.deserialize(json);
        } catch (e) {
            console.error("Error while restoring connectors");
            console.error(e);
            return [];
        }
    }
    /* eslint-enable @typescript-eslint/no-explicit-any */

    static toMonoFileJson(monoFile: MonoFile): string {
        // @ts-expect-error toJson is attached by Jsonizable
        const json = monoFile.toJson();
        return JSON.stringify(json);
    }

    static fromMonoFileJson(jsonString: string): MonoFile | null {
        try {
            const json = JSON.parse(jsonString);
            // @ts-expect-error fromJson is attached by Jsonizable
            return MonoFile.fromJson(json);
        } catch (e) {
            // Fallback to version 0
            const shape = this.fromShapeJson(jsonString) as SerializableGroup | null;
            if (shape) {
                return MonoFile.create(shape, [], Extra.create("", Point.ZERO));
            } else {
                return null;
            }
        }
    }
}
