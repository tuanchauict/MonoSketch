import type { Identifier } from "./identifier";

export enum MoveActionType {
    UP,
    DOWN,
    TOP,
    BOTTOM
}

export class AddPosition {
    static First = new AddPosition(null);
    static Last = new AddPosition(null);

    static After(identifier: Identifier): AddPosition {
        return new AddPosition(identifier);
    }

    private constructor(public identifier: Identifier | null) {
    }
}

/**
 * A collection which works similar to LinkedHashMap in Java by making accessing item fast while keeping
 * the order of items based on add-sequence.
 * This also supports move up/down/top/bottom of the list for an item as well as adding item into
 * the head or the tail or after a specific item.
 */
export class QuickList<T extends Identifier> implements Iterable<T> {
    private linkedList: DoubleLinkedList<T> = new DoubleLinkedList<T>();
    private map: Map<string, Node<T>> = new Map<string, Node<T>>();

    get size(): number {
        return this.map.size;
    }

    contains(element: T): boolean {
        return this.map.has(element.id);
    }

    containsAll(elements: Iterable<T>): boolean {
        for (const element of elements) {
            if (!this.contains(element)) {
                return false;
            }
        }
        return true;
    }

    isEmpty(): boolean {
        return this.size === 0;
    }

    [Symbol.iterator](): Iterator<T> {
        return this.linkedList.iterator();
    }

    add(element: T, position: AddPosition = AddPosition.Last): boolean {
        if (this.contains(element)) {
            return false;
        }

        const preNode = (() => {
            switch (position) {
                case AddPosition.Last:
                    return this.linkedList.tail.pre;
                case AddPosition.First:
                    return this.linkedList.head;
                default: // AddPosition after
                    return this.map.get(position.identifier!.id);
            }
        })();

        if (!preNode) {
            return false;
        }

        const node = this.linkedList.add(element, preNode);
        this.map.set(element.id, node);

        return true;
    }

    addAll(collection: Iterable<T>, position: AddPosition = AddPosition.Last) {
        let previous = position;
        for (const element of collection) {
            this.add(element, previous);
            previous = AddPosition.After(element);
        }
    }

    remove(identifier: Identifier): T | null {
        const node = this.map.get(identifier.id);
        if (!node) {
            return null;
        }
        this.map.delete(identifier.id);
        this.linkedList.remove(node);
        return node.value;
    }

    removeAll(): T[] {
        const result = Array.from(this);
        this.linkedList.clear();
        this.map.clear();
        return result;
    }

    get(id: string): T | null {
        return this.map.get(id)?.value || null;
    }

    move(identifier: Identifier, moveActionType: MoveActionType): boolean {
        if (this.size < 2) {
            return false;
        }

        const node = this.map.get(identifier.id);
        if (!node) {
            return false;
        }
        return this.linkedList.move(node, moveActionType);
    }
}

class Node<T> {
    constructor(
        public pre: Node<T> | null,
        public next: Node<T> | null,
        public value: T | null,
    ) {
    }
}

class DoubleLinkedList<T> {
    head: Node<T> = new Node<T>(null, null, null);
    tail: Node<T> = new Node<T>(null, null, null);

    constructor() {
        this.head.next = this.tail;
        this.tail.pre = this.head;
    }

    add(value: T, previousNode: Node<T>): Node<T> {
        const afterNode = previousNode.next;
        const node = new Node<T>(previousNode, afterNode, value);
        previousNode.next = node;
        if (afterNode) {
            afterNode.pre = node;
        }
        return node;
    }

    remove(node: Node<T>) {
        const previousNode = node.pre;
        const afterNode = node.next;
        if (previousNode) {
            previousNode.next = afterNode;
        }
        if (afterNode) {
            afterNode.pre = previousNode;
        }
        node.next = null;
        node.pre = null;
    }

    clear(): void {
        this.head.next = this.tail;
        this.tail.pre = this.head;
    }

    move(node: Node<T>, moveActionType: MoveActionType): boolean {
        let newPreviousNode: Node<T> | null | undefined;
        switch (moveActionType) {
            case MoveActionType.UP:
                newPreviousNode = node.next !== this.tail ? node.next : null;
                break;
            case MoveActionType.TOP:
                newPreviousNode = node.next !== this.tail ? this.tail.pre : null;
                break;
            case MoveActionType.DOWN:
                newPreviousNode = node.pre !== this.head ? node.pre?.pre : null;
                break;
            case MoveActionType.BOTTOM:
                newPreviousNode = node.pre !== this.head ? this.head : null;
                break;
            default:
                return false;
        }

        if (!newPreviousNode) {
            return false;
        }

        this.remove(node);

        const newNextNode = newPreviousNode.next;
        newPreviousNode.next = node;
        if (newNextNode) {
            newNextNode.pre = node;
        }
        node.pre = newPreviousNode;
        node.next = newNextNode;
        return true;
    }

    * iterator(): IterableIterator<T> {
        let current = this.head.next;
        while (current && current !== this.tail) {
            if (current.value) {
                yield current.value;
            }
            current = current.next;
        }
    }
}
