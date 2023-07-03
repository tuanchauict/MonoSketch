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

        // lineId -> headCount of selected shapes.
        val lineIdToHeadCountMap = allConnectors
            .fold(mutableMapOf<String, Int>()) { sum, connector ->
                sum[connector.lineId] = 1 + sum.getOrElse(connector.lineId) { 0 }
                sum
            }
        val lineIdToTotalConnectorCount =
            lines.associate { it.id to environment.countNumberOfConnections(it.id) }

        val affectedLineIds = mutableSetOf<String>()
        // Move non line shapes.
        for (shape in notLineShapes) {
            val newPosition = newPositionCalculator(shape) ?: continue
            val newBound = shape.bound.copy(position = newPosition)

            environment.shapeManager.execute(ChangeBound(shape, newBound))

            // Update line which has 1 head connected and the other head is not connected.
            for (connector in environment.shapeManager.shapeConnector.getConnectors(shape)) {
                // Only update the line anchor if:
                // - the of the connector has only 1 head connected in selected shape
                // - the line is not selected or is selected but has 2 connectors
                // TODO: This logic is too complicated
                val totalConnectorCount = lineIdToTotalConnectorCount[connector.lineId] ?: 2
                val shouldUpdateLineAnchor = lineIdToHeadCountMap[connector.lineId] == 1 &&
                    totalConnectorCount == 2
                if (shouldUpdateLineAnchor) {
                    affectedLineIds += connector.lineId
                    environment.updateConnector(connector, newBound, isUpdateConfirmed)
                }
            }
        }

        val connectorFullLineIds = lineIdToHeadCountMap
            .mapNotNull { if (it.value == 2) it.key else null }
            .toSet()
        val unaffectedLineIds: List<String> =
            lines.mapNotNull { if (it.id !in affectedLineIds) it.id else null }

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

    /**
     * Returns number of connections of a line in the current environment.
     */
    private fun CommandEnvironment.countNumberOfConnections(lineId: String): Int {
        val line = shapeManager.getShape(lineId) as? Line ?: return 0
        val hasStartConnector = shapeManager.shapeConnector.hasConnector(line, Line.Anchor.START)
        val hasEndConnector = shapeManager.shapeConnector.hasConnector(line, Line.Anchor.END)
        return hasStartConnector.toInt() + hasEndConnector.toInt()
    }

    private fun Boolean.toInt(): Int = if (this) 1 else 0
}
