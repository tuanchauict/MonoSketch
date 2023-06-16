/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.PointF
import mono.graphics.geo.Rect
import mono.shape.collection.TwoWayQuickMap
import mono.shape.connector.LineConnector
import mono.shape.connector.ShapeConnectorUseCase
import mono.shape.shape.AbstractShape
import mono.shape.shape.Line

/**
 * A manager of shape connectors.
 */
class ShapeConnectorManager {
    private val lineConnectors = TwoWayQuickMap<LineConnector, AbstractShape>()

    fun addConnector(line: Line, anchor: Line.Anchor, shape: AbstractShape) {
        val anchorPoint = when (anchor) {
            Line.Anchor.START -> line.startPoint
            Line.Anchor.END -> line.endPoint
        }
        val boxBound = shape.bound
        val (ratio, offset) = calculateConnectorRatioAndOffset(anchorPoint, boxBound) ?: return

        val connector =
            LineConnector(line, anchor, ratio, offset)

        // TODO: Based on the position of anchor point and shape's bound, the direction of the 
        //  anchor must be updated to optimise the initial directions of the line

        lineConnectors[connector] = shape
    }

    fun removeConnector(line: Line, anchor: Line.Anchor) =
        lineConnectors.removeKey(LineConnector.ConnectorIdentifier(line, anchor))

    fun removeShape(shape: AbstractShape) = lineConnectors.removeValue(shape)

    /**
     * Calculates the connector ratio. Returns null if the two cannot connect.
     */
    internal fun calculateConnectorRatioAndOffset(
        anchorPoint: DirectedPoint,
        boxBound: Rect
    ): Pair<PointF, Point>? {
        val around = ShapeConnectorUseCase.getAround(anchorPoint, boxBound) ?: return null

        val ratio = ShapeConnectorUseCase.calculateRatio(around, anchorPoint, boxBound)
        val offset = ShapeConnectorUseCase.calculateOffset(around, anchorPoint, boxBound)
        return ratio to offset
    }
}
