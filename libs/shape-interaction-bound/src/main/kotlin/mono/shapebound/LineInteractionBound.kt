package mono.shapebound

import mono.shape.shape.Line

/**
 * A class which defines interaction bound for Line shapes.
 */
class LineInteractionBound(
    private val targetedShapeId: String,
    edges: List<Line.Edge>
) : InteractionBound() {
    private val reducedEdges: List<Line.Edge>

    override val interactionPoints: List<InteractionPoint>

    init {
        val noIdenticalPointsEdges = edges.filterNot { it.startPoint == it.endPoint }
        reducedEdges = noIdenticalPointsEdges.ifEmpty { listOf(edges.first()) }

        val anchorPoints = listOf(
            createInteractionAnchor(Line.Anchor.START),
            createInteractionAnchor(Line.Anchor.END)
        )
        val middleEdgePoints =
            reducedEdges.mapNotNull {
                if (it.startPoint == it.endPoint) {
                    return@mapNotNull null
                }
                LineInteractionPoint.Edge(
                    targetedShapeId,
                    it.id,
                    left = it.middleLeft + 0.5,
                    top = it.middleTop + 0.5,
                    it.isHorizontal
                )
            }

        interactionPoints = anchorPoints + middleEdgePoints
    }

    private fun createInteractionAnchor(
        anchor: Line.Anchor,
    ): LineInteractionPoint.Anchor {
        val edge = if (anchor == Line.Anchor.START) reducedEdges.first() else reducedEdges.last()
        val (point, anotherPoint) =
            if (anchor == Line.Anchor.START) {
                edge.startPoint to edge.endPoint
            } else {
                edge.endPoint to edge.startPoint
            }
        val horizontalOffset = when {
            !edge.isHorizontal -> 0.5
            point == anotherPoint -> if (anchor == Line.Anchor.START) 0.0 else 1.0
            point.left < anotherPoint.left -> 0.0
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
