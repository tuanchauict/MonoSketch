package mono.shape.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.list.QuickList

/**
 * An abstract class which is used for defining all kinds of shape which are supported by the app.
 * Each shape will be assigned an id which is automatically generated or manually assigned. Two
 * shapes which have the same ID will be considered identical regardless the other attributes of
 * each kinds of shape class.
 *
 * Each shape's attributes might be changed and [version] reflects the update. To ensure the
 * [version]'s value is accurate, all properties modifying must be wrapped inside [update].
 */
abstract class AbstractShape(
    override val id: Int = generateId(),
    internal var parentId: Int? = null
) : QuickList.Identifier {
    var version: Int = 0
        private set

    abstract val bound: Rect

    abstract fun contains(point: Point): Boolean

    open fun setBound(newBound: Rect) = Unit

    open fun isNewBoundAcceptable(newBound: Rect): Boolean = true

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

    companion object {
        private var NEXT_ID: Int = 1

        fun generateId(): Int {
            val id = NEXT_ID
            NEXT_ID += 1
            return id
        }
    }
}
