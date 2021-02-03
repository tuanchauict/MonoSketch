package mono.shapesearcher

import mono.graphics.geo.Point
import mono.graphics.geo.Rect

/**
 * A model class to store owners of a zone whole size = 8x8 to fast identify potential candidate
 * owners of a position (row, column).
 * Owners of a zone will be stored in the same order as owners are added if overlapped.
 */
internal class ZoneOwnersManager {
    private val zoneToOwnersMap: MutableMap<ZoneAddress, MutableList<Int>> = mutableMapOf()

    fun clear(clearBound: Rect) {
        val leftIndex = ZoneAddressFactory.toAddressIndex(clearBound.left)
        val rightIndex = ZoneAddressFactory.toAddressIndex(clearBound.right)
        val topIndex = ZoneAddressFactory.toAddressIndex(clearBound.top)
        val bottomIndex = ZoneAddressFactory.toAddressIndex(clearBound.bottom)

        for (rowIndex in topIndex..bottomIndex) {
            for (colIndex in leftIndex..rightIndex) {
                val zone = ZoneAddressFactory.get(rowIndex, colIndex)
                zoneToOwnersMap[zone]?.clear()
            }
        }
    }

    fun registerOwnerAddresses(ownerId: Int, addresses: Set<ZoneAddress>) {
        println("> $ownerId $addresses")
        for (address in addresses) {
            zoneToOwnersMap.getOrPut(address) { mutableListOf() }.add(ownerId)
        }
    }

    fun getPotentialOwners(point: Point): List<Int> {
        val address = ZoneAddressFactory.toAddress(point.row, point.column)
        return zoneToOwnersMap[address].orEmpty()
    }
}
