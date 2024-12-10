/*
 * Copyright (c) 2024, tuanchauict
 */

import { type DirectedPoint, Point, PointF } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import { TwoWayQuickMap } from "$mono/shape/collection/two-way-quick-map";
import { ConnectorIdentifier, LineConnector, ShapeConnectorUseCase } from "$mono/shape/connector/line-connector";
import { SerializableLineConnector } from "$mono/shape/serialization/serializable-connector";
import { AbstractShape, ShapeIdentifier } from "$mono/shape/shape/abstract-shape";
import { Line } from "$mono/shape/shape/line";
import { LineAnchor } from "$mono/shape/shape/linehelper";

/**
 * A manager of shape connectors.
 */
export class ShapeConnector {
    private lineConnectors = new TwoWayQuickMap<LineConnector, ShapeIdentifier>();

    addConnector(line: Line, anchor: LineAnchor, shape: AbstractShape) {
        const anchorPoint = anchor === LineAnchor.START ? line.startPoint : line.endPoint;
        const boxBound = shape.bound;
        const result = this.calculateConnectorRatioAndOffset(anchorPoint, boxBound);
        if (!result) return;

        const [ratio, offset] = result;
        const connector = new LineConnector(line.id, anchor, ratio, offset);

        // TODO: Based on the position of anchor point and shape's bound, the direction of the
        //  anchor must be updated to optimise the initial directions of the line

        this.lineConnectors.set(connector, shape.identifier);
    }

    getConnectors(shape: AbstractShape): LineConnector[] {
        return Array.from(this.lineConnectors.getKeys(shape));
    }

    removeConnector(line: Line, anchor: LineAnchor) {
        this.lineConnectors.removeKey(new ConnectorIdentifier(line.id, anchor));
    }

    hasConnector(line: Line, anchor: LineAnchor): boolean {
        return this.lineConnectors.get(new ConnectorIdentifier(line.id, anchor)) !== undefined;
    }

    removeShape(shape: AbstractShape) {
        this.lineConnectors.removeValue(shape);

        if (shape instanceof Line) {
            this.removeConnector(shape, LineAnchor.START);
            this.removeConnector(shape, LineAnchor.END);
        }
    }

    /**
     * Calculates the connector ratio. Returns null if the two cannot connect.
     */
    private calculateConnectorRatioAndOffset(anchorPoint: DirectedPoint, boxBound: Rect): [PointF, Point] | null {
        const around = ShapeConnectorUseCase.getAround(anchorPoint, boxBound);
        if (!around) {
            return null
        }

        const ratio = ShapeConnectorUseCase.calculateRatio(around, anchorPoint, boxBound);
        const offset = ShapeConnectorUseCase.calculateOffset(around, anchorPoint, boxBound);
        return [ratio, offset];
    }

    toSerializable(): SerializableLineConnector[] {
        return Array.from(this.lineConnectors.asSequence()).map(([lineConnector, target]) =>
            ShapeConnector.toSerializableConnector(lineConnector, target),
        );
    }

    static fromSerializable(serializedConnectors: SerializableLineConnector[]): ShapeConnector {
        const manager = new ShapeConnector();
        for (const conn of serializedConnectors) {
            const lineConnector = new LineConnector(conn.lineId, conn.anchor, conn.ratio, conn.offset);
            manager.lineConnectors.set(lineConnector, new ShapeIdentifier(conn.targetId));
        }
        return manager;
    }

    static toSerializableConnector(
        lineConnector: LineConnector,
        targetOrIdentifier: ShapeIdentifier | AbstractShape,
    ): SerializableLineConnector {
        if (targetOrIdentifier instanceof AbstractShape) {
            return new SerializableLineConnector(
                lineConnector.lineId,
                lineConnector.anchor,
                targetOrIdentifier.id,
                lineConnector.ratio,
                lineConnector.offset,
            );
        } else {
            return new SerializableLineConnector(
                lineConnector.lineId,
                lineConnector.anchor,
                targetOrIdentifier.id,
                lineConnector.ratio,
                lineConnector.offset,
            );
        }
    }
}
