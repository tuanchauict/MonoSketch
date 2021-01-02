package mono.list

/**
 * A collection which works similar to [LinkedHashMap] by making accessing item fast while keeping
 * the order of items based on add-sequence.
 * This also supports move up/down/top/bottom of the list for an item as well as adding item into
 * the head or the tail or after an specific item.
 */
class QuickList<T : QuickList.Identifier> : Collection<T> {
    // TODO: Use double linked list for list to make add(), remove(), move() O(1).
    private val list: MutableList<T> = mutableListOf()
    private val map: MutableMap<Int, T> = mutableMapOf()

    override val size: Int
        get() = list.size

    override fun contains(element: T): Boolean = element.id in map

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { it in this }

    override fun isEmpty(): Boolean = list.isEmpty()

    override fun iterator(): Iterator<T> = list.iterator()

    fun add(element: T, position: AddPosition = AddPosition.Last): Boolean {
        if (element in this) {
            return false
        }
        map[element.id] = element

        val index = when (position) {
            AddPosition.Last -> size
            AddPosition.First -> 0
            is AddPosition.After -> {
                val previous = map[position.identifier.id]
                val previousIndex = list.indexOf(previous)
                if (previousIndex >= 0) previousIndex + 1 else size
            }
        }
        list.add(index, element)
        return true
    }

    fun addAll(collection: Collection<T>, position: AddPosition = AddPosition.Last) {
        var previous = position
        for (element in collection) {
            add(element, previous)
            previous = when (previous) {
                AddPosition.Last -> AddPosition.Last
                is AddPosition.After,
                AddPosition.First -> AddPosition.After(element)
            }
        }
    }

    fun remove(identifier: Identifier): T? {
        if (identifier.id !in map) {
            return null
        }
        val element = map.remove(identifier.id)
        list.remove(element)
        return element
    }

    fun removeAll(): List<T> {
        val result = list.toList()
        list.clear()
        map.clear()
        return result
    }

    operator fun get(id: Int): T? = map[id]

    fun move(identifier: Identifier, moveActionType: MoveActionType) {
        if (size < 2) {
            return
        }
        when (moveActionType) {
            MoveActionType.UP -> moveUp(identifier)
            MoveActionType.DOWN -> moveDown(identifier)
            MoveActionType.TOP -> moveTop(identifier)
            MoveActionType.BOTTOM -> moveBottom(identifier)
        }
    }

    private fun moveUp(identifier: Identifier) {
        if (list.last().id == identifier.id) {
            return
        }
        val shape = map[identifier.id] ?: return
        val index = list.indexOf(shape)
        list[index] = list[index + 1]
        list[index + 1] = shape
    }

    private fun moveDown(identifier: Identifier) {
        if (list.first().id == identifier.id) {
            return
        }
        val shape = map[identifier.id] ?: return
        val index = list.indexOf(shape)
        list[index] = list[index - 1]
        list[index - 1] = shape
    }

    private fun moveTop(identifier: Identifier) {
        if (list.last().id == identifier.id) {
            return
        }
        val shape = remove(identifier) ?: return
        add(shape)
    }

    private fun moveBottom(identifier: Identifier) {
        if (list.first().id == identifier.id) {
            return
        }
        val shape = remove(identifier) ?: return
        add(shape, AddPosition.First)
    }

    interface Identifier {
        val id: Int
    }

    sealed class AddPosition {
        object First : AddPosition()
        object Last : AddPosition()
        data class After(val identifier: Identifier) : AddPosition()
    }

    enum class MoveActionType {
        UP, DOWN, TOP, BOTTOM
    }
}
