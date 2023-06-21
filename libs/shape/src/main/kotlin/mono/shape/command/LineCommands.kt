/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shape.command

import mono.graphics.geo.Point
import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.Line
import mono.shape.shape.MockShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

/**
 * A [Command] for changing Line shape's Anchors.
 *
 * @param isUpdateConfirmed The flag for running Line's points reduction. If this is true, merging
 * same line points process will be conducted.
 */
class MoveLineAnchor(
    private val target: Line,
    private val anchorPointUpdate: Line.AnchorPointUpdate,
    private val isUpdateConfirmed: Boolean,
    private val connectableCandidateShapes: List<AbstractShape>
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.versionCode
        target.moveAnchorPoint(anchorPointUpdate, isUpdateConfirmed)
        if (currentVersion == target.versionCode) {
            return
        }
        val boxShape = connectableCandidateShapes.lastOrNull {
            when (it) {
                is Rectangle,
                is Text -> true

                is Group,
                is Line,
                is MockShape -> false
            }
        }
        if (boxShape != null) {
            shapeManager.shapeConnector.addConnector(target, anchorPointUpdate.anchor, boxShape)
        } else {
            shapeManager.shapeConnector.removeConnector(target, anchorPointUpdate.anchor)
        }
        parent.update { true }
    }
}

/**
 * A [Command] for updating Line shape's edges.
 *
 * @param isUpdateConfirmed The flag for running Line's points reduction. If this is true, merging
 *  * same line points process will be conducted.
 */
class MoveLineEdge(
    private val target: Line,
    private val edgeId: Int,
    private val point: Point,
    private val isUpdateConfirmed: Boolean
) : Command() {
    override fun getDirectAffectedParent(shapeManager: ShapeManager): Group? =
        shapeManager.getGroup(target.parentId)

    override fun execute(shapeManager: ShapeManager, parent: Group) {
        val currentVersion = target.versionCode
        target.moveEdge(edgeId, point, isUpdateConfirmed)
        parent.update { currentVersion != target.versionCode }
    }
}
