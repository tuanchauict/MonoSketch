/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shapesearcher

import mono.common.Characters.isTransparent
import mono.graphics.bitmap.MonoBitmap
import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

/**
 * A model class which optimises shapes retrieval from a point.
 * A shape is only indexed after it's drawn onto the board. Do not use this for pre-draw check.
 */
class ShapeSearcher(
    private val shapeManager: ShapeManager,
    private val getBitmap: (AbstractShape) -> MonoBitmap?
) {
    private val shapeZoneAddressManager: ShapeZoneAddressManager =
        ShapeZoneAddressManager(getBitmap)
    private val zoneOwnersManager: ZoneOwnersManager = ZoneOwnersManager()

    fun register(shape: AbstractShape) {
        val addresses = shapeZoneAddressManager.getZoneAddresses(shape)
        zoneOwnersManager.registerOwnerAddresses(shape.id, addresses)
    }

    fun clear(bound: Rect) {
        zoneOwnersManager.clear(bound)
    }

    /**
     * Returns a list of shapes which have a non-transparent pixel at [point].
     * The other of the list is based on z-index which 1st item is the lowest index.
     * Note: Group shape is not included in the result.
     */
    fun getShapes(point: Point): List<AbstractShape> = zoneOwnersManager.getPotentialOwners(point)
        .asSequence()
        .mapNotNull { shapeManager.getShape(it) }
        .filter {
            val position = it.bound.position
            val bitmap = getBitmap(it) ?: return@filter false
            val bitmapRow = point.row - position.row
            val bitmapCol = point.column - position.column
            !bitmap.getVisual(bitmapRow, bitmapCol).isTransparent
        }
        .toList()

    fun getAllShapesInZone(bound: Rect): List<AbstractShape> =
        zoneOwnersManager.getAllPotentialOwnersInZone(bound)
            .asSequence()
            .mapNotNull { shapeManager.getShape(it) }
            .filter { it.isOverlapped(bound) }
            .toList()

    /**
     * Gets the edge direction of a shape having bound's edges at [point].
     * The considerable shape types are shapes having static bound such as [Text], [Rectangle].
     * If there are many shapes satisfying the conditions, the first one will be used.
     */
    fun getEdgeDirection(point: Point): DirectedPoint.Direction? {
        val shape = zoneOwnersManager.getPotentialOwners(point)
            .asSequence()
            .mapNotNull { shapeManager.getShape(it) }
            .filter { it is Text || it is Rectangle }
            .filter { it.contains(point) }
            .filter {
                it.bound.left == point.left ||
                    it.bound.right == point.left ||
                    it.bound.top == point.top ||
                    it.bound.bottom == point.top
            }
            .firstOrNull()
        return when {
            shape == null -> null
            shape.bound.left == point.left ||
                shape.bound.right == point.left -> DirectedPoint.Direction.VERTICAL

            else -> DirectedPoint.Direction.HORIZONTAL
        }
    }
}
