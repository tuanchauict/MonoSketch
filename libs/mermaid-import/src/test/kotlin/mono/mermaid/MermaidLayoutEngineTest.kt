package mono.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MermaidLayoutEngineTest {

    @Test
    fun testSingleNode() {
        val result = MermaidLayoutEngine.layout(
            MermaidDiagram(listOf(MermaidNode("A", "Hello")), emptyList())
        )
        assertEquals(1, result.nodes.size)
        assertTrue(result.nodes[0].width >= 10)
        assertTrue(result.nodes[0].height >= 3)
    }

    @Test
    fun testChainLayout() {
        val result = MermaidLayoutEngine.layout(
            MermaidDiagram(
                listOf(MermaidNode("A", "X"), MermaidNode("B", "Y"), MermaidNode("C", "Z")),
                listOf(MermaidEdge("A", "B"), MermaidEdge("B", "C"))
            )
        )
        val a = result.nodes.first { it.node.id == "A" }
        val b = result.nodes.first { it.node.id == "B" }
        val c = result.nodes.first { it.node.id == "C" }

        assertTrue(b.top > a.top, "B should be below A")
        assertTrue(c.top > b.top, "C should be below B")
    }

    @Test
    fun testLongerLabelIsWider() {
        val result = MermaidLayoutEngine.layout(
            MermaidDiagram(
                listOf(MermaidNode("A", "X"), MermaidNode("B", "A much longer label")),
                listOf(MermaidEdge("A", "B"))
            )
        )
        val a = result.nodes.first { it.node.id == "A" }
        val b = result.nodes.first { it.node.id == "B" }
        assertTrue(b.width > a.width)
    }

    @Test
    fun testEmptyDiagram() {
        val result = MermaidLayoutEngine.layout(MermaidDiagram(emptyList(), emptyList()))
        assertEquals(0, result.nodes.size)
        assertEquals(0, result.edges.size)
    }
}
