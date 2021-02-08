package mono.graphics.geo

/**
 * An enum to indicates edge related position of a rectangle.
 * LEFT_TOP       MIDDLE_TOP     RIGHT_TOP
 * LEFT_MIDDLE                   RIGHT_MIDDLE
 * LEFT_BOTTOM   MIDDLE_BOTTOM   RIGHT_BOTTOM
 */
enum class EdgeRelatedPosition {
    LEFT_TOP, MIDDLE_TOP, RIGHT_TOP,
    LEFT_MIDDLE, RIGHT_MIDDLE,
    LEFT_BOTTOM, MIDDLE_BOTTOM, RIGHT_BOTTOM
}
