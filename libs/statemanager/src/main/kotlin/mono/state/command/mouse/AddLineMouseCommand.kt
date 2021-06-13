package mono.state.command.mouse

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.add
import mono.shape.command.MoveLineAnchor
import mono.shape.remove
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment

internal class AddLineMouseCommand : MouseCommand {
    private var workingShape: Line? = null

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean =
        when (mousePointer) {
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
            is MousePointer.Drag -> {
                environment.changeEndAnchor(mousePointer.point, false)
                false
            }
            is MousePointer.Up -> {
                environment.changeEndAnchor(mousePointer.point, true)

                if (isValidLine()) {
                    environment.selectedShapeManager.setSelectedShapes(workingShape)
                } else {
                    environment.shapeManager.remove(workingShape)
                    environment.selectedShapeManager.setSelectedShapes()
                }
                true
            }

            is MousePointer.Move,
            is MousePointer.Click,
            MousePointer.Idle -> true
        }

    private fun isValidLine(): Boolean {
        val line = workingShape ?: return false
        return line.bound.width * line.bound.height > 1
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
