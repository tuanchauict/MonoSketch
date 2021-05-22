package mono.shape.shape

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.shape.line.LineHelper

/**
 * A line shape which connects two end-dots with a collection of straight lines.
 *
 * A Line shape is defined by two end points which have direction. The inner algorithm will use the
 * defined direction to generate straight lines by creating joint points.
 * Line shapes are able to be modified by moving end points or moving connecting edges. Once the
 * edge is modified, the line won't depend on seeding direction.
 *
 * First initial line's edges will be decided by the direction inside the two end points
 * Examples
 *
 * 1. Same line
 * Start: (0, 0, Horizontal)
 * End  : (5, 0, Vertical)
 * Result:
 * ```
 * x----x
 * ```
 *
 * 2. Different line
 * 2.1.
 * Start: (0, 0, Horizontal)
 * End  : (5, 3, Vertical)
 * Result:
 * ```
 * x----+
 *      |
 *      |
 *      x
 * ```
 * 2.2.
 * Start: (0, 0, Horizontal)
 * End  : (5, 3, Horizontal)
 * Result:
 * ```
 * x-+
 *   |
 *   |
 *   +--x
 * ```
 * 2.3.
 * Start: (0, 0, Vertical)
 * End  : (5, 3, Horizontal)
 * Result:
 * ```
 * x
 * |
 * |
 * +----x
 * ```
 * 2.4.
 * Start: (0, 0, Vertical)
 * End  : (5, 4, Vertical)
 * Result:
 * ```
 * x
 * |
 * +----+
 *      |
 *      x
 * ```
 */
