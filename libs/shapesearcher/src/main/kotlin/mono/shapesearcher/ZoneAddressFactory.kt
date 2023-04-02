/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shapesearcher

/**
 * A data class to identify address of a zone
 */
internal data class ZoneAddress(val row: Int, val column: Int)

/**
 * A factory of [ZoneAddress].
 */
internal object ZoneAddressFactory {
    private val addressMap: MutableMap<Int, MutableMap<Int, ZoneAddress>> = mutableMapOf()

    init {
        for (left in -4..20) {
            addressMap[left] = mutableMapOf()
            for (top in -4..20) {
                addressMap[left]!![top] = ZoneAddress(left, top)
            }
        }
    }

    fun toAddress(row: Int, column: Int): ZoneAddress {
        val rowAddressIndex = toAddressIndex(row)
        val colAddressIndex = toAddressIndex(column)
        return get(rowAddressIndex, colAddressIndex)
    }

    fun get(rowAddressIndex: Int, colAddressIndex: Int): ZoneAddress =
        addressMap.getOrPut(rowAddressIndex) { mutableMapOf() }
            .getOrPut(colAddressIndex) { ZoneAddress(rowAddressIndex, colAddressIndex) }

    fun toAddressIndex(number: Int) = number shr 4
}
