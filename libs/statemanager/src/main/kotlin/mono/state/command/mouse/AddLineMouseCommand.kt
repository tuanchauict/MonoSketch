package mono.state.command.mouse

import mono.common.exhaustive
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.add
import mono.shape.command.MoveLineAnchor
import mono.shape.remove
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment

internal class AddLineMouseCommand : MouseCommand {
    override val mouseCursor: String = "crosshair"

    private var workingShape: Line? = null

    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean =
        when (mousePointer) {
            is MousePointer.Down -> {
                val edgeDirection = environment.getEdgeDirection(mousePointer.point)
                val direction =
                    edgeDirection?.normalizedDirection ?: DirectedPoint.Direction.HORIZONTAL
                val shape = Line(
                    DirectedPoint(direction, mousePointer.point),
                    DirectedPoint(DirectedPoint.Direction.VERTICAL, mousePointer.point),
                    environment.workingParentGroup.id
                )
                workingShape = shape
                environment.shapeManager.add(shape)
                environment.clearSelectedShapes()
                false
            }
            is MousePointer.Drag -> {
                environment.changeEndAnchor(environment, mousePointer.point, false)
                false
            }
            is MousePointer.Up -> {
                environment.changeEndAnchor(environment, mousePointer.point, true)

                if (isValidLine()) {
                    environment.addSelectedShape(workingShape)
                } else {
                    environment.shapeManager.remove(workingShape)
                    environment.clearSelectedShapes()
                }
                true
            }

            is MousePointer.Move,
            is MousePointer.Click,
            MousePointer.Idle -> true
        }.exhaustive

    private fun isValidLine(): Boolean {
        val line = workingShape ?: return false
        return line.bound.width * line.bound.height > 1
    }

    private fun CommandEnvironment.changeEndAnchor(
        environment: CommandEnvironment,
        point: Point,
        isReducedRequired: Boolean
    ) {
        val line = workingShape ?: return
        val edgeDirection = environment.getEdgeDirection(point)
        val direction = edgeDirection?.normalizedDirection ?: line.getDirection(Line.Anchor.END)
        val anchorPointUpdate = Line.AnchorPointUpdate(
            Line.Anchor.END,
            DirectedPoint(direction, point)
        )
        shapeManager.execute(MoveLineAnchor(line, anchorPointUpdate, isReducedRequired))
    }
}
