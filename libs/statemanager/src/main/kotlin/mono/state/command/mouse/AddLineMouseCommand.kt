/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import kotlin.math.abs
import mono.common.MouseCursor
import mono.common.exhaustive
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.MousePointer
import mono.graphics.geo.Point
import mono.shape.command.MoveLineAnchor
import mono.shape.connector.ShapeConnectorUseCase
import mono.shape.selection.SelectedShapeManager
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment

/**
 * A [MouseCommand] for adding a new Line shape.
 */
internal class AddLineMouseCommand : MouseCommand {
    override val mouseCursor: MouseCursor = MouseCursor.CROSSHAIR

    private var workingShape: Line? = null

    private val hoverShapeManager = HoverShapeManager.forLineConnectHover()

    override fun execute(
        environment: CommandEnvironment,
        mousePointer: MousePointer
    ): MouseCommand.CommandResultType =
        when (mousePointer) {
            is MousePointer.Down -> {
                workingShape = environment.createLineAndAdjustStartAnchor(mousePointer)
                environment.clearSelectedShapes()
                MouseCommand.CommandResultType.WORKING
            }

            is MousePointer.Drag -> {
                environment.changeEndAnchor(
                    environment,
                    mousePointer.boardCoordinate,
                    mousePointer.isWithShiftKey,
                    isUpdateConfirmed = false
                )
                MouseCommand.CommandResultType.WORKING
            }

            is MousePointer.Up -> {
                environment.changeEndAnchor(
                    environment,
                    mousePointer.boardCoordinate,
                    mousePointer.isWithShiftKey,
                    isUpdateConfirmed = true
                )
                environment.addSelectedShape(workingShape)
                MouseCommand.CommandResultType.DONE
            }

            is MousePointer.Move,
            is MousePointer.Click,
            is MousePointer.DoubleClick,
            MousePointer.Idle -> MouseCommand.CommandResultType.UNKNOWN
        }.exhaustive

    private fun CommandEnvironment.createLineAndAdjustStartAnchor(
        mousePointer: MousePointer.Down
    ): Line {
        val edgeDirection = getEdgeDirection(mousePointer.boardCoordinate)
        val direction = edgeDirection?.normalizedDirection ?: DirectedPoint.Direction.HORIZONTAL
        val line = Line(
            DirectedPoint(direction, mousePointer.boardCoordinate),
            DirectedPoint(DirectedPoint.Direction.VERTICAL, mousePointer.boardCoordinate),
            parentId = workingParentGroup.id
        )
        addShape(line)

        val connectShape = ShapeConnectorUseCase.getConnectableShape(
            line.startPoint,
            getShapes(mousePointer.boardCoordinate)
        )
        if (connectShape != null) {
            shapeManager.shapeConnector.addConnector(line, Line.Anchor.START, connectShape)
        }
        return line
    }

    private fun CommandEnvironment.changeEndAnchor(
        environment: CommandEnvironment,
        point: Point,
        isStraightLineMode: Boolean,
        isUpdateConfirmed: Boolean
    ) {
        val line = workingShape ?: return
        val endPoint = adjustEndPoint(line.startPoint.point, point, isStraightLineMode)
        val edgeDirection = environment.getEdgeDirection(point)
        val direction = edgeDirection?.normalizedDirection ?: line.getDirection(Line.Anchor.END)
        val anchorPointUpdate = Line.AnchorPointUpdate(
            Line.Anchor.END,
            DirectedPoint(direction, endPoint)
        )

        val connectShape = hoverShapeManager.getHoverShape(environment, anchorPointUpdate.point)
        environment.setFocusingShape(
            connectShape.takeIf { !isUpdateConfirmed },
            SelectedShapeManager.ShapeFocusType.LINE_CONNECTING
        )
        shapeManager.execute(
            MoveLineAnchor(
                line,
                anchorPointUpdate,
                isUpdateConfirmed,
                justMoveAnchor = false,
                connectShape = connectShape
            )
        )
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
