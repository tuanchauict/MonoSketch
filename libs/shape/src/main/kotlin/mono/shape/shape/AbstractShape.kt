/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.shape

import kotlin.random.Random
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.extra.NoExtra
import mono.shape.extra.ShapeExtra
import mono.shape.list.QuickList
import mono.shape.serialization.AbstractSerializableShape
import mono.uuid.UUID

/**
 * An abstract class which is used for defining all kinds of shape which are supported by the app.
 * Each shape will be assigned an id which is automatically generated or manually assigned. Two
 * shapes which have the same ID will be considered identical regardless the other attributes of
 * each kinds of shape class.
 *
 * Each shape's attributes might be changed and [versionCode] reflects the update. To ensure the
 * [versionCode]'s value is accurate, all properties modifying must be wrapped inside [update].
 *
 * @param id with null means the id will be automatically generated.
 */
sealed class AbstractShape(
    id: String?,
    internal var parentId: String? = null
) : QuickList.Identifier {
    override val id: String = id ?: UUID.generate()

    var versionCode: Int = nextVersionCode()
        protected set
    abstract val bound: Rect

    /**
     * Extra information which is specific to each shape.
     */
    open val extra: ShapeExtra = NoExtra

    abstract fun toSerializableShape(isIdIncluded: Boolean): AbstractSerializableShape

    open fun setBound(newBound: Rect) = Unit

    open fun setExtra(newExtra: ShapeExtra) = Unit

    /**
     * Updates properties of the shape by [action]. The [action] returns true if the shape's
     * properties are changed.
     */
    internal fun update(action: () -> Boolean) {
        val isChanged = action()
        if (isChanged) {
            versionCode = nextVersionCode(versionCode)
        }
    }

    open fun contains(point: Point): Boolean = bound.contains(point)

    open fun isOverlapped(rect: Rect): Boolean = bound.isOverlapped(rect)

    companion object {
        /**
         * Generates a new version code which is different from [excludedValue].
         */
        internal fun nextVersionCode(excludedValue: Int = 0): Int {
            var nextCode = Random.nextInt()
            // The probability of a new number is equal to old number is low, therefore, this loop
            // is short.
            while (nextCode == excludedValue) {
                nextCode = Random.nextInt()
            }
            return nextCode
        }
    }
}
