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
                createBound(point.left, point.top, initialBound.right, initialBound.bottom)
            EdgeRelatedPosition.MIDDLE_TOP ->
                createBound(initialBound.left, point.top, initialBound.right, initialBound.bottom)
            EdgeRelatedPosition.RIGHT_TOP ->
                createBound(initialBound.left, point.top, point.left, initialBound.bottom)
            EdgeRelatedPosition.LEFT_MIDDLE ->
                createBound(point.left, initialBound.top, initialBound.right, initialBound.bottom)
            EdgeRelatedPosition.RIGHT_MIDDLE ->
                createBound(initialBound.left, initialBound.top, point.left, initialBound.bottom)
            EdgeRelatedPosition.LEFT_BOTTOM ->
                createBound(point.left, initialBound.top, initialBound.right, point.top)
            EdgeRelatedPosition.MIDDLE_BOTTOM ->
                createBound(initialBound.left, initialBound.top, initialBound.right, point.top)
            EdgeRelatedPosition.RIGHT_BOTTOM ->
                createBound(initialBound.left, initialBound.top, point.left, point.top)
        } ?: return
        val newShapeBounds = shapes.mapNotNull { createNewShapeBound(newBound, it) }.toMap()

        if (newShapeBounds.size < shapes.size) {
            // Having at least 1 invalid new shape. No scale
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

        val newShapeBound = createBound(left, top, right, bottom) ?: return null
        return if (shape.isNewBoundAcceptable(newShapeBound)) shape.id to newShapeBound else null
    }

    private fun createBound(left: Int, top: Int, right: Int, bottom: Int): Rect? =
        if (left < right && top < bottom) Rect.byLTRB(left, top, right, bottom) else null
}
