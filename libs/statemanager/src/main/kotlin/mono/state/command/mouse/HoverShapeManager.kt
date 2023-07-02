/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import mono.graphics.geo.DirectedPoint
import mono.shape.connector.ShapeConnectorUseCase
import mono.shape.shape.AbstractShape
import mono.state.command.CommandEnvironment

/**
 * A short live-time class to manage hover shape.
 * This class is used to avoid searching for hover shape multiple times.
 */
internal class HoverShapeManager private constructor(
    private val searcher: (CommandEnvironment, DirectedPoint) -> AbstractShape?
) {
    private val pointToTargetMap = mutableMapOf<DirectedPoint, AbstractShape?>()

    fun getHoverShape(
        environment: CommandEnvironment,
        point: DirectedPoint
    ): AbstractShape? = pointToTargetMap.getOrSearch(environment, point)

    private fun MutableMap<DirectedPoint, AbstractShape?>.getOrSearch(
        environment: CommandEnvironment,
        point: DirectedPoint
    ): AbstractShape? = getOrPut(point) {
        if (point !in this) {
            searcher.invoke(environment, point)
        } else {
            // This point is already in the map, so we don't need to search again.
            null
        }
    }

    companion object {
        fun forLineConnectHover(): HoverShapeManager = HoverShapeManager { environment, point ->
            ShapeConnectorUseCase.getConnectableShape(
                point,
                environment.getShapes(point.point)
            )
        }
    }
}
