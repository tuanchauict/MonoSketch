package mono.graphics.geo

data class Size(val width: Int, val height: Int) {
    companion object {
        val ZERO = Size(0, 0)
    }
}

data class SizeF(val width: Double, val height: Double)
