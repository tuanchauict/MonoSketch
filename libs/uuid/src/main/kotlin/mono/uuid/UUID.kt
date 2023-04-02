/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.uuid

import kotlin.js.Date
import kotlin.random.Random

/**
 * An object class that generate unique ID.
 *
 * Version 1:
 * ```
 * Structure: version datetime random random
 * Length   :   3        8      10     10
 * ```
 * Version: 01-
 * Datetime: Base64-like encoded current time (8 chars)
 * Random: Base64-like encoded random number (10 chars)
 */
object UUID {
    const val VERSION = 1

    private val BASE64_CHARS =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+=".toCharArray()

    fun generate(): String {
        val part1 = Date().getTime().toBits().toBase64().dropLast(3)
        val part2 = Random.nextLong().toBase64()
        val part3 = Random.nextLong().toBase64()
        return "0$VERSION-$part1$part2$part3"
    }

    private fun Long.toBase64(): String {
        var number = this
        val chars = CharArray(10)
        for (i in chars.indices) {
            val v = number and 0b0111111
            number = number ushr 6
            chars[i] = BASE64_CHARS[v.toInt()]
        }
        return chars.joinToString("")
    }
}
