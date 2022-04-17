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
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */
    override fun iterator() = TrieIterator()


    inner class TrieIterator internal constructor() : MutableIterator<String> {
        private val wordList = mutableListOf<String>()
        private val currentString = Stack<Char>()
        var index = 0

        private fun findNextString(start: Node, isZero: Boolean) {
            if (!isZero) {
                for (childNode in start.children) {
                    currentString.push(childNode.key)
                    findNextString(childNode.value, childNode.key == 0.toChar())
                    currentString.pop()
                }
            } else {
                wordList.add(currentString.joinToString("").dropLast(1))
            }
        }

        init {
            findNextString(root, false)
        }

        override fun hasNext(): Boolean = index < wordList.size
        override fun next(): String = wordList[index++]

        override fun remove() {
            TODO("Not yet implemented")
        }
    }
}