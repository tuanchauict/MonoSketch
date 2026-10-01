/*
 * Copyright (c) 2026, tuanchauict
 */

package mono.scopedcss

import org.intellij.lang.annotations.Language

/**
 * Declares a block of SCSS colocated with the component that uses it and returns the scoped
 * class name to apply on the component's root element.
 *
 * The body is the content of a rule: declarations apply to the root element, nested selectors
 * (`&:hover`, `.child`, `:root.dark &`) work as in SCSS.
 *
 * This function does not inject any style at runtime. The `extractScopedCss` Gradle task scans
 * the sources for calls to this function, wraps each body in `.<class-name> { ... }` and compiles
 * it together with `main.scss`. Both sides derive the class name from the content of [source], so
 * they always agree. Consequently, [source] must be a raw string literal passed directly to this
 * function, without string templates. To use SCSS variables, use a multi-dollar string:
 * `css($$"""height: $nav-height;""")`.
 */
fun css(@Language("SCSS") source: String): String = scopedClassName(source)

/**
 * FNV-1a 32-bit hash over UTF-16 code units, encoded in base 36.
 * Must stay in sync with `scopedClassName` in `scoped-css.gradle`.
 */
internal fun scopedClassName(source: String): String {
    var hash = FNV_OFFSET_BASIS
    for (char in source) {
        hash = (hash xor char.code) * FNV_PRIME
    }
    return "s-" + hash.toUInt().toString(36)
}

private const val FNV_OFFSET_BASIS = 0x811c9dc5.toInt()
private const val FNV_PRIME = 0x01000193
