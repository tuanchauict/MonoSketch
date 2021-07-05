package mono.shape.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.list.QuickList
import mono.shape.serialization.AbstractSerializableShape
import mono.uuid.UUID

/**
 * An abstract class which is used for defining all kinds of shape which are supported by the app.
 * Each shape will be assigned an id which is automatically generated or manually assigned. Two
 * shapes which have the same ID will be considered identical regardless the other attributes of
 * each kinds of shape class.
 *
 * Each shape's attributes might be changed and [version] reflects the update. To ensure the
 * [version]'s value is accurate, all properties modifying must be wrapped inside [update].
 *
 * @param id with null means the id will be automatically generated.
 */
sealed class AbstractShape(
    id: String?,
    internal var parentId: String? = null
) : QuickList.Identifier {
    override val id: String = id ?: UUID.generate()

    var version: Int = 0
        private set
    abstract val bound: Rect

    /**
     * Extra information which is specific to each shape.
     */
    open val extra: Any = Unit

    abstract fun toSerializableShape(): AbstractSerializableShape

    open fun setBound(newBound: Rect) = Unit

    open fun setExtra(extraUpdater: ExtraUpdater) = Unit

    open fun isValid(): Boolean = true

    /**
     * Updates properties of the shape by [action]. The [action] returns true if the shape's
     * properties are changed.
     */
    internal fun update(action: () -> Boolean) {
        val isChanged = action()
        if (isChanged) {
            version++
        }
    }

    open fun contains(point: Point): Boolean = bound.contains(point)

    /**
     * An interface which is used for updating extra value.
     */
    interface ExtraUpdater
}
