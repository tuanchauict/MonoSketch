/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.connector

import mono.graphics.geo.Point
import mono.graphics.geo.PointF
import mono.graphics.geo.Rect
import mono.shape.collection.Identifier
import mono.shape.shape.Line

/**
 * A connector for [Line].
 *
 * @param line The target of this connector
 * @param anchor The extra information for identifying which head of the line (start or end)
 * @param ratio The relative position of the connector based on the size of the box.
 * @param offset The absolute offset of the connector to the box
 */
class LineConnector(
    val line: Line,
    val anchor: Line.Anchor,
    val ratio: PointF,
    val offset: Point
) : Identifier by ConnectorIdentifier(line, anchor) {

    fun getRow(boxBound: Rect): Int =
        (boxBound.top + boxBound.height * ratio.top + offset.top).toInt()

    fun getColumn(boxBound: Rect): Int =
        (boxBound.left + boxBound.width * ratio.left + offset.left).toInt()

    /**
     * A [Identifier] of Line's connector.
     * This is a minimal version of [LineConnector] that only has required information to identify
     * the connector.
     */
    class ConnectorIdentifier(line: Line, anchor: Line.Anchor) : Identifier {
        override val id: String = toId(line, anchor)
    }

    companion object {
        fun toId(line: Line, anchor: Line.Anchor): String = "${line.id}@@${anchor.ordinal}"
    }
}
