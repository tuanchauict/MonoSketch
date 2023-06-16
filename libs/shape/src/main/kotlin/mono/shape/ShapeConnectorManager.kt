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
        val connector = LineConnector(
            line,
            anchor,
            calculateConnectorRatio(anchorPoint, shape.bound),
            calculateConnectorOffset(anchorPoint, shape.bound)
        )

        // TODO: Based on the position of anchor point and shape's bound, the direction of the 
        //  anchor must be updated to optimise the initial directions of the line

        lineConnectors[connector] = shape
    }

    fun removeConnector(line: Line, anchor: Line.Anchor) =
        lineConnectors.removeKey(LineConnector.ConnectorIdentifier(line, anchor))

    fun removeShape(shape: AbstractShape) = lineConnectors.removeValue(shape)

    private fun calculateConnectorRatio(
        anchorPoint: DirectedPoint,
        boxBound: Rect
    ): PointF {
        // TODO: Calculate the ratio based on anchor point and box bound
        return PointF(0.0, 0.0)
    }

    private fun calculateConnectorOffset(
        anchorPoint: DirectedPoint,
        boxBound: Rect
    ): Point {
        // TODO calculate the offset based on anchor point and box bound
        return Point.ZERO
    }
}
