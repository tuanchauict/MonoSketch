package mono.shapesearcher

import mono.graphics.geo.Rect
import mono.shapesearcher.ZoneAddressFactory.toAddress
import mono.shapesearcher.ZoneAddressFactory.toAddressIndex

/**
 * A model class to store owners of a zone whole size = 8x8 to fast identify potential candidate
 * owners of a position (row, column).
 * Owners of a zone will be stored in the same order as owners are added if overlapped.
 */
internal class ZoneOwnersManager {
    private val zoneToOwnersMap: MutableMap<ZoneAddress, MutableList<Int>> = mutableMapOf()

    fun clear(clearBound: Rect) {
        val leftIndex = toAddressIndex(clearBound.left)
        val rightIndex = toAddressIndex(clearBound.right)
        val topIndex = toAddressIndex(clearBound.top)
        val bottomIndex = toAddressIndex(clearBound.bottom)

        for (rowIndex in topIndex..bottomIndex) {
            for (colIndex in leftIndex..rightIndex) {
                val zone = toAddress(rowIndex, colIndex)
                zoneToOwnersMap[zone]?.clear()
            }
        }
    }

    fun addOwner(ownerId: Int, addresses: Set<ZoneAddress>) {
        for (address in addresses) {
            zoneToOwnersMap.getOrPut(address) { mutableListOf() }.add(ownerId)
        }
    }

    fun getPotentialOwners(row: Int, column: Int): List<Int> =
        zoneToOwnersMap[toAddress(row, column)].orEmpty()
}
