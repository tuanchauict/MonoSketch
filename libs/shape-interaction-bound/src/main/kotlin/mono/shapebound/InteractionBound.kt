package mono.shapebound

import mono.graphics.geo.Rect

/**
 * A sealed class to define all possible interaction bound types.
 */
sealed class InteractionBound

/**
 * A class which defines interaction bound for scalable shapes.
 */
class ScalableInteractionBound(shapeBound: Rect) : InteractionBound() {
    val left: Double = shapeBound.left.toDouble()
    val top: Double = shapeBound.top.toDouble()
    val right: Double = shapeBound.right + 1.0
    val bottom: Double = shapeBound.bottom + 1.0

    private val horizontalMiddle: Double = (left + right) / 2.0
    private val verticalMiddle: Double = (top + bottom) / 2.0

    val interactionPoints: List<ScaleInteractionPoint> = listOf(
        ScaleInteractionPoint.TopLeft(left, top),
        ScaleInteractionPoint.TopMiddle(horizontalMiddle, top),
        ScaleInteractionPoint.TopRight(right, top),
        ScaleInteractionPoint.MiddleLeft(left, verticalMiddle),
        ScaleInteractionPoint.MiddleRight(right, verticalMiddle),
        ScaleInteractionPoint.BottomLeft(left, bottom),
        ScaleInteractionPoint.BottomMiddle(horizontalMiddle, bottom),
        ScaleInteractionPoint.BottomRight(right, bottom),
    )
}
