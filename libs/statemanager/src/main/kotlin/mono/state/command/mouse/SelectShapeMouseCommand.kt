package mono.state.command.mouse

import mono.common.exhaustive
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Rect
import mono.state.command.CommandEnvironment

/**
 * A [MouseCommand] to select shapes.
 */
internal object SelectShapeMouseCommand : MouseCommand {
    override val mouseCursor: String = "default"

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean =
        when (mousePointer) {
            is MousePointer.Down -> false
            is MousePointer.Drag -> {
                environment.setSelectionBound(
                    Rect.byLTRB(
                        mousePointer.mouseDownPoint.left,
                        mousePointer.mouseDownPoint.top,
                        mousePointer.point.left,
                        mousePointer.point.top
                    )
                )
                false
            }
            is MousePointer.Up -> {
                environment.setSelectionBound(null)

                val area = Rect.byLTRB(
                    mousePointer.mouseDownPoint.left,
                    mousePointer.mouseDownPoint.top,
                    mousePointer.point.left,
                    mousePointer.point.top
                )

                val shapes = if (area.width * area.height > 1) {
                    environment.shapeSearcher.getAllShapesInZone(area)
                } else {
                    emptyList()
                }

                if (!mousePointer.isWithShiftKey) {
                    environment.clearSelectedShapes()
                }
                for (shape in shapes) {
                    environment.addSelectedShape(shape)
                }
                false
            }
            is MousePointer.Click -> {
                val shapes = environment.shapeSearcher.getShapes(mousePointer.point)
                if (shapes.isNotEmpty()) {
                    val shape = shapes.last()
                    if (mousePointer.isWithShiftKey) {
                        environment.toggleShapeSelection(shape)
                    } else {
                        environment.clearSelectedShapes()
                        environment.addSelectedShape(shape)
                    }
                }
                true
            }
            is MousePointer.DoubleClick -> true
            is MousePointer.Move,
            MousePointer.Idle -> true
        }.exhaustive
}
