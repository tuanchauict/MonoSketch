package mono.graphics.shape

import mono.graphics.geo.Point
import mono.list.QuickList

/**
 * An abstract class which is used for defining all kinds of shape which are supported by the app.
 * Each shape will be assigned an id which is automatically generated or manually assigned. Two
 * shapes which have the same ID will be considered identical regardless the other attributes of
 * each kinds of shape class.
 */
abstract class AbstractShape(
    override val id: Int = generateId(),
    internal var parentId: Int? = null
) : QuickList.Identifier {
    abstract fun contains(point: Point): Boolean

    companion object {
        private var NEXT_ID: Int = 1

        fun generateId(): Int {
            val id = NEXT_ID
            NEXT_ID += 1
            return id
        }
    }
}
