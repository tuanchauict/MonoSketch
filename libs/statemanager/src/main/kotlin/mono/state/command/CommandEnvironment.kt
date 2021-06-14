package mono.state.command

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
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
    val workingParentGroup: Group

    fun getInteractionPoint(pointPx: Point): InteractionPoint?
    
    fun updateInteractionBounds()

    fun isPointInInteractionBounds(point: Point): Boolean
    
    fun setSelectionBound(bound: Rect?)
    
    fun getSelectedShapes(): Set<AbstractShape>
    
    fun addSelectedShape(shape: AbstractShape?)
    
    fun toggleShapeSelection(shape: AbstractShape)
    
    fun clearSelectedShapes()
}
