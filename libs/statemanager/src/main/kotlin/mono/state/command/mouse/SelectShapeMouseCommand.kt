package mono.state.command.mouse

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Rect
import mono.state.command.CommandEnvironment

/**
 * A [MouseCommand] to select shapes.
 */
internal class SelectShapeMouseCommand : MouseCommand {
    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean =
        when (mousePointer) {
            is MousePointer.Down -> false
            is MousePointer.Drag -> {
                environment.selectedShapeManager.setSelectionBound(
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
                environment.selectedShapeManager.setSelectionBound(null)

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
                val selectedShapes = if (mousePointer.isWithShiftKey) {
                    environment.selectedShapeManager.selectedShapes + shapes
                } else {
                    shapes
                }
                environment.selectedShapeManager.setSelectedShapes(*selectedShapes.toTypedArray())
                false
            }
            is MousePointer.Click -> {
                val shapes = environment.shapeSearcher.getShapes(mousePointer.point)
                if (shapes.isNotEmpty()) {
                    val shape = shapes.last()
                    if (mousePointer.isWithShiftKey) {
                        environment.selectedShapeManager.toggleSelection(shape)
                    } else {
                        environment.selectedShapeManager.setSelectedShapes(shape)
                    }
                }
                true
            }
            MousePointer.Idle -> true
        }
}
