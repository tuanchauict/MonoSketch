/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.connector

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.PointF
import mono.graphics.geo.Rect
import mono.shape.collection.TwoWayQuickMap
import mono.shape.connector.LineConnector.ConnectorIdentifier
import mono.shape.serialization.SerializableLineConnector
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

/**
 * A manager of shape connectors.
 */
class ShapeConnector {
    private val lineConnectors = TwoWayQuickMap<LineConnector, AbstractShape.ShapeIdentifier>()

    fun addConnector(line: Line, anchor: Line.Anchor, shape: AbstractShape) {
        val anchorPoint = when (anchor) {
            Line.Anchor.START -> line.startPoint
            Line.Anchor.END -> line.endPoint
        }
        val boxBound = shape.bound
        val (ratio, offset) = calculateConnectorRatioAndOffset(anchorPoint, boxBound) ?: return

        val connector = LineConnector(line.id, anchor, ratio, offset)

        // TODO: Based on the position of anchor point and shape's bound, the direction of the
        //  anchor must be updated to optimise the initial directions of the line

        lineConnectors[connector] = shape.identifier
    }

    fun getConnectors(shape: AbstractShape): Collection<LineConnector> =
        lineConnectors.getKeys(shape)

    fun removeConnector(line: Line, anchor: Line.Anchor) =
        lineConnectors.removeKey(ConnectorIdentifier(line.id, anchor))

    fun hasConnector(line: Line, anchor: Line.Anchor): Boolean =
        lineConnectors[ConnectorIdentifier(line.id, anchor)] != null

    fun removeShape(shape: AbstractShape) {
        lineConnectors.removeValue(shape)

        when (shape) {
            is Line -> {
                removeConnector(shape, Line.Anchor.START)
                removeConnector(shape, Line.Anchor.END)
            }

            is Group,
            is MockShape,
            is Rectangle,
            is Text -> Unit
        }
    }

    /**
     * Calculates the connector ratio. Returns null if the two cannot connect.
     */
    private fun calculateConnectorRatioAndOffset(
        anchorPoint: DirectedPoint,
        boxBound: Rect
    ): Pair<PointF, Point>? {
        val around = ShapeConnectorUseCase.getAround(anchorPoint, boxBound) ?: return null

        val ratio = ShapeConnectorUseCase.calculateRatio(around, anchorPoint, boxBound)
        val offset = ShapeConnectorUseCase.calculateOffset(around, anchorPoint, boxBound)
        return ratio to offset
    }

    fun toSerializable(): List<SerializableLineConnector> =
        lineConnectors.asSequence()
            .map { (lineConnector, target) -> toSerializableConnector(lineConnector, target) }
            .toList()

    companion object {
        fun fromSerializable(
            serializedConnectors: List<SerializableLineConnector>
        ): ShapeConnector {
            val manager = ShapeConnector()
            for (conn in serializedConnectors) {
                val lineConnector = LineConnector(conn.lineId, conn.anchor, conn.ratio, conn.offset)
                manager.lineConnectors[lineConnector] = AbstractShape.ShapeIdentifier(conn.targetId)
            }
            return manager
        }

        fun toSerializableConnector(
            lineConnector: LineConnector,
            target: AbstractShape.ShapeIdentifier
        ): SerializableLineConnector = SerializableLineConnector(
            lineId = lineConnector.lineId,
            anchor = lineConnector.anchor,
            targetId = target.id,
            lineConnector.ratio,
            lineConnector.offset
        )

        fun toSerializableConnector(
            lineConnector: LineConnector,
            target: AbstractShape
        ): SerializableLineConnector = SerializableLineConnector(
            lineId = lineConnector.lineId,
            anchor = lineConnector.anchor,
            targetId = target.id,
            lineConnector.ratio,
            lineConnector.offset
        )
    }
}
