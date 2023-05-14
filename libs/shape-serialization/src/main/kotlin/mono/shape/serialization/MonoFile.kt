/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mono.common.currentTimeMillis

const val MONO_FILE_VERSION = 1

/**
 * A data class for serializing shape to Json and load shape from Json.
 *
 */
@Serializable
data class MonoFile internal constructor(
    @SerialName("root")
    val root: SerializableGroup,
    @SerialName("extra")
    val extra: Extra,
    @SerialName("version")
    val version: Int,
    @SerialName("modified_timestamp_millis")
    val modifiedTimestampMillis: Long
) {
    constructor(root: SerializableGroup, extra: Extra) : this(
        root,
        extra,
        MONO_FILE_VERSION,
        currentTimeMillis().toLong()
    )
}

@Serializable
data class Extra(
    @SerialName("name")
    val name: String
)
