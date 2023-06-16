/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.connector

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.DirectedPoint.Direction.HORIZONTAL
import mono.graphics.geo.DirectedPoint.Direction.VERTICAL
import mono.graphics.geo.Rect
import mono.shape.connector.ShapeConnectorUseCase.Around

class ShapeConnectorUseCaseTest {
    @Test
    fun testGetAround_notAround() {
        val rect = Rect.byLTRB(4, 20, 10, 30)

        val nonAroundPoints = listOf(
            // Around left edge
            DirectedPoint(HORIZONTAL, 2, 25),
            DirectedPoint(HORIZONTAL, 6, 25),
            // Around right edge
            DirectedPoint(HORIZONTAL, 8, 25),
            DirectedPoint(HORIZONTAL, 12, 25),
            // Around top edge
            DirectedPoint(HORIZONTAL, 6, 18),
            DirectedPoint(HORIZONTAL, 6, 22),
            // Around bottom edge
            DirectedPoint(HORIZONTAL, 6, 28),
            DirectedPoint(HORIZONTAL, 6, 32),
            // On left edge - outside top
            DirectedPoint(HORIZONTAL, 3, 18),
            DirectedPoint(HORIZONTAL, 4, 18),
            DirectedPoint(HORIZONTAL, 5, 18),
            // On left edge - outside bottom
            DirectedPoint(HORIZONTAL, 3, 32),
            DirectedPoint(HORIZONTAL, 4, 32),
            DirectedPoint(HORIZONTAL, 5, 32),
            // On right edge - outside top
            DirectedPoint(HORIZONTAL, 9, 18),
            DirectedPoint(HORIZONTAL, 10, 18),
            DirectedPoint(HORIZONTAL, 11, 18),
            // On right edge - outside bottom
            DirectedPoint(HORIZONTAL, 9, 32),
            DirectedPoint(HORIZONTAL, 10, 32),
            DirectedPoint(HORIZONTAL, 11, 32),
            // On top edge - outside left
            DirectedPoint(HORIZONTAL, 2, 19),
            DirectedPoint(HORIZONTAL, 2, 20),
            DirectedPoint(HORIZONTAL, 2, 21),
            // On top edge - outside right
            DirectedPoint(HORIZONTAL, 12, 19),
            DirectedPoint(HORIZONTAL, 12, 20),
            DirectedPoint(HORIZONTAL, 12, 21),
            // On bottom edge - outside left
            DirectedPoint(HORIZONTAL, 2, 29),
            DirectedPoint(HORIZONTAL, 2, 30),
            DirectedPoint(HORIZONTAL, 2, 31),
            // On bottom edge - outside right
            DirectedPoint(HORIZONTAL, 12, 29),
            DirectedPoint(HORIZONTAL, 12, 30),
            DirectedPoint(HORIZONTAL, 12, 31)
        )

        for (point in nonAroundPoints) {
            println(point)
            assertNull(ShapeConnectorUseCase.getAround(point, rect))
        }
    }

