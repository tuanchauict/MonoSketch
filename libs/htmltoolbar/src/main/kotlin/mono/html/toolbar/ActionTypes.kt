package mono.html.toolbar

/**
 * An enum class which defines all action types which repeatedly have effects after triggered.
 */
enum class RetainableActionType {
    IDLE,
    ADD_RECTANGLE,
    ADD_TEXT,
    ADD_LINE
}

/**
 * An enum class which defines all action types which are only have affect once.
 */
enum class OneTimeActionType {
    IDLE
}
