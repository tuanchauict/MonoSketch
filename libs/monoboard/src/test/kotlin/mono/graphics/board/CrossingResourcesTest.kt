/*
 * Copyright (c) 2024, tuanchauict
 */

package mono.graphics.board

import kotlin.test.Test
import kotlin.test.assertEquals
import mono.graphics.board.CrossingResources.createExcludeMask
import mono.graphics.board.CrossingResources.getCharMask
import mono.graphics.board.CrossingResources.maskToString

/**
 * A test for [CrossingResources].
 */
class CrossingResourcesTest {
    @Test
    fun testExcludeMask() {
        console.log(maskToString(getCharMask('║', CrossingResources.MASK_CROSS)))
        console.log(maskToString(createExcludeMask(getCharMask('║', CrossingResources.MASK_CROSS))))
        assertEquals(
            0b001100110011,
            createExcludeMask(getCharMask('│', CrossingResources.MASK_CROSS))
        )
        assertEquals(
            0b001100110011,
            createExcludeMask(getCharMask('┃', CrossingResources.MASK_CROSS))
        )
        assertEquals(
            0b001100110011,
            createExcludeMask(getCharMask('║', CrossingResources.MASK_CROSS))
        )

        assertEquals(
            0b0110011001100,
            createExcludeMask(getCharMask('─', CrossingResources.MASK_CROSS))
        )
        assertEquals(
            0b0110011001100,
            createExcludeMask(getCharMask('━', CrossingResources.MASK_CROSS))
        )
        assertEquals(
            0b0110011001100,
            createExcludeMask(getCharMask('═', CrossingResources.MASK_CROSS))
        )
    }
}
