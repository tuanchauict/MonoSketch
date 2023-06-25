/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.utils

import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.command.ChangeBound
import mono.shape.connector.LineConnector
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
        val (lines, notLineShapes) = selectedShapes.partition { it is Line }
        val allConnectors =
            notLineShapes.flatMap { environment.shapeManager.shapeConnector.getConnectors(it) }

        val lineIdToHeadCountMap = allConnectors
            .fold(mutableMapOf<String, Int>()) { sum, connector ->
                sum[connector.lineId] = 1 + sum.getOrElse(connector.lineId) { 0 }
                sum
            }

        for (shape in notLineShapes) {
            val newPosition = newPositionCalculator(shape) ?: continue
            val newBound = shape.bound.copy(position = newPosition)

            environment.shapeManager.execute(ChangeBound(shape, newBound))

            for (connector in environment.shapeManager.shapeConnector.getConnectors(shape)) {
                if (lineIdToHeadCountMap[connector.lineId] == 1) {
                    environment.updateConnector(connector, newBound, isUpdateConfirmed)
                }
            }
        }

        val connectorFullLineIds = lineIdToHeadCountMap
            .mapNotNull { if (it.value == 2) it.key else null }
            .toSet()
        val unaffectedLineIds: List<String> =
            lines.mapNotNull { if (lineIdToHeadCountMap[it.id] != 1) it.id else null }

        val lineIds = connectorFullLineIds + unaffectedLineIds

        for (lineId in lineIds) {
            val line = environment.shapeManager.getShape(lineId) ?: continue
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
            environment.updateConnector(connector, newBound, isUpdateConfirmed)
        }
        return connectors.map { it.lineId }
    }

    private fun CommandEnvironment.updateConnector(
        connector: LineConnector,
        newBound: Rect,
        isUpdateConfirmed: Boolean
    ) {
        val line = shapeManager.getShape(connector.lineId) as? Line ?: return
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
}
