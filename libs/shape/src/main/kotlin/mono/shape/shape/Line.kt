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
}
