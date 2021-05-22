package mono.state.command.mouse

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.add
import mono.shape.command.MoveLineAnchor
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment

internal class AddLineMouseCommand : MouseCommand {
    private var workingShape: Line? = null

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        return when (mousePointer) {
            is MousePointer.Down -> {
                // TODO: Detect direction based on the environment
                val shape = Line(
                    DirectedPoint(DirectedPoint.Direction.HORIZONTAL, mousePointer.point),
                    DirectedPoint(DirectedPoint.Direction.VERTICAL, mousePointer.point),
                    environment.workingParentGroup.id
                )
                workingShape = shape
                environment.shapeManager.add(shape)
                environment.selectedShapeManager.setSelectedShapes()
                false
            }
            is MousePointer.Move -> {
                environment.changeEndAnchor(mousePointer.point, false)
                false
            }
            is MousePointer.Up -> {
                environment.changeEndAnchor(mousePointer.point, true)
                environment.selectedShapeManager.setSelectedShapes(workingShape)
                true
            }
            is MousePointer.Click,
            MousePointer.Idle -> true
        }
    }

    private fun CommandEnvironment.changeEndAnchor(point: Point, isReducedRequired: Boolean) {
        val currentShape = workingShape ?: return
        // TODO: Detect direction based on the environment.
        val anchorPointUpdate = Line.AnchorPointUpdate(
            Line.Anchor.END,
            DirectedPoint(DirectedPoint.Direction.VERTICAL, point)
        )
        shapeManager.execute(MoveLineAnchor(currentShape, anchorPointUpdate, isReducedRequired))
    }
}
