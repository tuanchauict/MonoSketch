package mono.list

/**
 * A collection which works similar to [LinkedHashMap] by making accessing item fast while keeping
 * the order of items based on add-sequence.
 * This also supports move up/down/top/bottom of the list for an item as well as adding item into
 * the head or the tail or after an specific item.
 */
class QuickList<T : QuickList.Identifier> : Collection<T> {
    private val linkedList: DoubleLinkedList<T> = DoubleLinkedList()
    private val map: MutableMap<Int, Node<T>> = mutableMapOf()

    override val size: Int
        get() = map.size

    override fun contains(element: T): Boolean = element.id in map

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { it in this }

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun iterator(): Iterator<T> = linkedList.iterator()

    fun add(element: T, position: AddPosition = AddPosition.Last): Boolean {
        if (element in this) {
            return false
        }

        val preNode = when (position) {
            AddPosition.Last -> linkedList.tail.pre
            AddPosition.First -> linkedList.head
            is AddPosition.After -> map[position.identifier.id]
        } ?: return false
        val node = linkedList.add(element, preNode)
        map[element.id] = node

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
        val node = map.remove(identifier.id) ?: return null
        linkedList.remove(node)

        return node.value
    }

    fun removeAll(): List<T> {
        val result = linkedList.iterator().asSequence().toList()
        linkedList.clear()
        map.clear()
        return result
    }

    operator fun get(id: Int): T? = map[id]?.value

    fun move(identifier: Identifier, moveActionType: MoveActionType) {
        if (size < 2) {
            return
        }

        val node = map[identifier.id] ?: return
        linkedList.move(node, moveActionType)
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

    private class Node<T>(var pre: Node<T>?, var next: Node<T>?, val value: T?)

    private class DoubleLinkedList<T> {
        // Publishing head and tail is unsafe. However, since they are only used under QuickList
        // context, this is acceptable. head and tail's next and pre pointers must not be changed
        // outside this class.
        val head: Node<T> = Node(null, null, null)
        val tail: Node<T> = Node(null, null, null)

        init {
            head.next = tail
            tail.pre = head
        }

        fun add(value: T, previousNode: Node<T>): Node<T> {
            val afterNode = previousNode.next
            val node = Node(previousNode, afterNode, value)
            previousNode.next = node
            afterNode?.pre = node
            return node
        }

        fun remove(node: Node<T>) {
            val previousNode = node.pre
            val afterNode = node.next
            previousNode?.next = afterNode
            afterNode?.pre = previousNode
            node.next = null
            node.pre = null
        }

        fun clear() {
            head.next?.pre = null
            tail.pre?.next = null
            head.next = tail
            tail.pre = head
        }

        fun move(node: Node<T>, moveActionType: MoveActionType) {
            val newPreviousNode = when (moveActionType) {
                MoveActionType.UP -> node.next
                MoveActionType.DOWN -> node.pre?.pre
                MoveActionType.TOP -> tail.pre
                MoveActionType.BOTTOM -> head
            } ?: return
            remove(node)

            val newNextNode = newPreviousNode.next
            newPreviousNode.next = node
            newNextNode?.pre = node
            node.pre = newPreviousNode
            node.next = newNextNode
        }

        fun iterator(): Iterator<T> = DoubleLinkedListIterator(head)

        private class DoubleLinkedListIterator<T>(private var head: Node<T>?) : Iterator<T> {

            override fun hasNext(): Boolean {
                val nextNext = head?.next?.next
                return nextNext != null
            }

            override fun next(): T {
                val result = head?.next
                head = head?.next
                return result?.value!!
            }
        }
    }
}
