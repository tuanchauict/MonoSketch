package mono.state.command

import mono.graphics.geo.EdgeRelatedPosition
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.command.ChangeBound
import mono.shape.shape.AbstractShape

/**
 * A [MouseCommand] for scaling shapes.
 * [shapes] must have at least 1 item.
 */
internal class ScaleShapeMouseCommand(
    private val shapes: Collection<AbstractShape>,
    private val interactionPosition: EdgeRelatedPosition
) : MouseCommand {
    private val initialShapeBounds = shapes.map { it.id to it.bound }.toMap()
    private val initialBound = Rect.boundOf(shapes.asSequence().map { it.bound })
    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        when (mousePointer) {
            is MousePointer.Move -> scale(environment, mousePointer.point)
            is MousePointer.Up -> scale(environment, mousePointer.point)
            is MousePointer.Down,
            is MousePointer.Click,
            MousePointer.Idle -> Unit
        }

        return mousePointer == MousePointer.Idle
    }

    private fun scale(environment: CommandEnvironment, point: Point) {
        val newBound = when (interactionPosition) {
            EdgeRelatedPosition.LEFT_TOP ->
                Rect.byLTRB(point.left, point.top, initialBound.right, initialBound.bottom)
            EdgeRelatedPosition.MIDDLE_TOP ->
                Rect.byLTRB(initialBound.left, point.top, initialBound.right, initialBound.bottom)
            EdgeRelatedPosition.RIGHT_TOP ->
                Rect.byLTRB(initialBound.left, point.top, point.left, initialBound.bottom)
            EdgeRelatedPosition.LEFT_MIDDLE ->
                Rect.byLTRB(point.left, initialBound.top, initialBound.right, initialBound.bottom)
            EdgeRelatedPosition.RIGHT_MIDDLE ->
                Rect.byLTRB(initialBound.left, initialBound.top, point.left, initialBound.bottom)
            EdgeRelatedPosition.LEFT_BOTTOM ->
                Rect.byLTRB(point.left, initialBound.top, initialBound.right, point.top)
            EdgeRelatedPosition.MIDDLE_BOTTOM ->
                Rect.byLTRB(initialBound.left, initialBound.top, initialBound.right, point.top)
            EdgeRelatedPosition.RIGHT_BOTTOM ->
                Rect.byLTRB(initialBound.left, initialBound.top, point.left, point.top)
        }
        val newShapeBounds = shapes.mapNotNull { createNewShapeBound(newBound, it) }.toMap()

        if (newShapeBounds.size < shapes.size) {
            return
        }

        for (shape in shapes) {
            val toBeRect = newShapeBounds[shape.id] ?: continue
            environment.shapeManager.execute(ChangeBound(shape, toBeRect))
        }
        environment.selectedShapeManager.updateInteractionBound()
    }

    // TODO: This calculation doesn't work well with multiple shape. New size is not scaled
    //  naturally.
    private fun createNewShapeBound(newBound: Rect, shape: AbstractShape): Pair<Int, Rect>? {
        val initShapeBound = initialShapeBounds[shape.id] ?: return null

        val left = initShapeBound.left * newBound.left / initialBound.left
        val top = initShapeBound.top * newBound.top / initialBound.top
        val right = initShapeBound.right * newBound.right / initialBound.right
        val bottom = initShapeBound.bottom * newBound.bottom / initialBound.bottom
        console.log(left, right, top, bottom)
        if (left >= right || top >= bottom) {
            return null
        }
        val newShapeBound = Rect.byLTRB(left, top, right, bottom)
        return if (shape.isNewBoundAcceptable(newShapeBound)) {
            shape.id to Rect.byLTRB(left, top, right, bottom)
        } else {
            null
        }
    }
}
