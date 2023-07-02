/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.command.mouse

import mono.graphics.geo.Point
import mono.shape.connector.ShapeConnectorUseCase
import mono.shape.shape.AbstractShape
import mono.state.command.CommandEnvironment

/**
 * A short live-time class to manage hover shape.
 * This class is used to avoid searching for hover shape multiple times.
 */
internal class HoverShapeManager private constructor(
    private val searcher: (CommandEnvironment, Point) -> AbstractShape?
) {
    private val pointToTargetMap = mutableMapOf<Point, AbstractShape?>()

    fun getHoverShape(
        environment: CommandEnvironment,
        point: Point
    ): AbstractShape? = pointToTargetMap.getOrSearch(environment, point)

    private fun MutableMap<Point, AbstractShape?>.getOrSearch(
        environment: CommandEnvironment,
        point: Point
    ): AbstractShape? = getOrPut(point) {
        if (point !in this) {
            searcher.invoke(environment, point)
        } else {
            // This point is already in the map, so we don't need to search again.
            null
        }
    }

    fun resetCache() {
        pointToTargetMap.clear()
    }

    companion object {
        fun forLineConnectHover(): HoverShapeManager = HoverShapeManager { environment, point ->
            ShapeConnectorUseCase.getConnectableShape(point, environment.getShapes(point))
        }
    }
}
