/*
 * Copyright (c) 2024, tuanchauict
 */

import { Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { Size } from "$libs/graphics-geo/size";
import { type ShapeExtra, TextExtra } from "$mono/shape/extra/shape-extra";
import { AbstractSerializableShape, SerializableText } from "$mono/shape/serialization/serializable-shape";
import { AbstractShape } from "$mono/shape/shape/abstract-shape";

/**
 * A text shape which contains a bound and a text.
 */
class Text extends AbstractShape {
    private userSettingSize: Size = Size.ZERO;

    // Text can be auto resized by text
    private boundInner: Rect;

    private textInner: string = "";
    private isTextEditableInner: boolean;
    private isTextEditingInner: boolean = false;
    private renderableTextInner: RenderableText = RenderableText.EMPTY;

    constructor(rect: Rect, id: string | null = null, parentId: string | null = null, isTextEditable: boolean = true) {
        super(id, parentId);
        this.boundInner = rect;
        this.isTextEditableInner = isTextEditable;
        this.userSettingSize = rect.size;
        this.updateRenderableText();
    }

    static fromPoints(startPoint: Point, endPoint: Point, id: string | null = null, parentId: string | null = null, isTextEditable: boolean): Text {
        const rect = Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top);
        return new Text(rect, id, parentId, isTextEditable);
    }

    static fromSerializable(serializableText: SerializableText, parentId: string | null): Text {
        const text = new Text(serializableText.bound, serializableText.actualId, parentId, serializableText.isTextEditable);
        text.extraInner = TextExtra.fromSerializable(serializableText.extra);
        text.setText(serializableText.text);
        text.versionCode = serializableText.versionCode;
        return text;
    }

    get text(): string {
        return this.textInner;
    }

    setText(newText: string): void {
        this.update(() => {
            const isTextChanged = newText !== this.textInner;
            this.textInner = newText;
            this.updateRenderableText();
            return isTextChanged;
        });
    }

    get isTextEditable(): boolean {
        return this.isTextEditableInner;
    }

    setTextEditingMode(isEditing: boolean): void {
        this.update(() => {
            const isUpdated = this.isTextEditingInner !== isEditing;
            this.isTextEditingInner = isEditing;
            return isUpdated;
        });
    }

    get contentBound(): Rect {
        return this.extra.boundExtra.isBorderEnabled
            ? Rect.byLTWH(this.bound.left + 1, this.bound.top + 1, this.bound.width - 2, this.bound.height - 2)
            : this.bound;
    }

    get bound(): Rect {
        return this.boundInner;
    }

    set bound(newBound: Rect) {
        this.update(() => {
            const isUpdated = this.boundInner !== newBound;
            this.userSettingSize = newBound.size;
            this.boundInner = newBound;
            this.updateRenderableText();
            return isUpdated;
        });
    }

    get extra(): TextExtra {
        return this.extraInner as TextExtra;
    }

    setExtra(newExtra: ShapeExtra): void {
        if (!(newExtra instanceof TextExtra)) {
            throw new Error(`New extra is not a TextExtra (${newExtra.constructor.name})`);
        }
        if (newExtra.equals(this.extraInner)) {
            return;
        }
        this.update(() => {
            this.extraInner = newExtra;
            this.updateRenderableText();
            return true;
        });
    }

    toSerializableShape(isIdIncluded: boolean): AbstractSerializableShape {
        return new SerializableText(
            this.id,
            !isIdIncluded,
            this.versionCode,
            this.bound,
            this.text,
            this.extra.toSerializableExtra(),
            this.isTextEditable,
        );
    }

    private updateRenderableText(): void {
        const maxRowCharCount = this.extra.hasBorder() ? this.bound.width - 2 : this.bound.width;
        if (this.text !== this.renderableTextInner.text || maxRowCharCount !== this.renderableTextInner.maxRowCharCount) {
            this.renderableTextInner = new RenderableText(this.text, Math.max(maxRowCharCount, 1));
        }
    }

    makeTextEditable(): void {
        if (this.isTextEditableInner) {
            return;
        }
        this.update(() => {
            this.isTextEditableInner = true;
            return true;
        });
    }
}

/**
 * A class to generate renderable text.
 */
class RenderableText {
    private renderableText: string[] | null = null;

    constructor(public text: string, public maxRowCharCount: number) {
    }

    getRenderableText(): string[] {
        if (!this.renderableText) {
            this.renderableText = this.createRenderableText();
        }
        return this.renderableText;
    }

    private createRenderableText(): string[] {
        if (this.maxRowCharCount === 1) {
            return this.text.split('');
        } else {
            return this.standardizeLines(this.text.split('\n'));
        }
    }

    private standardizeLines(lines: string[]): string[] {
        return lines.flatMap(line => {
            const adjustedLines: string[] = [];
            let currentLine = '';
            for (const word of this.toStandardizedWords(line, this.maxRowCharCount)) {
                const space = currentLine ? ' ' : '';
                const newLineLength = currentLine.length + space.length + word.length;
                if (newLineLength <= this.maxRowCharCount) {
                    currentLine += space + word;
                } else {
                    adjustedLines.push(currentLine);
                    currentLine = word;
                }
            }
            adjustedLines.push(currentLine);
            return adjustedLines;
        });
    }

    private toStandardizedWords(line: string, maxCharCount: number): string[] {
        return line.split(' ').flatMap(word => {
            if (word.length <= maxCharCount) {
                return [word];
            } else {
                return word.match(new RegExp(`.{1,${maxCharCount}}`, 'g')) || [];
            }
        });
    }

    static EMPTY = new RenderableText('', 0);
}
