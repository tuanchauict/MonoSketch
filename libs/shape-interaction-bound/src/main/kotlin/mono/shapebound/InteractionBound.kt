/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.shapebound

/**
 * A sealed class to define all possible interaction bound types.
 */
sealed class InteractionBound {
    abstract val interactionPoints: List<InteractionPoint>
}
