package mono.shape.shape

import mono.shape.serialization.SerializableGroup

/**
 * A special [Group] for the root group of the file.
 * This group contains some extra information for storing and restoring from file.
 */
class RootGroup : Group {
    constructor(id: String?) : super(id, parentId = null)
    constructor(serializableGroup: SerializableGroup) : super(serializableGroup, parentId = null)
}
