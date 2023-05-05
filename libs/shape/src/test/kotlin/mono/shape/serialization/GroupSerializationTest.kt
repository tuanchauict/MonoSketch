/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.serialization

import kotlin.test.Test
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mono.graphics.geo.Rect
import mono.shape.ShapeExtraManager

class GroupSerializationTest {
    @Test
    fun test() {
        val rectangle = SerializableRectangle(
            null,
            0,
            Rect.byLTRB(0, 0, 1, 1),
            ShapeExtraManager.defaultRectangleExtra.toSerializableExtra()
        )
        val group = SerializableGroup(
            null,
            0,
            listOf(rectangle)
        )
        val string = Json.encodeToString(group)
        println(string)
        val g = Json.decodeFromString<SerializableGroup>(string)
        println(g)
    }
}
