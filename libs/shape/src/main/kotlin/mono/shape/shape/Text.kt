package mono.shape.shape

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.graphics.geo.Size

/**
 * A text shape which contains a bound and a text.
 */
class Text(rect: Rect, parentId: Int? = null) : AbstractShape(parentId = parentId) {
    private var userSettingSize: Size = Size.ZERO
        set(value) {
            field = value.takeIf { it.width >= 3 && it.height >= 3 } ?: Size.ZERO
        }

    // Text can be auto resized by text
    override var bound: Rect = rect

    var text: String = ""

    constructor(startPoint: Point, endPoint: Point, parentId: Int?) : this(
        Rect.byLTRB(startPoint.left, startPoint.top, endPoint.left, endPoint.top),
        parentId
    )

    init {
        userSettingSize = rect.size
    }
}
