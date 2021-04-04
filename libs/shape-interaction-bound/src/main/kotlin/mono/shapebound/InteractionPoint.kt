package mono.shapebound

/**
 * A sealed class for defining all possible interaction point types for a shape and common apis.
 */
sealed class InteractionPoint {
    abstract val leftPx: Int
    abstract val topPx: Int
}
