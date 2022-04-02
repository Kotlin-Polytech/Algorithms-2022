package lesson3

import java.util.*
import kotlin.math.max

// attention: Comparable is supported but Comparator is not
class KtBinarySearchTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private open class Node<T>(
        val value: T
    ) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    private var root: Node<T>? = null

    override var size = 0
        private set

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    private fun findWithParentOrNull(
        start: Node<T>,
        value: T,
        parent: Node<T>?,
        isRightChild: Boolean
    ): Triple<Node<T>, Node<T>?, Boolean>? {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> Triple(start, parent, isRightChild)
            comparison < 0 -> start.left?.let { findWithParentOrNull(it, value, start, false) }
            else -> start.right?.let { findWithParentOrNull(it, value, start, true) }
        }
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    /**
     * Добавление элемента в дерево
     *
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     *
     * Спецификация: [java.util.Set.add] (Ctrl+Click по add)
     *
     * Пример
     */
    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    /**
     * Удаление элемента из дерева
     *
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     * (в Котлине тип параметера изменён с Object на тип хранимых в дереве данных)
     *
     * Средняя
     *
     * N - количество элементов
     * h - высота дерева
     *
     * R = O(1)
     * T = O(h)
     */


    private fun deleteNode(replacement: Node<T>?, parent: Node<T>?, isRightChild: Boolean) {
        when (isRightChild) {
            true -> parent?.let { it.right = replacement } ?: run { root = replacement }
            false -> parent?.let { it.left = replacement } ?: run { root = replacement }
        }
    }

    private fun remove(foundNode: Node<T>, foundParent: Node<T>?, isRightChild: Boolean) {
        when {
            foundNode.left == null && foundNode.right == null -> deleteNode(null, foundParent, isRightChild)
            foundNode.left != null && foundNode.right == null -> deleteNode(foundNode.left, foundParent, isRightChild)
            foundNode.right != null && foundNode.left == null -> deleteNode(foundNode.right, foundParent, isRightChild)
            else -> {
                //turn left
                var isRight = false
                var maxNode = foundNode.left
                var maxParent: Node<T>? = foundNode

                // max in left part()
                while (maxNode!!.right != null) { // O(h)
                    isRight = true
                    maxParent = maxNode; maxNode = maxNode.right
                }
                // node deletion
                maxParent?.apply {
                    if (isRight) right = maxNode.left
                    else left = maxNode.left
                }

                maxNode.apply {
                    right = foundNode.right; foundNode.right = null
                    left = foundNode.left; foundNode.left = null
                }

                if (foundParent != null) {
                    if (isRightChild) foundParent.right = maxNode
                    else foundParent.left = maxNode
                } else {
                    root = maxNode
                }
            }
        }
        size--
    }

    override fun remove(element: T): Boolean {
        val (foundNode, foundParent, isRightChild) =
            findWithParentOrNull(root ?: return false, element, null, false) ?: return false // O(h)
        println("   remove found node=${foundNode.value}, parent=${foundParent?.value}, $isRightChild")
        remove(foundNode, foundParent, isRightChild)
        return true
    }

    override fun comparator(): Comparator<in T>? =
        null

    override fun iterator(): MutableIterator<T> =
        BinarySearchTreeIterator()

    inner class BinarySearchTreeIterator internal constructor() : MutableIterator<T> {
        private val nodeStack = Stack<Node<T>>()
        private var current = root

        private var currentNodeParent: Node<T>? = null

        private fun minInLeft() { // O(h)
            while (current != null) {
                nodeStack.push(current)
                current = current!!.left
            }
        }

        init {
            minInLeft()
        }

        /**
         * Проверка наличия следующего элемента
         *
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         *
         * Спецификация: [java.util.Iterator.hasNext] (Ctrl+Click по hasNext)
         *
         * Средняя
         *
         * R = O(h) (для работы функции необходим стэк)
         * T = O(1)
         *
         */
        override fun hasNext(): Boolean = nodeStack.isNotEmpty()

        /**
         * Получение следующего элемента
         *
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         *
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         *
         * Спецификация: [java.util.Iterator.next] (Ctrl+Click по next)
         *
         * Средняя
         *
         *
         * R = O(h) (для работы функции необходим стэк)
         * T = O(h)
         */
        private var lastNode: Node<T>? = null
        private var lastParent: Node<T>? = null

        override fun next(): T {
            if (hasNext()) {
                lastNode = nodeStack.pop()
                lastParent = currentNodeParent

                if (lastNode!!.right != null) {
                    currentNodeParent = lastNode
                    current = lastNode!!.right
                    minInLeft()
                } else if (nodeStack.isNotEmpty()) {
                    lastParent = nodeStack.last()
                }
                return lastNode!!.value
            } else throw NoSuchElementException()
        }

        /**
         * Удаление предыдущего элемента
         *
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         *
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         *
         * Спецификация: [java.util.Iterator.remove] (Ctrl+Click по remove)
         *
         * Сложная
         *
         *
         * R = O(h) (для работы функции необходим стэк)
         * T = O(h)
         */
        override fun remove() {
            if (lastNode == null) throw IllegalStateException()
            println("\n   iter found node=${lastNode!!.value}, parent= ${lastParent?.value}, ${lastParent?.right == lastNode}, stack= ${nodeStack.map { it.value }}\n" +
                    "   parent.left= ${lastParent?.left?.value}, parent.right = ${lastParent?.right?.value}")
            this@KtBinarySearchTree.remove(lastNode!!, lastParent, lastParent?.right == lastNode)
//            this@KtBinarySearchTree.remove(lastNode!!.value)
            lastNode = null
        }
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.subSet] (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.headSet] (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     *
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     *
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     *
     * Спецификация: [java.util.SortedSet.tailSet] (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     *
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }

    override fun height(): Int =
        height(root)

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

}