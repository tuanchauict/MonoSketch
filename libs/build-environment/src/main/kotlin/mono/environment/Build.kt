package mono.environment

/**
 * A special object for extracting build environments.
 */
object Build {
    private const val BUILD_MODE_DEVELOPMENT = "development"
    private const val BUILD_MODE_PRODUCTION = "production"

    val BUILD_MODE = js("process.env.NODE_ENV")
    val DEBUG: Boolean = BUILD_MODE == BUILD_MODE_DEVELOPMENT
}
