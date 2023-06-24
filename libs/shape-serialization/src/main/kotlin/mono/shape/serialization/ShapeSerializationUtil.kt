/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.serialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mono.graphics.geo.Point

/**
 * A util object for serializing shape to Json and load shape from Json
 */
object ShapeSerializationUtil {
    fun toShapeJson(serializableShape: AbstractSerializableShape): String =
        Json.encodeToString(serializableShape)

    fun fromShapeJson(jsonString: String): AbstractSerializableShape? = try {
        Json.decodeFromString(jsonString)
    } catch (e: Exception) {
        console.error("Error while restoring shapes")
        console.error(e)
        null
    }

    fun toConnectorsJson(connectors: List<SerializableLineConnector>): String =
        Json.encodeToString(connectors)

    fun fromConnectorsJson(jsonString: String): List<SerializableLineConnector> = try {
        Json.decodeFromString(jsonString)
    } catch (e: Exception) {
        console.error("Error while restoring connectors")
        console.error(e)
        emptyList()
    }

    fun toMonoFileJson(name: String, serializableShape: SerializableGroup, offset: Point): String {
        val extra = Extra(name, offset)
        val monoFile = MonoFile(serializableShape, extra)
        return Json.encodeToString(monoFile)
    }

    fun fromMonoFileJson(jsonString: String): MonoFile? = try {
        Json.decodeFromString<MonoFile>(jsonString)
    } catch (e: Exception) {
        // Fallback to version 0
        val shape = fromShapeJson(jsonString) as? SerializableGroup
        if (shape != null) {
            MonoFile(shape, Extra("", Point.ZERO))
        } else {
            null
        }
    }
}
