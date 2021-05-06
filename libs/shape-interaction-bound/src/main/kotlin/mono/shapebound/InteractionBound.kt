package mono.shapebound

import mono.graphics.geo.Rect
import mono.shape.shape.Line

/**
 * A sealed class to define all possible interaction bound types.
 */
sealed class InteractionBound {
    abstract val interactionPoints: List<InteractionPoint>
}

/**
 * A class which defines interaction bound for scalable shapes.
 */
class ScalableInteractionBound(
    targetedShapeId: Int,
    shapeBound: Rect
) : InteractionBound() {
    val left: Double = shapeBound.left.toDouble()
    val top: Double = shapeBound.top.toDouble()
    val right: Double = shapeBound.right + 1.0
    val bottom: Double = shapeBound.bottom + 1.0

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

/**
 * A class which defines interaction bound for Line shapes.
 */
class LineInteractionBound(
    private val targetedShapeId: Int,
    private val edges: List<Line.Edge>
) : InteractionBound() {
    override val interactionPoints: List<InteractionPoint>

    init {
        val anchorPoints = listOf(
            createInteractionAnchor(Line.Anchor.START),
            createInteractionAnchor(Line.Anchor.END)
        )
        val middleEdgePoints = edges.map {
            LineInteractionPoint.Edge(
                targetedShapeId,
                it.id,
                left = it.middleLeft + 0.5,
                top = it.middleTop + 0.5
            )
        }

        interactionPoints = anchorPoints + middleEdgePoints
    }

    private fun createInteractionAnchor(
        anchor: Line.Anchor,
    ): LineInteractionPoint.Anchor {
        val edge = if (anchor == Line.Anchor.START) edges.first() else edges.last()
        val (point, anotherPoint) = if (anchor == Line.Anchor.START) {
            edge.startPoint to edge.endPoint
        } else {
            edge.endPoint to edge.startPoint
        }
        val horizontalOffset = when {
            !edge.isHorizontal -> 0.5
            point.left <= anotherPoint.left -> 0.0
            else -> 1.0
        }
        val verticalOffset = when {
            edge.isHorizontal -> 0.5
            point.top <= anotherPoint.top -> 0.0
            else -> 1.0
        }
        return LineInteractionPoint.Anchor(
            targetedShapeId,
            anchor,
            left = point.left + horizontalOffset,
            top = point.top + verticalOffset
        )
    }
}