class Line(
    private var startPoint: DirectedPoint,
    private var endPoint: DirectedPoint,
    parentId: Int?
) : AbstractShape(parentId = parentId) {

    private var jointPoints: List<Point> =
        LineHelper.createJointPoints(listOf(startPoint, endPoint))

    val reducedJoinPoints: List<Point>
        get() = LineHelper.reduce(jointPoints)

    private var edges: List<Edge> = LineHelper.createEdges(jointPoints)

    val reducedEdges: List<Edge>
        get() = edges.filterNot { it.startPoint == it.endPoint }

    var anchorCharStart: AnchorChar = AnchorChar('─', '─', '|', '|')
        private set
    var anchorCharEnd: AnchorChar = AnchorChar('─', '─', '|', '|')
        private set

    /**
     * A list of joint points which is determined once an edge is updated.
     */
    private var confirmedJointPoints: List<Point> = emptyList()

    override val bound: Rect
        get() {
            val left = jointPoints.minOf { it.left }
            val right = jointPoints.maxOf { it.left }
            val top = jointPoints.minOf { it.top }
            val bottom = jointPoints.maxOf { it.top }
            return Rect.byLTRB(left, top, right, bottom)
        }

    /**
     * Move start point or end point to new location decided by [AnchorPointUpdate.anchor] of
     * [anchorPointUpdate].
     * If the line's edges have never been moved, new edges will be decided by new anchor point and
     * unaffected point with their direction like at the initial step.
     * Otherwise, the anchor point is just moved to new point.
     * New point in the middle will be introduced if the new position is not on the same line with
     * the previous containing edge.
     *
     * Examples for moved:
     * Case 1. Line's edges are never moved: see examples in the class doc
     * Case 2. Line's edges have been moved
     * Input
     * ```
     * +-------o
     * ```
     * Result:
     * Same line
     * ```
     * +---------------x
     * ```
     *
     * 2.2. Different lines
     * ```
     * +----------+
     *            |
     *            x
     * ```
     *
     */
    fun moveAnchorPoint(anchorPointUpdate: AnchorPointUpdate, isReduceRequired: Boolean) = update {
        when (anchorPointUpdate.anchor) {
            Anchor.START -> startPoint = anchorPointUpdate.point
            Anchor.END -> endPoint = anchorPointUpdate.point
        }

        val isEdgeUpdated = confirmedJointPoints.isNotEmpty()
        val newJointPoints = if (!isEdgeUpdated) {
            val seedPoints = listOf(startPoint, endPoint)
            LineHelper.createJointPoints(seedPoints)
        } else {
            val newJointPoint = confirmedJointPoints.createNewJointPoint(anchorPointUpdate)
            confirmedJointPoints.toMutableList().apply {
                val (anchorIndex, newJointPointIndex) = when (anchorPointUpdate.anchor) {
                    Anchor.START -> 0 to 1
                    Anchor.END -> lastIndex to lastIndex
                }
                this[anchorIndex] = anchorPointUpdate.point.point
                if (newJointPoint != null) {
                    add(newJointPointIndex, newJointPoint)
                }
            }
        }
        val isUpdated = newJointPoints != jointPoints
        jointPoints = if (isReduceRequired) LineHelper.reduce(newJointPoints) else newJointPoints
        edges = LineHelper.createEdges(jointPoints)

        isUpdated
    }

    private fun List<Point>.createNewJointPoint(anchorPointUpdate: AnchorPointUpdate): Point? {
        val (anchorPointIndex, previousPointIndex) = when (anchorPointUpdate.anchor) {
            Anchor.START -> 0 to 1
            Anchor.END -> lastIndex to lastIndex - 1
        }
        val anchorEndPoint = get(anchorPointIndex)
        val previousJointPoint = get(previousPointIndex)
        val updatePoint = anchorPointUpdate.point.point

        val isOnSameLine = LineHelper.isOnStraightLine(
            anchorEndPoint,
            previousJointPoint,
            updatePoint,
            isInOrderedRequired = false
        )
        if (isOnSameLine) {
            // No new joint point when they are on the same line regardless order. Just move anchor
            // point
            return null
        }

        return if (isHorizontal(anchorEndPoint, previousJointPoint)) {
            Point(updatePoint.left, anchorEndPoint.top)
        } else {
            Point(anchorEndPoint.left, updatePoint.top)
        }
    }

    /**
     * Move the targeted edge by [edgeId] to make its line contains [point].
     * During moving edge, two anchor points won't be moved.
     * If [edgeId] is the first or last edge, new edge will be introduced.
     *
     * Examples:
     * 1. Move single edge
     * ```
     * x---o---x
     * ```
     * Result:
     * ```
     * x       x
     * |       |
     * +---o---+
     * ```
     *
     * 2. Move 1st/last edge
     * ```
     * x----o----+       x---------+
     *           |                 |
     *           |                 o
     *           |                 |
     *           x                 x
     * ```
     * Result:
     * ```
     * x                x-----+
     * |                      |
     * +---o---+              o
     *         |              |
     *         x              +----x
     * ```
     * 3. Move edge in the middle
     * ```
     * x-------+
     *         |
     *         o
     *         |
     * x-------+
     * ```
     * Result
     * ```
     * x--------------+
     *                |
     *                o
     *                |
     * x--------------+
     * ```
     * Once the edge is moved successfully, the line becomes independent from direction in the
     * stored in two anchor points.
     *
     * When reduce move is on ([isReduceRequired]), all adjacent edges which are on the same line
     * by the same direction will be merged.
     * For example
     * ```
     * 1-----2-----3   ->   1-----------3
     *
     * 1-----3-----2   ->   1-----3-----2
     * ```
     */
    fun moveEdge(edgeId: Int, point: Point, isReduceRequired: Boolean) = update {
        val edgeIndex = edges.indexOfFirst { it.id == edgeId }
        if (edgeIndex < 0) {
            return@update false
        }

        val edge = edges[edgeIndex]
        val newEdge = edge.translate(point)
        if (!isReduceRequired && edge == newEdge) {
            // Skip when reducing is not required and old edge is identical to new edge.
            return@update false
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
                val startPointIndex = newJointPoints.indexOfFirst { it === edge.startPoint }
                newJointPoints[startPointIndex] = newEdge.startPoint
                newJointPoints[startPointIndex + 1] = newEdge.endPoint
            }
        }

        val isUpdated = jointPoints != newJointPoints
        jointPoints = if (isReduceRequired) LineHelper.reduce(newJointPoints) else newJointPoints
        confirmedJointPoints = jointPoints

        val newEdges = LineHelper.createEdges(jointPoints)
        if (!isReduceRequired) {
            val newEdgeIndex = if (edgeIndex == 0) 1 else edgeIndex
            // Reserve current interacted edge's id.
            newEdges[newEdgeIndex] = newEdges[newEdgeIndex].copy(id = edge.id)
        }
        edges = newEdges

        isUpdated
    }

    fun getDirection(anchor: Anchor): DirectedPoint.Direction = when (anchor) {
        Anchor.START -> startPoint.direction
        Anchor.END -> endPoint.direction
    }

    override fun contains(point: Point): Boolean = edges.any { it.contains(point) }

    data class Edge internal constructor(
        val id: Int = getId(),
        val startPoint: Point,
        val endPoint: Point
    ) {
        val middleLeft: Double = (startPoint.left + endPoint.left).toDouble() / 2.0
        val middleTop: Double = (startPoint.top + endPoint.top).toDouble() / 2.0

        val isHorizontal: Boolean = isHorizontal(startPoint, endPoint)

        internal fun translate(point: Point): Edge {
            val (newStartPoint, newEndPoint) = if (isHorizontal) {
                startPoint.copy(top = point.top) to endPoint.copy(top = point.top)
            } else {
                startPoint.copy(left = point.left) to endPoint.copy(left = point.left)
            }
            return copy(startPoint = newStartPoint, endPoint = newEndPoint)
        }

        internal fun contains(point: Point): Boolean =
            LineHelper.isOnStraightLine(startPoint, point, endPoint, isInOrderedRequired = true)

        companion object {
            private var lastUsedId: Int = 0
            internal fun getId(): Int {
                val newId = lastUsedId + 1
                lastUsedId = newId
                return newId
            }
        }
    }

    data class AnchorPointUpdate(val anchor: Anchor, val point: DirectedPoint)

    enum class Anchor {
        START, END
    }

    data class AnchorChar(val left: Char, val right: Char, val top: Char, val bottom: Char) {
        constructor(all: Char) : this(all, all, all, all)
    }

    companion object {
        private fun isHorizontal(p1: Point, p2: Point): Boolean = p1.top == p2.top
    }
}
