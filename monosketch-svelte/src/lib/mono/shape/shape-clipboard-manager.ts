/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import { Rect } from "$libs/graphics-geo/rect";
import { TextExtra } from "$mono/shape/extra/shape-extra";
import {
    type SerializableLineConnector,
    SerializableLineConnectorArraySerializer,
} from "$mono/shape/serialization/connector";
import { Jsonizable, Serializer, SerialName } from "$mono/shape/serialization/serializable";
import {
    type AbstractSerializableShape,
    SerializableText,
    ShapeArraySerializer,
} from "$mono/shape/serialization/shapes";
import { AbstractShape } from "./shape/abstract-shape";

/**
 * A clipboard manager specializing for shapes.
 * This class handles storing shapes to clipboard and getting shapes in clipboard from paste action.
 */
export class ShapeClipboardManager {
    private clipboardShapeMutableFlow: Flow<ClipboardObject> = new Flow(ClipboardObject.create([], []));
    public clipboardShapeFlow: Flow<ClipboardObject> = this.clipboardShapeMutableFlow.immutable();

    constructor() {
        document.onpaste = (event) => {
            event.preventDefault();
            event.stopPropagation();
            this.onPasteText(event.clipboardData?.getData('text/plain') || '');
        };
    }

    private onPasteText(text: string) {
        if (text.trim() === '') {
            return;
        }
        const json = this.parseJsonFromString(text);
        // @ts-expect-error fromJson is attached by Jsonizable
        const clipboardObject = json ? ClipboardObject.fromJson(json) : null;
        if (clipboardObject && clipboardObject.shapes.length > 0) {
            this.clipboardShapeMutableFlow.value = clipboardObject;
            return;
        }

        this.clipboardShapeMutableFlow.value = ClipboardObject.create([this.createTextShapeFromText(text)], []);
    }

    private parseJsonFromString(text: string) {
        try {
            const json = JSON.parse(text);
            // Backward compatibility with old clipboard format
            return Array.isArray(json) ? { shapes: json, connectors: [] } : json;
        } catch (e) {
            console.error('Failed to parse JSON from clipboard text', e);
            return null;
        }

    }

    private createTextShapeFromText(text: string): SerializableText {
        const lines = text.split('\n').flatMap(line => line.match(/.{1,400}/g) || []);
        const width = Math.max(...lines.map(line => line.length));
        const height = lines.length;

        const toBeUsedText = text.replace(/ /g, '\u00a0');
        return SerializableText.create({
            id: null,
            isIdTemporary: false,
            versionCode: AbstractShape.nextVersionCode(),
            bound: Rect.byLTWH(0, 0, width, height),
            text: toBeUsedText,
            extra: TextExtra.NO_BOUND.toSerializableExtra(),
            isTextEditable: false,
        });
    }

    public setClipboard(clipboardObject: ClipboardObject) {
        // @ts-expect-error toJson is attached by Jsonizable
        const text = JSON.stringify(clipboardObject.toJson());
        this.setClipboardText(text);
    }

    public setClipboardText(text: string) {
        const textArea = document.createElement('textarea');
        textArea.style.position = 'absolute';
        textArea.style.left = '-10000px';
        textArea.style.top = '-10000px';
        textArea.style.width = '1px';
        textArea.style.height = '1px';
        textArea.style.opacity = '0';
        textArea.value = text;
        document.body.appendChild(textArea);
        textArea.select();
        // TODO: Address this deprecated method
        document.execCommand('copy');
        document.body.removeChild(textArea);
    }
}

/**
 * A data class to store shapes and connectors in clipboard when serializing to JSON.
 */
@Jsonizable
export class ClipboardObject {
    @SerialName("shapes")
    @Serializer(ShapeArraySerializer)
    public shapes: AbstractSerializableShape[] = [];
    @SerialName("connectors")
    @Serializer(SerializableLineConnectorArraySerializer)
    public connectors: SerializableLineConnector[] = [];

    private constructor() {
    }

    static create(shapes: AbstractSerializableShape[], connectors: SerializableLineConnector[]): ClipboardObject {
        const result = new ClipboardObject();
        result.shapes = shapes;
        result.connectors = connectors;
        return result;
    }
}
