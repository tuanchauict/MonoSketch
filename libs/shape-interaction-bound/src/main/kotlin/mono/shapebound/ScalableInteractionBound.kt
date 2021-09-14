package mono.shapebound

import mono.graphics.geo.Rect

/**
 * A class which defines interaction bound for scalable shapes.
 */
class ScalableInteractionBound(
    targetedShapeId: String,
    shapeBound: Rect
) : InteractionBound() {
    val left: Double = shapeBound.left.toDouble() - 0.25
    val top: Double = shapeBound.top.toDouble() - 0.25
    val right: Double = shapeBound.right + 1.0 + 0.25
    val bottom: Double = shapeBound.bottom + 1.0 + 0.25

    private val horizontalMiddle: Double = (left + right) / 2.0
    private val verticalMiddle: Double = (top + bottom) / 2.0

    override val interactionPoints: List<ScaleInteractionPoint> = listOf(
        ScaleInteractionPoint.TopLeft(targetedShapeId, left, top),
        ScaleInteractionPoint.TopMiddle(targetedShapeId, horizontalMiddle, top),
        ScaleInteractionPoint.TopRight(targetedShapeId, right, top),
        ScaleInteractionPoint.MiddleLeft(targetedShapeId, left, verticalMiddle),
        ScaleInteractionPoint.MiddleRight(targetedShapeId, right, verticalMiddle),
        ScaleInteractionPoint.BottomLeft(targetedShapeId, left, bottom),
        ScaleInteractionPoint.BottomMiddle(targetedShapeId, horizontalMiddle, bottom),
        ScaleInteractionPoint.BottomRight(targetedShapeId, right, bottom),
    )
}
