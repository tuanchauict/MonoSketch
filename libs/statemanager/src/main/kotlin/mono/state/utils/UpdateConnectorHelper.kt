/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.utils

import mono.graphics.geo.Rect
import mono.shape.connector.ShapeConnectorUseCase.getPointInNewBound
import mono.shape.shape.AbstractShape
import mono.shape.shape.Line
import mono.state.command.CommandEnvironment

/**
 * A utility object for updating connectors attached to a shape.
 */
internal object UpdateConnectorHelper {
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