    @Test
    fun testGetAround_Left() {
        val rect = Rect.byLTRB(4, 20, 10, 30)
        val leftPointsToExpectedAround = listOf(
            // No conflict
            DirectedPoint(HORIZONTAL, 3, 25) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 4, 25) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 5, 25) to Around.LEFT,
            DirectedPoint(VERTICAL, 3, 25) to Around.LEFT,
            DirectedPoint(VERTICAL, 4, 25) to Around.LEFT,
            DirectedPoint(VERTICAL, 5, 25) to Around.LEFT,
            // Conflict
            // Horizontal, top edge -> left
            DirectedPoint(HORIZONTAL, 3, 20) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 4, 20) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 5, 20) to Around.LEFT,
            // Vertical, top edge -> top
            DirectedPoint(VERTICAL, 3, 20) to Around.TOP,
            DirectedPoint(VERTICAL, 4, 20) to Around.TOP,
            DirectedPoint(VERTICAL, 5, 20) to Around.TOP,
            // Horizontal, bottom edge -> left
            DirectedPoint(HORIZONTAL, 3, 30) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 4, 30) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 5, 30) to Around.LEFT,
            // Vertical, bottom edge -> bottom
            DirectedPoint(VERTICAL, 3, 30) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 4, 30) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 5, 30) to Around.BOTTOM,
        )

        for ((point, expected) in leftPointsToExpectedAround) {
            println(point)
            assertEquals(expected, ShapeConnectorUseCase.getAround(point, rect))
        }
    }

    @Test
    fun testGetAround_Right() {
        val rect = Rect.byLTRB(4, 20, 10, 30)
        val leftPointsToExpectedAround = listOf(
            // No conflict
            DirectedPoint(HORIZONTAL, 9, 25) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 10, 25) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 11, 25) to Around.RIGHT,
            DirectedPoint(VERTICAL, 9, 25) to Around.RIGHT,
            DirectedPoint(VERTICAL, 10, 25) to Around.RIGHT,
            DirectedPoint(VERTICAL, 11, 25) to Around.RIGHT,
            // Conflict
            // Horizontal, top edge -> left
            DirectedPoint(HORIZONTAL, 9, 20) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 10, 20) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 11, 20) to Around.RIGHT,
            // Vertical, top edge -> top
            DirectedPoint(VERTICAL, 9, 20) to Around.TOP,
            DirectedPoint(VERTICAL, 10, 20) to Around.TOP,
            DirectedPoint(VERTICAL, 11, 20) to Around.TOP,
            // Horizontal, bottom edge -> left
            DirectedPoint(HORIZONTAL, 9, 30) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 10, 30) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 11, 30) to Around.RIGHT,
            // Vertical, bottom edge -> bottom
            DirectedPoint(VERTICAL, 9, 30) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 10, 30) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 11, 30) to Around.BOTTOM,
        )

        for ((point, expected) in leftPointsToExpectedAround) {
            println(point)
            assertEquals(expected, ShapeConnectorUseCase.getAround(point, rect))
        }
    }

    @Test
    fun testGetAround_Top() {
        val rect = Rect.byLTRB(4, 20, 10, 30)
        val leftPointsToExpectedAround = listOf(
            // No conflict
            DirectedPoint(HORIZONTAL, 6, 19) to Around.TOP,
            DirectedPoint(HORIZONTAL, 6, 20) to Around.TOP,
            DirectedPoint(HORIZONTAL, 6, 21) to Around.TOP,
            DirectedPoint(VERTICAL, 6, 19) to Around.TOP,
            DirectedPoint(VERTICAL, 6, 20) to Around.TOP,
            DirectedPoint(VERTICAL, 6, 21) to Around.TOP,
            // Conflict
            // Vertical, left edge -> top
            DirectedPoint(VERTICAL, 4, 19) to Around.TOP,
            DirectedPoint(VERTICAL, 4, 20) to Around.TOP,
            DirectedPoint(VERTICAL, 4, 21) to Around.TOP,
            // Horizontal, left edge -> left
            DirectedPoint(HORIZONTAL, 4, 19) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 4, 20) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 4, 21) to Around.LEFT,
            // Vertical, right edge -> top
            DirectedPoint(VERTICAL, 10, 19) to Around.TOP,
            DirectedPoint(VERTICAL, 10, 20) to Around.TOP,
            DirectedPoint(VERTICAL, 10, 21) to Around.TOP,
            // Horizontal, right edge -> right
            DirectedPoint(HORIZONTAL, 10, 19) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 10, 20) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 10, 21) to Around.RIGHT,
        )

        for ((point, expected) in leftPointsToExpectedAround) {
            println(point)
            assertEquals(expected, ShapeConnectorUseCase.getAround(point, rect))
        }
    }

    @Test
    fun testGetAround_Bottom() {
        val rect = Rect.byLTRB(4, 20, 10, 30)
        val leftPointsToExpectedAround = listOf(
            // No conflict
            DirectedPoint(HORIZONTAL, 6, 29) to Around.BOTTOM,
            DirectedPoint(HORIZONTAL, 6, 30) to Around.BOTTOM,
            DirectedPoint(HORIZONTAL, 6, 31) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 6, 29) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 6, 30) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 6, 31) to Around.BOTTOM,
            // Conflict
            // Vertical, left edge -> bottom
            DirectedPoint(VERTICAL, 4, 29) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 4, 30) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 4, 31) to Around.BOTTOM,
            // Horizontal, left edge -> left
            DirectedPoint(HORIZONTAL, 4, 29) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 4, 30) to Around.LEFT,
            DirectedPoint(HORIZONTAL, 4, 31) to Around.LEFT,
            // Vertical, right edge -> bottom
            DirectedPoint(VERTICAL, 10, 29) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 10, 30) to Around.BOTTOM,
            DirectedPoint(VERTICAL, 10, 31) to Around.BOTTOM,
            // Horizontal, right edge -> right
            DirectedPoint(HORIZONTAL, 10, 29) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 10, 30) to Around.RIGHT,
            DirectedPoint(HORIZONTAL, 10, 31) to Around.RIGHT,
        )

        for ((point, expected) in leftPointsToExpectedAround) {
            println(point)
            assertEquals(expected, ShapeConnectorUseCase.getAround(point, rect))
        }
    }

    // TODO: Add unit test for calculateRatio and calculateOffset
}
