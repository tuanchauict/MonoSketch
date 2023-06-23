/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.serialization

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mono.graphics.geo.Point
import mono.graphics.geo.PointF
import mono.shape.shape.Line

/**
 * A test for [SerializableLineConnector]
 */
class SerializableLineConnectorTest {
    @Test
    fun test() {
        val serializableLineConnector = SerializableLineConnector(
            "line",
            Line.Anchor.START,
            "target",
            PointF(1.2, 4.5),
            Point(5, 10)
        )

        val string = Json.encodeToString(serializableLineConnector)
        println(string)

        assertEquals(serializableLineConnector, Json.decodeFromString(string))
    }
}
