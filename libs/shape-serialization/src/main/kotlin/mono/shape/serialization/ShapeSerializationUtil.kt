package mono.shape.serialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * An util object for serializing shape to Json and load shape from Json
 */
object ShapeSerializationUtil {
    fun toJson(serializableShape: AbstractSerializableShape): String =
        Json.encodeToString(serializableShape)

    fun fromJson(jsonString: String): AbstractSerializableShape? = try {
        Json.decodeFromString(jsonString)
    } catch (e: Exception) {
        console.error("Error while restoring shapes")
        console.error(e)
        null
    }
}
