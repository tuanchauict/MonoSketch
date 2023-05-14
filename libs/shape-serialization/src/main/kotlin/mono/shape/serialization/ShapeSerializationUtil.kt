/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.serialization

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * A util object for serializing shape to Json and load shape from Json
 */
object ShapeSerializationUtil {
    fun toJson(serializableShape: AbstractSerializableShape): String =
        Json.encodeToString(serializableShape)

    fun fromJson(jsonString: String): AbstractSerializableShape? = try {
        Json.decodeFromString(jsonString)
    } catch (e: Exception) {
        console.error("Error while restoring shapes")
        console.error(e)
        null
    }

    fun toMonoFileJson(name: String, serializableShape: SerializableGroup): String {
        val monoFile = MonoFile(serializableShape, Extra(name))
        return Json.encodeToString(monoFile)
    }

    fun fromMonoFileJson(jsonString: String): MonoFile? = try {
        Json.decodeFromString<MonoFile>(jsonString)
    } catch (e: SerializationException) {
        // Fallback to version 0
        val shape = Json.decodeFromString<SerializableGroup>(jsonString)
        MonoFile(shape, Extra(""))
    } catch (e: Exception) {
        console.error("Error while restoring shapes")
        console.error(e)
        null
    }
}
