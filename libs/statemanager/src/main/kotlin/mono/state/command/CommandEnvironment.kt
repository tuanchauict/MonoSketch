/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.livedata.LiveData
import mono.shape.ShapeManager
import mono.shape.connector.ShapeConnector
import mono.shape.selection.SelectedShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shape.shape.RootGroup
import mono.shapebound.InteractionPoint

/**
 * An interface defines apis for command to interact with the environment.
 */
internal interface CommandEnvironment {
    val shapeManager: ShapeManager

    val editingModeLiveData: LiveData<EditingMode>

    /**
     * The current working parent group, which is the group that is focused, shape actions will be
     * applied to this group.
     *
     * This is similar to the concept of "current directory" in file system.
     */
    val workingParentGroup: Group

    fun replaceRoot(newRoot: RootGroup, newShapeConnector: ShapeConnector)

    fun enterEditingMode()

    fun exitEditingMode(isNewStateAccepted: Boolean)

    fun addShape(shape: AbstractShape?)

    fun removeShape(shape: AbstractShape?)

    fun getShapes(point: Point): Sequence<AbstractShape>

    fun getAllShapesInZone(bound: Rect): Sequence<AbstractShape>

    fun getWindowBound(): Rect

    fun getInteractionPoint(pointPx: Point): InteractionPoint?

    fun updateInteractionBounds()

    fun isPointInInteractionBounds(point: Point): Boolean

    fun setSelectionBound(bound: Rect?)

    val selectedShapesLiveData: LiveData<Set<AbstractShape>>

    fun getSelectedShapes(): Set<AbstractShape>

    fun addSelectedShape(shape: AbstractShape?)

    fun toggleShapeSelection(shape: AbstractShape)

    fun setFocusingShape(shape: AbstractShape?, focusType: SelectedShapeManager.ShapeFocusType)

    fun getFocusingShape(): AbstractShape?

    fun selectAllShapes()

    fun clearSelectedShapes()

    fun getEdgeDirection(point: Point): DirectedPoint.Direction?

    fun toXPx(column: Double): Double
    fun toYPx(row: Double): Double
    fun toWidthPx(width: Double): Double
    fun toHeightPx(height: Double): Double

    class EditingMode private constructor(val isEditing: Boolean, val skippedVersion: Int?) {
        companion object {
            private val EDIT = EditingMode(true, null)
            private val IDLE = EditingMode(false, null)

            fun edit(): EditingMode = EDIT
            fun idle(skippedVersion: Int?): EditingMode =
                if (skippedVersion == null) IDLE else EditingMode(false, skippedVersion)
        }
    }
}
