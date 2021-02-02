package mono.state.command

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.add
import mono.shape.command.ChangeBound
import mono.shape.shape.AbstractShape
import mono.shape.shape.Rectangle

/**
 * Add new shape with mouse command.
 */
internal class AddShapeMouseCommand(private val shapeFactory: ShapeFactory) : MouseCommand {
    private var workingShape: AbstractShape? = null

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        when (mousePointer) {
            is MousePointer.Down -> {
                val shape =
                    shapeFactory.createShape(mousePointer.point, environment.workingParentGroup.id)
                workingShape = shape
                environment.setSelectedShapes(setOf(shape))
                environment.shapeManager.add(shape)
            }
            is MousePointer.Move ->
                environment.changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
            is MousePointer.Up -> {
                environment.changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
                workingShape = null
                return true
            }
            is MousePointer.Click,
            MousePointer.Idle -> Unit
        }
        return false
    }

    private fun CommandEnvironment.changeShapeBound(point1: Point, point2: Point) {
        val currentShape = workingShape ?: return
        val rect = Rect.byLTRB(
            left = point1.left,
            top = point1.top,
            right = point2.left,
            bottom = point2.top
        )

        shapeManager.execute(ChangeBound(currentShape, rect))
    }
}

internal sealed class ShapeFactory {
    abstract fun createShape(position: Point, parentId: Int): AbstractShape

    object RectangleFactory : ShapeFactory() {
        override fun createShape(position: Point, parentId: Int): AbstractShape =
            Rectangle(position, position, parentId)
    }
}
