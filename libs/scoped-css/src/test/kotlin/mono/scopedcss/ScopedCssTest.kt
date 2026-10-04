/*
 * Copyright (c) 2026, tuanchauict
 */

package mono.scopedcss

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * A test for [scopedClassName]. The expected values must match the hashes produced by
 * `scoped-css.gradle`.
 */
class ScopedCssTest {
    @Test
    fun testScopedClassName() {
        assertEquals("s-ztntfp", scopedClassName(""))
        assertEquals("s-1r9wi7g", scopedClassName("a"))
        assertEquals("s-1gsnvr5", scopedClassName("display: flex;"))
    }
}
