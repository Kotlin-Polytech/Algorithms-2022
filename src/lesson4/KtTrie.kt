package lesson4

import java.util.*

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: SortedMap<Char, Node> = sortedMapOf()
    }

    private val root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }


    /**
     * для оценок трудоемкости и ресурсоемкости: h = высота дерева
     */
    inner class TrieIterator internal constructor() : MutableIterator<String> {
        private val charStack = Stack<Char>()
        private val forkStack = Stack<MutableIterator<MutableMap.MutableEntry<Char, Node>>>()
        private var currentString: String = ""
        private var currentIter: MutableIterator<MutableMap.MutableEntry<Char, Node>>? = null
        private val savedSize = size
        var foundStr = 0


        private fun findNextString(stop: Boolean) {
            if (!stop) {
                if (forkStack.lastElement().hasNext()) {
                    val entry = forkStack.lastElement().next()
                    charStack.push(entry.key)
                    currentIter = forkStack.lastElement()
                    val currentIterator = entry.value.children.iterator()
                    forkStack.push(currentIterator)
                    findNextString(entry.key == 0.toChar())
                } else {
                    if (charStack.isNotEmpty()) {
                        forkStack.pop()
                        charStack.pop()
                    }
                    findNextString(charStack.isEmpty() && !forkStack[0].hasNext())
                }
            } else {
                if (charStack.isNotEmpty()) {
                    charStack.pop()
                    forkStack.pop()
                }
                currentString = charStack.joinToString("")
            }
        }

        init {
            if (root.children.isNotEmpty()) {
                forkStack.push(root.children.iterator())
            }
        }


        /**
         * R = O(h) (для работы функции необходим стэк)
         * T = O(1)
         */
        override fun hasNext(): Boolean = foundStr != savedSize && savedSize != 0

        /**
         * R = O(h) (для работы функции необходим стэк)
         * T = O(h)
         */
        override fun next(): String {
            if (hasNext()) {
                findNextString(false)
                foundStr++
                return currentString
            } else throw NoSuchElementException()
        }

        /**
         * R = O(h) (для работы функции необходим стэк)
         * T = O(1)
         */
        override fun remove() {
            currentIter?.apply {
                remove()
                size--
                currentIter = null
            } ?: throw IllegalStateException()
        }
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator() = TrieIterator()
}