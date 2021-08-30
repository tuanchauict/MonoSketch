package mono.shape.extra.style

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * A test for [StraightStrokeDashPattern].
 */
class StraightStrokeDashPatternTest {
    @Test
    fun testIsGap_solid() {
        val pattern = StraightStrokeDashPattern(1, 0, 0)
        assertFalse(pattern.isGap(0))
        assertFalse(pattern.isGap(1))
        assertFalse(pattern.isGap(2))
        assertFalse(pattern.isGap(3))
    }

    @Test
    fun testIsGap_1segment_1gap_0offset() {
        val pattern = StraightStrokeDashPattern(1, 1, 0)
        assertFalse(pattern.isGap(0))
        assertTrue(pattern.isGap(1))
        assertFalse(pattern.isGap(2))
        assertTrue(pattern.isGap(3))
        assertFalse(pattern.isGap(4))
        assertTrue(pattern.isGap(5))
    }

    @Test
    fun testIsGap_1segment_1gap_1offset() {
        val pattern = StraightStrokeDashPattern(1, 1, 1)
        assertTrue(pattern.isGap(0))
        assertFalse(pattern.isGap(1))
        assertTrue(pattern.isGap(2))
        assertFalse(pattern.isGap(3))
        assertTrue(pattern.isGap(4))
        assertFalse(pattern.isGap(5))
    }

    @Test
    fun testIsGap_1segment_1gap_minus1offset() {
        val pattern = StraightStrokeDashPattern(1, 1, -1)
        assertTrue(pattern.isGap(0))
        assertFalse(pattern.isGap(1))
        assertTrue(pattern.isGap(2))
        assertFalse(pattern.isGap(3))
        assertTrue(pattern.isGap(4))
        assertFalse(pattern.isGap(5))
    }

    @Test
    fun testIsGap_2segment_1gap_0offset() {
        val pattern = StraightStrokeDashPattern(2, 1, 0)
        assertFalse(pattern.isGap(0))
        assertFalse(pattern.isGap(1))
        assertTrue(pattern.isGap(2))
        assertFalse(pattern.isGap(3))
        assertFalse(pattern.isGap(4))
        assertTrue(pattern.isGap(5))
    }

    @Test
    fun testIsGap_2segment_1gap_1offset() {
        val pattern = StraightStrokeDashPattern(2, 1, 1)
        assertFalse(pattern.isGap(0))
        assertTrue(pattern.isGap(1))
        assertFalse(pattern.isGap(2))
        assertFalse(pattern.isGap(3))
        assertTrue(pattern.isGap(4))
        assertFalse(pattern.isGap(5))
    }
}
