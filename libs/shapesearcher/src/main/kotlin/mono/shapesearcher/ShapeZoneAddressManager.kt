package mono.shapesearcher

import mono.graphics.bitmap.MonoBitmapManager
import mono.shape.shape.AbstractShape

/**
 * A model class to convert a shape to addresses which the shape have pixels on.
 * This class also cache the addresses belong to the shape along with its version.
 */
internal class ShapeZoneAddressManager(private val bitmapManager: MonoBitmapManager) {
    private val idToZoneAddressMap: MutableMap<Int, VersionizedZoneAddresses> = mutableMapOf()

    fun getZoneAddresses(shape: AbstractShape): Set<ZoneAddress> {
        val cachedAddresses = getCachedAddresses(shape)
        if (cachedAddresses != null) {
            return cachedAddresses
        }

        val bitmap = bitmapManager.getBitmap(shape)
        if (bitmap == null) {
            idToZoneAddressMap.remove(shape.id)
            return emptySet()
        }
        val position = shape.bound.position
        val addresses = mutableSetOf<ZoneAddress>()
        for (ir in bitmap.matrix.indices) {
            bitmap.matrix[ir].forEachIndex { ic, _ ->
                val row = ir + position.row
                val col = ic + position.column
                val address = ZoneAddressFactory.toAddress(row, col)
                addresses.add(address)
            }
        }
        val versionizedZoneAddresses = VersionizedZoneAddresses(shape.version, addresses)
        idToZoneAddressMap[shape.id] = versionizedZoneAddresses
        return versionizedZoneAddresses.addresses
    }

    private fun getCachedAddresses(shape: AbstractShape): Set<ZoneAddress>? =
        idToZoneAddressMap[shape.id]?.takeIf { it.version == shape.version }?.addresses

    private class VersionizedZoneAddresses(val version: Int, val addresses: Set<ZoneAddress>)
}
