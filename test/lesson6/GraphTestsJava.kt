package lesson6

import lesson2.JavaAlgorithms
import org.junit.jupiter.api.Tag
import kotlin.test.Test

class GraphTestsJava : AbstractGraphTests() {
    @Test
    @Tag("Normal")
    fun testFindEulerLoopJava() {
        findEulerLoop { let { JavaGraphTasks.findEulerLoop(it) } }
    }

    @Test
    @Tag("Normal")
    fun testMinimumSpanningTreeJava() {
        minimumSpanningTree { let { JavaGraphTasks.minimumSpanningTree(it) } }
    }

    @Test
    @Tag("Hard")
    fun testLargestIndependentVertexSetJava() {
        largestIndependentVertexSet { let { JavaGraphTasks.largestIndependentVertexSet(it) } }
    }

    @Test
    @Tag("Hard")
    fun testLongestSimplePathJava() {
        longestSimplePath { let { JavaGraphTasks.longestSimplePath(it) } }
    }

    @Test
    @Tag("Hard")
    fun testBaldaSearcherJava() {
        baldaSearcher { inputName, words -> JavaGraphTasks.baldaSearcher(inputName, words) }
    }
}