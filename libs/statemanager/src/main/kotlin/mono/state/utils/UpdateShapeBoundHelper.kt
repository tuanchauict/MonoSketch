/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.utils

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.command.ChangeBound
import mono.shape.connector.ShapeConnectorUseCase.getPointInNewBound
import mono.shape.shape.AbstractShape
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment

/**
 * A utility object for updating connectors attached to a shape.
 */
internal object UpdateShapeBoundHelper {
    fun moveShapes(
        environment: CommandEnvironment,
        selectedShapes: Collection<AbstractShape>,
        isUpdateConfirmed: Boolean,
        newPositionCalculator: (AbstractShape) -> Point?
    ) {
        val affectedLines = mutableSetOf<String>()
        val (lines, notLineShapes) = selectedShapes.partition { it is Line }

        for (shape in notLineShapes) {
            val newPosition = newPositionCalculator(shape) ?: continue
            val newBound = shape.bound.copy(position = newPosition)

            environment.shapeManager.execute(ChangeBound(shape, newBound))
            affectedLines += updateConnectors(environment, shape, newBound, isUpdateConfirmed)
        }

        for (line in lines) {
            if (line.id in affectedLines) {
                // If a line is updated by connectors, ignore it from updating as individual shape
                continue
            }
            val newPosition = newPositionCalculator(line) ?: continue
            val newBound = line.bound.copy(position = newPosition)
            environment.shapeManager.execute(ChangeBound(line, newBound))
        }
    }

    /**
     * Updates connects of [target].
     * Returns a list of affected lines
     */
    fun updateConnectors(
        environment: CommandEnvironment,
        target: AbstractShape,
        newBound: Rect,
        isUpdateConfirmed: Boolean
    ): List<String> {
        val connectors = environment.shapeManager.shapeConnector.getConnectors(target)
        for (connector in connectors) {
            val line = environment.shapeManager.getShape(connector.lineId) as? Line ?: continue
            val anchorPointUpdate = Line.AnchorPointUpdate(
                connector.anchor,
                connector.getPointInNewBound(line.getDirection(connector.anchor), newBound)
            )

            line.moveAnchorPoint(
                anchorPointUpdate,
                isReduceRequired = isUpdateConfirmed,
                justMoveAnchor = true
            )
        }
        return connectors.map { it.lineId }
    }
}
