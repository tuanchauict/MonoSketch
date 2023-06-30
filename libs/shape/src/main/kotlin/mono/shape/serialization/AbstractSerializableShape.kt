/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.shape.AbstractShape

@Serializable
sealed class AbstractSerializableShape {
    /**
     * The id of this shape.
     * If this is null, the shape does not have id and will be assigned a new id when created.
     * This value is only for serialization and reference purpose, do not read this value directly
     * when creating a shape, instead, use [actualId].
     */
    abstract val id: String?

    /**
     * A flag indicates that, if this value is true, even if the [id] is not null, the id is still
     * unavailable. This is similar to when id is null but the temporary id is used for function
     * like copy-paste.
     */
    abstract val isIdTemporary: Boolean
    abstract val versionCode: Int

    val actualId: String?
        get() = if (isIdTemporary) null else id
}

@Serializable
@SerialName("R")
data class SerializableRectangle(
    @SerialName("i")
    override val id: String? = null,
    @SerialName("idtemp")
    override val isIdTemporary: Boolean = false,
    @SerialName("v")
    override val versionCode: Int,
    @SerialName("b")
    val bound: Rect,
    @SerialName("e")
    val extra: SerializableExtra
) : AbstractSerializableShape() {

    @Serializable
    data class SerializableExtra(
        @SerialName("fe")
        val isFillEnabled: Boolean,
        @SerialName("fu")
        val userSelectedFillStyleId: String,
        @SerialName("be")
        val isBorderEnabled: Boolean,
        @SerialName("bu")
        val userSelectedBorderStyleId: String,
        @SerialName("du")
        val dashPattern: String,
        @SerialName("rc")
        val corner: String = ""
    )
}

@Serializable
@SerialName("T")
data class SerializableText(
    @SerialName("i")
    override val id: String? = null,
    @SerialName("idtemp")
    override val isIdTemporary: Boolean = false,
    @SerialName("v")
    override val versionCode: Int = AbstractShape.nextVersionCode(),
    @SerialName("b")
    val bound: Rect,
    @SerialName("t")
    val text: String,
    @SerialName("e")
    val extra: SerializableExtra,
    @SerialName("te")
    val isTextEditable: Boolean = true
) : AbstractSerializableShape() {

    @Serializable
    data class SerializableExtra(
        @SerialName("be")
        val boundExtra: SerializableRectangle.SerializableExtra,
        @SerialName("tha")
        val textHorizontalAlign: Int,
        @SerialName("tva")
        val textVerticalAlign: Int
    )
}

@Serializable
@SerialName("L")
data class SerializableLine(
    @SerialName("i")
    override val id: String? = null,
    @SerialName("idtemp")
    override val isIdTemporary: Boolean = false,
    @SerialName("v")
    override val versionCode: Int,
    @SerialName("ps")
    val startPoint: DirectedPoint,
    @SerialName("pe")
    val endPoint: DirectedPoint,
    @SerialName("jps")
    val jointPoints: List<Point>,
    @SerialName("e")
    val extra: SerializableExtra,
    @SerialName("em")
    val wasMovingEdge: Boolean
) : AbstractSerializableShape() {

    @Serializable
    data class SerializableExtra(
        @SerialName("se")
        val isStrokeEnabled: Boolean = true,
        @SerialName("su")
        val userSelectedStrokeStyleId: String,
        @SerialName("ase")
        val isStartAnchorEnabled: Boolean = false,
        @SerialName("asu")
        val userSelectedStartAnchorId: String,
        @SerialName("aee")
        val isEndAnchorEnabled: Boolean = false,
        @SerialName("aeu")
        val userSelectedEndAnchorId: String,
        @SerialName("du")
        val dashPattern: String,
        @SerialName("rc")
        val isRoundedCorner: Boolean = false
    )
}

@Serializable
@SerialName("G")
data class SerializableGroup(
    @SerialName("i")
    override val id: String? = null,
    @SerialName("idtemp")
    override val isIdTemporary: Boolean = false,
    @SerialName("v")
    override val versionCode: Int,
    @SerialName("ss")
    val shapes: List<AbstractSerializableShape>
) : AbstractSerializableShape()
