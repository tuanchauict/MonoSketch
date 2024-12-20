/*
 * Copyright (c) 2024, tuanchauict
 */

import { Rect } from "$libs/graphics-geo/rect";
import { MonoBitmap } from "$mono/monobitmap/bitmap/monobitmap";
import { MonoBoard } from "$mono/monobitmap/board";
import { HighlightType } from "$mono/monobitmap/board/pixel";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Group } from "$mono/shape/shape/group";

/**
 * A helper class for exporting selected shapes.
 */
export class ExportShapesHelper {

    constructor(
        private getBitmap: (shape: AbstractShape) => MonoBitmap.Bitmap | null,
        private setClipboardText: (text: string) => void,
    ) {
    }

    exportText(shapes: AbstractShape[], isModalRequired: boolean): void {
        if (shapes.length === 0) {
            return;
        }

        const left = Math.min(...shapes.map(shape => shape.bound.left));
        const right = Math.max(...shapes.map(shape => shape.bound.right));
        const top = Math.min(...shapes.map(shape => shape.bound.top));
        const bottom = Math.max(...shapes.map(shape => shape.bound.bottom));
        const window = Rect.byLTRB(left, top, right, bottom);

        const exportingBoard = new MonoBoard();
        exportingBoard.clearAndSetWindow(window);
        this.drawShapesOntoExportingBoard(exportingBoard, shapes);

        const text = exportingBoard.toStringInBound(window);
        if (isModalRequired) {
            // TODO: Show modal
            // new ExportShapesModal().show(text);
        } else {
            this.setClipboardText(text);
        }
    }

    private drawShapesOntoExportingBoard(board: MonoBoard, shapes: Iterable<AbstractShape>): void {
        for (const shape of shapes) {
            if (shape instanceof Group) {
                this.drawShapesOntoExportingBoard(board, shape.items);
                continue;
            }
            const bitmap = this.getBitmap(shape);
            if (bitmap) {
                board.fillBitmap(shape.bound.position, bitmap, HighlightType.NO);
            }
        }
    }
}