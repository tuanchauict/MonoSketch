import { Point } from "$libs/graphics-geo/point";
import { SerializableLineConnector } from "$mono/shape/serialization/connector";
import { SerializableGroup } from "$mono/shape/serialization/shapes";
import { StorageDocument, StoreKeys } from "$mono/store-manager";

/**
 * A dao for an object (aka project or file) in the workspace.
 */
export class WorkspaceObjectDao {
    objectId: string;
    private objectDocument: StorageDocument;

    constructor(objectId: string, workspaceDocument: StorageDocument) {
        this.objectId = objectId;
        this.objectDocument = workspaceDocument.childDocument(objectId);
    }

    get offset(): Point {
        const storedValue = this.objectDocument.get(StoreKeys.OBJECT_OFFSET);
        if (storedValue === null) {
            return Point.ZERO;
        }
        const [left, top] = storedValue.split(',').map(parseFloat);
        if (isNaN(left) || isNaN(top)) {
            return Point.ZERO;
        }
        return Point.of(left, top);
    }

    set offset(value: Point) {
        this.objectDocument.set(StoreKeys.OBJECT_OFFSET, `${value.left},${value.top}`);
    }

    get rootGroup(): SerializableGroup | null {
        const storedValue = this.objectDocument.get(StoreKeys.OBJECT_CONTENT);
        if (storedValue === null) {
            return null;
        }
        // @ts-expect-error fromJson
        return SerializableGroup.fromJson(JSON.parse(storedValue));
    }

    set rootGroup(value: SerializableGroup | null) {
        if (value !== null) {
            // @ts-expect-error toJson
            const json = value.toJson();
            this.objectDocument.set(StoreKeys.OBJECT_CONTENT, JSON.stringify(json));
        }
        this.lastModifiedTimestampMillis = Date.now();
    }

    get connectors(): SerializableLineConnector[] {
        const storedValue = this.objectDocument.get(StoreKeys.OBJECT_CONNECTORS);
        if (storedValue === null) {
            return [];
        }
        const jsonArray = JSON.parse(storedValue);
        // @ts-expect-error fromJson
        return jsonArray.map((json: any) => SerializableLineConnector.fromJson(json));
    }

    set connectors(value: SerializableLineConnector[]) {
        // @ts-expect-error toJson
        const jsonArray = value.map(connector => connector.toJson());
        this.objectDocument.set(StoreKeys.OBJECT_CONNECTORS, JSON.stringify(jsonArray));
    }

    get name(): string {
        return this.objectDocument.get(StoreKeys.OBJECT_NAME) ?? DEFAULT_NAME;
    }

    set name(value: string) {
        this.objectDocument.set(StoreKeys.OBJECT_NAME, value);
        this.lastModifiedTimestampMillis = Date.now();
    }

    get lastModifiedTimestampMillis(): number {
        const storedValue = this.objectDocument.get(StoreKeys.OBJECT_LAST_MODIFIED);
        if (storedValue === null) {
            return Date.now();
        }
        return parseFloat(storedValue);
    }

    set lastModifiedTimestampMillis(value: number) {
        this.objectDocument.set(StoreKeys.OBJECT_LAST_MODIFIED, value.toString());
    }

    get lastOpened(): number {
        const storedValue = this.objectDocument.get(StoreKeys.OBJECT_LAST_OPENED);
        if (storedValue === null) {
            return Date.now();
        }
        return parseFloat(storedValue);
    }

    removeSelf(): void {
        const storeKeys = [
            StoreKeys.OBJECT_OFFSET,
            StoreKeys.OBJECT_CONTENT,
            StoreKeys.OBJECT_CONNECTORS,
            StoreKeys.OBJECT_NAME,
            StoreKeys.OBJECT_LAST_MODIFIED,
            StoreKeys.OBJECT_LAST_OPENED,
        ]
        for (const key of storeKeys) {
            this.objectDocument.remove(key);
        }
    }
}

const DEFAULT_NAME = "Undefined"