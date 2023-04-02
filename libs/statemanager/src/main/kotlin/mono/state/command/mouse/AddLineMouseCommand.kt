/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import mono.common.MouseCursor
import mono.common.exhaustive
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.MoveLineAnchor
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment
import kotlin.math.abs

internal class AddLineMouseCommand : MouseCommand {
    override val mouseCursor: MouseCursor = MouseCursor.CROSSHAIR

    private var workingShape: Line? = null

    override fun execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer
    ): MouseCommand.CommandResultType =
        when (mousePointer) {
            is MousePointer.Down -> {
                val edgeDirection = environment.getEdgeDirection(mousePointer.point)
                val direction =
                    edgeDirection?.normalizedDirection ?: DirectedPoint.Direction.HORIZONTAL
                val shape = Line(
                    DirectedPoint(direction, mousePointer.point),
                    DirectedPoint(DirectedPoint.Direction.VERTICAL, mousePointer.point),
                    parentId = environment.workingParentGroup.id
                )
                workingShape = shape
                environment.addShape(shape)
                environment.clearSelectedShapes()
                MouseCommand.CommandResultType.WORKING
            }
            is MousePointer.Drag -> {
                environment.changeEndAnchor(
                    environment,
                    mousePointer.point,
                    mousePointer.isWithShiftKey,
                    isReducedRequired = false
                )
                MouseCommand.CommandResultType.WORKING
            }
            is MousePointer.Up -> {
                environment.changeEndAnchor(
                    environment,
                    mousePointer.point,
                    mousePointer.isWithShiftKey,
                    isReducedRequired = true
                )
                environment.addSelectedShape(workingShape)
                MouseCommand.CommandResultType.DONE
            }

            is MousePointer.Move,
            is MousePointer.Click,
            is MousePointer.DoubleClick,
            MousePointer.Idle -> MouseCommand.CommandResultType.UNKNOWN
        }.exhaustive

    private fun CommandEnvironment.changeEndAnchor(
        environment: CommandEnvironment,
        point: Point,
        isStraightLineMode: Boolean,
        isReducedRequired: Boolean
    ) {
        val line = workingShape ?: return
        val endPoint = adjustEndPoint(line.startPoint.point, point, isStraightLineMode)
        val edgeDirection = environment.getEdgeDirection(point)
        val direction = edgeDirection?.normalizedDirection ?: line.getDirection(Line.Anchor.END)
        val anchorPointUpdate = Line.AnchorPointUpdate(
            Line.Anchor.END,
            DirectedPoint(direction, endPoint)
        )
        shapeManager.execute(MoveLineAnchor(line, anchorPointUpdate, isReducedRequired))
    }

    private fun adjustEndPoint(
        startPoint: Point,
        candidateEndPoint: Point,
        isStraightLineMode: Boolean
    ): Point {
        if (!isStraightLineMode) {
            return candidateEndPoint
        }
        val width = abs(startPoint.left - candidateEndPoint.left)
        val height = abs(startPoint.top - candidateEndPoint.top)
        return if (width > height) {
            Point(candidateEndPoint.left, startPoint.top)
        } else {
            Point(startPoint.left, candidateEndPoint.top)
        }
    }
}
