/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.shape

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.shape.serialization.SerializableLine
import mono.shape.shape.line.LineHelper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * A test for [Line]
 */
class LineTest {
    @Test
    fun testSerialization_init() {
        val startPoint = DirectedPoint(DirectedPoint.Direction.VERTICAL, 1, 2)
        val endPoint = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 3, 4)
        val line = Line(startPoint, endPoint, parentId = PARENT_ID)

        val serializableLine = line.toSerializableShape(true) as SerializableLine
        assertEquals(startPoint, serializableLine.startPoint)
        assertEquals(endPoint, serializableLine.endPoint)
        assertEquals(line.reducedJoinPoints, LineHelper.reduce(serializableLine.jointPoints))
        assertEquals(line.extra.toSerializableExtra(), serializableLine.extra)
        assertFalse(serializableLine.wasMovingEdge)
    }

    @Test
    fun testSerialization_moveAnchorPoints() {
        val startPoint = DirectedPoint(DirectedPoint.Direction.VERTICAL, 1, 2)
        val endPoint = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 3, 4)
        val line = Line(startPoint, endPoint, parentId = PARENT_ID)

        val newStartPoint = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 4, 5)
        val newEndPoint = DirectedPoint(DirectedPoint.Direction.VERTICAL, 7, 8)
        line.moveAnchorPoint(
            Line.AnchorPointUpdate(Line.Anchor.START, newStartPoint),
            isReduceRequired = true
        )
        line.moveAnchorPoint(
            Line.AnchorPointUpdate(Line.Anchor.END, newEndPoint),
            isReduceRequired = true
        )

        val serializableLine = line.toSerializableShape(true) as SerializableLine
        assertEquals(newStartPoint, serializableLine.startPoint)
        assertEquals(newEndPoint, serializableLine.endPoint)
        assertEquals(line.reducedJoinPoints, LineHelper.reduce(serializableLine.jointPoints))
        assertEquals(line.extra.toSerializableExtra(), serializableLine.extra)
        assertFalse(serializableLine.wasMovingEdge)
    }

    @Test
    fun testSerialization_moveEdge() {
        val startPoint = DirectedPoint(DirectedPoint.Direction.VERTICAL, 1, 2)
        val endPoint = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 3, 4)
        val line = Line(startPoint, endPoint, parentId = PARENT_ID)
        line.moveEdge(line.edges.first().id, Point(10, 10), isReduceRequired = true)

        val serializableLine = line.toSerializableShape(true) as SerializableLine
        assertEquals(startPoint, serializableLine.startPoint)
        assertEquals(endPoint, serializableLine.endPoint)
        assertEquals(line.reducedJoinPoints, LineHelper.reduce(serializableLine.jointPoints))
        assertEquals(line.extra.toSerializableExtra(), serializableLine.extra)
        assertTrue(serializableLine.wasMovingEdge)
    }

    @Test
    fun testSerialization_restoreInit() {
        val startPoint = DirectedPoint(DirectedPoint.Direction.VERTICAL, 1, 2)
        val endPoint = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 3, 4)
        val line = Line(startPoint, endPoint, parentId = PARENT_ID)

        val serializableLine = line.toSerializableShape(true) as SerializableLine
        val line2 = Line(serializableLine, parentId = PARENT_ID)
        assertEquals(line.getDirection(Line.Anchor.START), line2.getDirection(Line.Anchor.START))
        assertEquals(line.getDirection(Line.Anchor.END), line2.getDirection(Line.Anchor.END))
        assertEquals(line.reducedJoinPoints, line2.reducedJoinPoints)
        assertEquals(line.extra, line2.extra)
        assertEquals(line.wasMovingEdge(), line2.wasMovingEdge())
    }

    @Test
    fun testSerialization_restoreAfterMovingAnchorPoints() {
        val startPoint = DirectedPoint(DirectedPoint.Direction.VERTICAL, 1, 2)
        val endPoint = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 3, 4)
        val line = Line(startPoint, endPoint, parentId = PARENT_ID)

        val newStartPoint = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 4, 5)
        val newEndPoint = DirectedPoint(DirectedPoint.Direction.VERTICAL, 7, 8)
        line.moveAnchorPoint(
            Line.AnchorPointUpdate(Line.Anchor.START, newStartPoint),
            isReduceRequired = true
        )
        line.moveAnchorPoint(
            Line.AnchorPointUpdate(Line.Anchor.END, newEndPoint),
            isReduceRequired = true
        )

        val serializableLine = line.toSerializableShape(true) as SerializableLine
        val line2 = Line(serializableLine, parentId = PARENT_ID)
        assertEquals(line.getDirection(Line.Anchor.START), line2.getDirection(Line.Anchor.START))
        assertEquals(line.getDirection(Line.Anchor.END), line2.getDirection(Line.Anchor.END))
        assertEquals(line.reducedJoinPoints, line2.reducedJoinPoints)
        assertEquals(line.extra, line2.extra)
        assertEquals(line.wasMovingEdge(), line2.wasMovingEdge())
    }

    @Test
    fun testSerialization_restoreAfterMovingEdge() {
        val startPoint = DirectedPoint(DirectedPoint.Direction.VERTICAL, 1, 2)
        val endPoint = DirectedPoint(DirectedPoint.Direction.HORIZONTAL, 3, 4)
        val line = Line(startPoint, endPoint, parentId = PARENT_ID)
        line.moveEdge(line.edges.first().id, Point(10, 10), isReduceRequired = true)

        val serializableLine = line.toSerializableShape(true) as SerializableLine
        val line2 = Line(serializableLine, PARENT_ID)
        assertEquals(line.getDirection(Line.Anchor.START), line2.getDirection(Line.Anchor.START))
        assertEquals(line.getDirection(Line.Anchor.END), line2.getDirection(Line.Anchor.END))
        assertEquals(line.reducedJoinPoints, line2.reducedJoinPoints)
        assertEquals(line.extra, line2.extra)
        assertEquals(line.wasMovingEdge(), line2.wasMovingEdge())
    }

    companion object {
        private const val PARENT_ID = "1"
    }
}
