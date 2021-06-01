package mono.graphics.geo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Size(
    @SerialName("w")
    val width: Int,
    @SerialName("h")
    val height: Int
) {
    companion object {
        val ZERO = Size(0, 0)
    }
}

data class SizeF(val width: Double, val height: Double)
