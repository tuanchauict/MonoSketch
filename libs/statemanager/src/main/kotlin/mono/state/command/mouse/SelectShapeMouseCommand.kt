/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import mono.common.MouseCursor
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Rect
import mono.state.command.CommandEnvironment

/**
 * A [MouseCommand] to select shapes.
 */
internal object SelectShapeMouseCommand : MouseCommand {
    override val mouseCursor: MouseCursor = MouseCursor.DEFAULT

    override fun execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer
    ): MouseCommand.CommandResultType =
        when (mousePointer) {
            is MousePointer.Down -> MouseCommand.CommandResultType.WORKING
            is MousePointer.Drag -> {
                environment.setSelectionBound(
                    Rect.byLTRB(
                        mousePointer.mouseDownPoint.left,
                        mousePointer.mouseDownPoint.top,
                        mousePointer.boardCoordinate.left,
                        mousePointer.boardCoordinate.top
                    )
                )
                MouseCommand.CommandResultType.WORKING
            }

            is MousePointer.Up -> {
                environment.setSelectionBound(null)

                val area = Rect.byLTRB(
                    mousePointer.mouseDownPoint.left,
                    mousePointer.mouseDownPoint.top,
                    mousePointer.boardCoordinate.left,
                    mousePointer.boardCoordinate.top
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
                MouseCommand.CommandResultType.WORKING
            }

            is MousePointer.Click -> {
                val shapes =
                    environment.shapeSearcher.getShapes(mousePointer.boardCoordinate).toList()
                if (shapes.isNotEmpty()) {
                    val shape = shapes.last()
                    if (mousePointer.isWithShiftKey) {
                        environment.toggleShapeSelection(shape)
                    } else {
                        environment.clearSelectedShapes()
                        environment.addSelectedShape(shape)
                    }
                }
                MouseCommand.CommandResultType.DONE
            }

            is MousePointer.DoubleClick,
            is MousePointer.Move,
            MousePointer.Idle -> MouseCommand.CommandResultType.DONE
        }
}
