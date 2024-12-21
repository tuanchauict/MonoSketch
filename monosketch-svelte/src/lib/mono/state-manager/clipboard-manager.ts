/*
 * Copyright (c) 2024, tuanchauict
 */

import { LifecycleOwner } from "$libs/flow";
import { Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { ShapeConnector } from "$mono/shape/connector/shape-connector";
import { ClipboardObject, type ShapeClipboardManager } from "$mono/shape/shape-clipboard-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Group } from "$mono/shape/shape/group";
import { Line } from "$mono/shape/shape/line";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";

/**
 * A manager class to handle clipboard related data.
 */
export class ClipboardManager {
    private selectedShapes: AbstractShape[] = [];

    constructor(
        private environment: CommandEnvironment,
        private shapeClipboardManager: ShapeClipboardManager,
    ) {
    }

    observe(lifecycleOwner: LifecycleOwner) {
        this.environment.selectedShapesFlow.observe(lifecycleOwner, (shapes) => {
            this.selectedShapes = Array.from(shapes);
        });
        this.shapeClipboardManager.clipboardShapeFlow.observe(
            lifecycleOwner,
            this.pasteShapes.bind(this),
        );
    }

    copySelectedShapes(isRemoveRequired: boolean) {
        this.shapeClipboardManager.setClipboard(this.createClipboardObject());
        if (isRemoveRequired) {
            for (const shape of this.selectedShapes) {
                this.environment.removeShape(shape);
            }
            this.environment.clearSelectedShapes();
        }
    }

    private pasteShapes(clipboardObject: ClipboardObject) {
        if (clipboardObject.shapes.length === 0) {
            return;
        }
        this.environment.clearSelectedShapes();
        const bound = this.environment.getWindowBound();
        const left = bound.left + bound.width / 5;
        const top = bound.top + bound.height / 5;
        this.insertShapes(left, top, clipboardObject);
    }

    duplicateSelectedShapes() {
        if (this.selectedShapes.length === 0) {
            return;
        }
        const currentSelectedShapes = this.selectedShapes;
        const clipboardObject = this.createClipboardObject();
        const minLeft = Math.min(...currentSelectedShapes.map(shape => shape.bound.left));
        const minTop = Math.min(...currentSelectedShapes.map(shape => shape.bound.top));

        this.environment.clearSelectedShapes();
        this.insertShapes(minLeft + 1, minTop + 1, clipboardObject);
    }

    private insertShapes(
        left: number,
        top: number,
        clipboardObject: ClipboardObject,
    ) {
        const currentParentId = this.environment.workingParentGroup.id;

        const srcIdToShapeMap = new Map(
            clipboardObject.shapes.map(shape => [shape.id, Group.toShape(currentParentId, shape)]),
        );
        const minLeft = Math.min(...Array.from(srcIdToShapeMap.values()).map(shape => shape.bound.left));
        const minTop = Math.min(...Array.from(srcIdToShapeMap.values()).map(shape => shape.bound.top));

        const offset = Point.from({ left: minLeft - left, top: minTop - top });
        for (const shape of srcIdToShapeMap.values()) {
            const shapeBound = shape.bound;
            const newShapeBound = new Rect(shapeBound.position.minus(offset), shapeBound.size);
            shape.setBound(newShapeBound);

            this.environment.addShape(shape);
            this.environment.addSelectedShape(shape);
        }

        for (const connector of clipboardObject.connectors) {
            const line = srcIdToShapeMap.get(connector.lineId);
            if (!line || !(line instanceof Line)) {
                continue;
            }
            const target = srcIdToShapeMap.get(connector.targetId);
            if (line && target) {
                this.environment.shapeManager.shapeConnector.addConnector(line, connector.anchor, target);
            }
        }
    }

    private createClipboardObject(): ClipboardObject {
        const serializableShapes =
            this.selectedShapes.map(shape => shape.toSerializableShape(false));
        const serializableConnectors = this.selectedShapes.flatMap(target =>
            this.environment.shapeManager.shapeConnector.getConnectors(target).map(connector =>
                ShapeConnector.toSerializableConnector(connector, target),
            ),
        );
        return ClipboardObject.create(serializableShapes, serializableConnectors);
    }
}
