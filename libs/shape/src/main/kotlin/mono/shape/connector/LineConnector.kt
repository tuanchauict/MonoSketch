/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.connector

import mono.graphics.geo.Point
import mono.graphics.geo.PointF
import mono.shape.collection.Identifier
import mono.shape.shape.Line

/**
 * A connector for [Line].
 *
 * @param lineId The id of the target of this connector
 * @param anchor The extra information for identifying which head of the line (start or end)
 * @param ratio The relative position of the connector based on the size of the box.
 * @param offset The absolute offset of the connector to the box
 */
class LineConnector(
    val lineId: String,
    val anchor: Line.Anchor,
    val ratio: PointF,
    val offset: Point
) : Identifier by ConnectorIdentifier(lineId, anchor) {

    /**
     * A [Identifier] of Line's connector.
     * This is a minimal version of [LineConnector] that only has required information to identify
     * the connector.
     */
    class ConnectorIdentifier(lineId: String, anchor: Line.Anchor) : Identifier {
        override val id: String = toId(lineId, anchor)
    }

    companion object {
        fun toId(lineId: String, anchor: Line.Anchor): String = "${lineId}@@${anchor.ordinal}"
    }
}
