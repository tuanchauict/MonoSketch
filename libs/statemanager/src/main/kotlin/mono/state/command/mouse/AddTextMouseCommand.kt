package mono.state.command.mouse

import mono.common.nullToFalse
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.add
import mono.shape.command.ChangeBound
import mono.shape.remove
import mono.shape.shape.Text
import mono.state.command.CommandEnvironment
import mono.state.command.text.EditTextShapeHelper

/**
 * A [MouseCommand] to add new text shape.
 * This command does two jobs:
 * 1. Identify the initial bound for the text shape
 * 2. Open a modal for entering text content when mouse up.
 *
 * TODO: Implement step 2
 */
internal class AddTextMouseCommand : MouseCommand {
    private var workingShape: Text? = null
    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean =
        when (mousePointer) {
            is MousePointer.Down -> {
                val shape = Text(
                    mousePointer.point,
                    mousePointer.point,
                    environment.workingParentGroup.id
                )
                workingShape = shape
                environment.shapeManager.add(shape)
                environment.selectedShapeManager.setSelectedShapes()
                false
            }
            is MousePointer.Drag -> {
                environment.changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
                false
            }
            is MousePointer.Up -> {
                onMouseUp(environment, mousePointer)
                true
            }
            is MousePointer.Click,
            MousePointer.Idle -> true
        }

    private fun onMouseUp(environment: CommandEnvironment, mousePointer: MousePointer.Up) {
        environment.changeShapeBound(mousePointer.mouseDownPoint, mousePointer.point)
        if (!workingShape?.isBoundValid().nullToFalse()) {
            environment.shapeManager.remove(workingShape)
            workingShape = null
            return
        }
        environment.selectedShapeManager.setSelectedShapes(workingShape)
        EditTextShapeHelper.showEditTextDialog(environment.shapeManager, workingShape) {
            if (!workingShape?.isValid().nullToFalse()) {
                environment.shapeManager.remove(workingShape)
                environment.selectedShapeManager.setSelectedShapes()
            }
            workingShape = null
        }
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
