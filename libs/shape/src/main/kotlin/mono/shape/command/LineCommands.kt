/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.command

import mono.graphics.geo.Point
import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line

/**
 * A [Command] for changing Line shape's Anchors.
 *
 * @param isReducedRequired The flag for running Line's points reduction. If this is true, merging
 * same line points process will be conducted.
 */
class MoveLineAnchor(
    private val target: Line,
    private val anchorPointUpdate: Line.AnchorPointUpdate,
    private val isReducedRequired: Boolean,
    private val connectableCandidateShapes: List<AbstractShape>
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.versionCode
        target.moveAnchorPoint(anchorPointUpdate, isReducedRequired)
        parent.update { currentVersion != target.versionCode }
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
        val currentVersion = target.versionCode
        target.moveEdge(edgeId, point, isReducedRequired)
        parent.update { currentVersion != target.versionCode }
    }
}
