package mono.shapesearcher

import mono.common.Characters.isTransparent
import mono.graphics.bitmap.MonoBitmapManager
import mono.graphics.geo.Point
import mono.graphics.geo.Rect
import mono.shape.ShapeManager
import mono.shape.shape.AbstractShape

/**
 * A model class which optimises shapes retrieval from a point.
 */
class ShapeSearcher(
    private val shapeManager: ShapeManager,
    private val bitmapManager: MonoBitmapManager
) {
    private val shapeZoneAddressManager: ShapeZoneAddressManager =
        ShapeZoneAddressManager(bitmapManager)
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
            val bitmap = bitmapManager.getBitmap(it) ?: return@filter false
            val bitmapRow = point.row - position.row
            val bitmapCol = point.column - position.column
            !bitmap.get(bitmapRow, bitmapCol).isTransparent
        }
        .toList()
}
