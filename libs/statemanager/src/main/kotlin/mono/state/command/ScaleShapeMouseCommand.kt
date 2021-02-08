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
        val newShapeBounds = shapes.mapNotNull {
            val initShapeBound = initialShapeBounds[it.id] ?: return@mapNotNull null
            val rect = Rect.byLTRB(
                left = initShapeBound.left * newBound.left / initialBound.left,
                top = initShapeBound.top * newBound.top / initialBound.top,
                right = initShapeBound.right * newBound.right / initialBound.right,
                bottom = initShapeBound.bottom * newBound.bottom / initialBound.bottom,
            )

            if (rect.width > 1 && rect.height > 1) it.id to rect else null
        }.toMap()

        if (newShapeBounds.size < shapes.size) {
            return
        }

        for (shape in shapes) {
            val toBeRect = newShapeBounds[shape.id] ?: continue
            environment.shapeManager.execute(ChangeBound(shape, toBeRect))
        }
        environment.selectedShapeManager.updateInteractionBound()
    }
}
