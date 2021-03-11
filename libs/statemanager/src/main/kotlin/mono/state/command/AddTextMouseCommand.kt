package mono.state.command

import mono.common.nullToFalse
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.html.modal.EditTextDialog
import mono.shape.add
import mono.shape.command.ChangeBound
import mono.shape.command.ChangeExtra
import mono.shape.remove
import mono.shape.shape.Text

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
            is MousePointer.Move -> {
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

        val dialog = EditTextDialog("monomodal-mono-edit-text") { environment.changeText(it) }
        dialog.setOnDismiss {
            if (!workingShape?.isValid().nullToFalse()) {
                environment.shapeManager.remove(workingShape)
                environment.selectedShapeManager.setSelectedShapes()
            }
            workingShape = null
        }
        dialog.show()
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

    private fun CommandEnvironment.changeText(text: String) {
        val currentShape = workingShape ?: return
        val extraUpdater = Text.Extra.Updater.Text(text)
        shapeManager.execute(ChangeExtra(currentShape, extraUpdater))
    }
}
