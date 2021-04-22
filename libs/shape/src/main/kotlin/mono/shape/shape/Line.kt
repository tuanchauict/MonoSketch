package mono.shape.shape

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.shape.shape.line.LineHelper

/**
 * A line shape which connects two end-dots with a collection of straight lines.
 *
 * A Line shape is defined by two end points which have direction. The inner algorithm will use the
 * defined direction to generate straight lines by creating joint points.
 * Line shapes are able to be modified by moving end points or moving connecting edges. Once the
 * edge is modified, the line won't depend on seeding direction.
 */
class Line(startPoint: DirectedPoint, endPoint: DirectedPoint) {
    private var startPoint: DirectedPoint = startPoint
    private var endPoint: DirectedPoint = endPoint

    var jointPoints: List<Point> = LineHelper.createJointPoints(listOf(startPoint, endPoint))
        private set

    private var edges: List<Edge> = LineHelper.createEdges(jointPoints)

    /**
     * A list of joint points which is determined once an edge is updated.
     */
    private var confirmedJointPoints: List<Point> = emptyList()

    fun moveEdge(edgeId: Int, point: Point, isReduceRequired: Boolean) {
        val edgeIndex = edges.indexOfFirst { it.id == edgeId }
        if (edgeIndex < 0) {
            return
        }

        val edge = edges[edgeIndex]
        val newEdge = edge.translate(point)

        if (edge == newEdge) {
            return
        }

        val newJointPoints = jointPoints.toMutableList()

        when {
            edgeIndex == 0 && edgeIndex == edges.lastIndex -> {
                newJointPoints.add(1, newEdge.startPoint)
                newJointPoints.add(2, newEdge.endPoint)
            }
            edgeIndex == 0 -> {
                newJointPoints.add(1, newEdge.startPoint)
                newJointPoints[2] = newEdge.endPoint
            }
            edgeIndex == edges.lastIndex -> {
                val startPointIndex = newJointPoints.lastIndex - 1
                newJointPoints[startPointIndex] = newEdge.startPoint
                newJointPoints.add(startPointIndex + 1, newEdge.endPoint)
            }
            else -> {
                // Just move affected points
                val startPointIndex = newJointPoints.indexOf(edge.startPoint)
                newJointPoints[startPointIndex] = newEdge.startPoint
                newJointPoints[startPointIndex + 1] = newEdge.endPoint
            }
        }

        updateJointPoints(newJointPoints, isReduceRequired)
        if (isReduceRequired) {
            confirmedJointPoints = jointPoints
        }
    }
    
    private fun updateJointPoints(newJointPoints: List<Point>, isReduceRequired: Boolean) {
        jointPoints = if (isReduceRequired) LineHelper.reduce(newJointPoints) else newJointPoints
        edges = LineHelper.createEdges(jointPoints)
    }

    internal data class Edge(val id: Int = getId(), val startPoint: Point, val endPoint: Point) {

        private val isHorizontal: Boolean = startPoint.top == endPoint.top

        fun translate(point: Point): Edge {
            val (newStartPoint, newEndPoint) = if (isHorizontal) {
                startPoint.copy(top = point.top) to endPoint.copy(top = point.top)
            } else {
                startPoint.copy(left = point.left) to endPoint.copy(left = point.left)
            }
            return copy(startPoint = newStartPoint, endPoint = newEndPoint)
        }

        companion object {
            private var lastUsedId: Int = 0
            fun getId(): Int {
                val newId = lastUsedId + 1
                lastUsedId = newId
                return newId
            }
        }
    }
}
