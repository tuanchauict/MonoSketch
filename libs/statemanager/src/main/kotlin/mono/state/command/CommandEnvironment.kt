package mono.state.command

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.livedata.LiveData
import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.shapebound.InteractionPoint
import mono.shapesearcher.ShapeSearcher

/**
 * An interface defines apis for command to interact with the environment.
 */
internal interface CommandEnvironment {
    val shapeManager: ShapeManager
    val shapeSearcher: ShapeSearcher

    val editingInProgressLiveData: LiveData<Boolean>

    val workingParentGroup: Group

    fun replaceRoot(newRoot: Group)

    fun setEditingState(isEditing: Boolean)

    fun addShape(shape: AbstractShape?)

    fun removeShape(shape: AbstractShape?)

    fun getWindowBound(): Rect

    fun getInteractionPoint(pointPx: Point): InteractionPoint?

    fun updateInteractionBounds()

    fun isPointInInteractionBounds(point: Point): Boolean

    fun setSelectionBound(bound: Rect?)

    val selectedShapesLiveData: LiveData<Set<AbstractShape>>

    fun getSelectedShapes(): Set<AbstractShape>

    fun addSelectedShape(shape: AbstractShape?)

    fun toggleShapeSelection(shape: AbstractShape)

    fun selectAllShapes()

    fun clearSelectedShapes()

    fun getEdgeDirection(point: Point): DirectedPoint.Direction?

    fun toXPx(column: Double): Double
    fun toYPx(row: Double): Double
    fun toWidthPx(width: Double): Double
    fun toHeightPx(height: Double): Double
}
