package mono.shape.command

import mono.graphics.geo.Point
import mono.shape.ShapeManager
import mono.shape.shape.Group
import mono.shape.shape.Line

/**
 * A [Command] for changing Line shape's Anchors.
 */
class MoveLineAnchor(
    private val target: Line,
    private val anchorPointUpdate: Line.AnchorPointUpdate,
    private val isReducedRequired: Boolean
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.version
        target.moveAnchorPoint(anchorPointUpdate, isReducedRequired)
        parent.update { currentVersion != target.version }
    }
}

/**
 * A [Command] for updating Line shape's edges.
 */
class MoveLineEdge(
    private val target: Line,
    private val edgeId: Int,
    private val point: Point,
    private val isReducedRequired: Boolean
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.version
        target.moveEdge(edgeId, point, isReducedRequired)
        parent.update { currentVersion != target.version }
    }
}
