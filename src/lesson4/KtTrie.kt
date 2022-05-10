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

    @Suppress("IncorrectFormatting")
    inner class TrieIterator internal constructor() : MutableIterator<String> {
        private val charStack = Stack<Char>()
        private val forkStack = Stack<MutableIterator<MutableMap.MutableEntry<Char, Node>>>()
        private var currentNode = root
        private var currentString: String = ""
        private var currentIter: MutableIterator<MutableMap.MutableEntry<Char, Node>>? = null
        private var savedIterator: MutableIterator<MutableMap.MutableEntry<Char, Node>>? = null

        private fun findNextString(stop: Boolean) {
            if (!stop) {
                if (forkStack.lastElement().hasNext()) {
                    val entry = forkStack.lastElement().next()
                    charStack.push(entry.key)
                    currentNode = entry.value
                    currentIter = forkStack.lastElement()
                    val currentIterator = currentNode.children.iterator()
                    forkStack.push(currentIterator)
                    findNextString(entry.key == 0.toChar())
                } else {
                    if (charStack.isNotEmpty()) {
                        forkStack.pop()
                        charStack.pop()
                    }
                    findNextString(charStack.isEmpty() && !forkStack.lastElement().hasNext())
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
            findNextString(root.children.isEmpty())
        }

        override fun hasNext(): Boolean = currentString != ""
        override fun next(): String {
            if (hasNext()) {
                savedIterator = currentIter
                return currentString.also {
//                    lastParentIterator = currentIterator
//                    println(lastParentIterator)
                    findNextString(false)
                }
            } else throw NoSuchElementException()
        }

        override fun remove() {
            savedIterator?.apply {
                remove()
                size--
                savedIterator = null
            } ?: throw IllegalStateException()
        }
//            lastParentNode?.apply {
//                size--
//                children.remove(0.toChar())
//                lastParentNode = null
//            } ?: throw IllegalStateException()
//            if (isDeleted) throw IllegalStateException()
//            remove(lastString).also { isDeleted = true }
//        }
    }


//    inner class TrieIterator internal constructor() : MutableIterator<String> {
//        private val wordList = mutableListOf<String>()
//        private val currentString = Stack<Char>()
//        var index = 0
//        var isDeleted = true
//
//        private fun findAllString(start: Node, stop: Boolean, currString: String) {
//            if (!stop) {
//                start.children.forEach { (key, value) ->
//                    findAllString(value, key == 0.toChar(), currString + key)
//                }
//            } else wordList.add(currString.dropLast(1))
//        }
//
//        init {
//            findAllString(root, false, "")
//        }
//
//        override fun hasNext(): Boolean = index < wordList.size
//        override fun next(): String =
//            if (hasNext()) wordList[index++].also { isDeleted = false } else throw NoSuchElementException()
//
//        override fun remove() {
//            if (isDeleted) throw IllegalStateException()
//            remove(wordList[index - 1]).also { isDeleted = true }
//        }
//    }
}