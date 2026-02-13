package mono.mermaid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MermaidParserTest {

    @Test
    fun testBasicFlowchart() {
        val diagram = MermaidParser.parse("""
            graph TD
                A[Start] --> B{Decision}
                B -->|Yes| C[OK]
                B -->|No| D[End]
                C --> D
        """.trimIndent())

        assertNotNull(diagram)
        assertEquals(4, diagram.nodes.size)
        assertEquals(4, diagram.edges.size)
        assertTrue(diagram.nodes.map { it.id }.containsAll(listOf("A", "B", "C", "D")))
    }

    @Test
    fun testNodeShapes() {
        val diagram = MermaidParser.parse("""
            graph LR
                A[Rect] --> B(Rounded) --> C{Diamond} --> D((Circle))
        """.trimIndent())

        assertNotNull(diagram)
        val byId = diagram.nodes.associateBy { it.id }
        assertEquals(MermaidNode.NodeShape.RECTANGLE, byId["A"]?.shape)
        assertEquals(MermaidNode.NodeShape.ROUNDED_RECTANGLE, byId["B"]?.shape)
        assertEquals(MermaidNode.NodeShape.DIAMOND, byId["C"]?.shape)
        assertEquals(MermaidNode.NodeShape.CIRCLE, byId["D"]?.shape)
    }

    @Test
    fun testEdgeStyles() {
        val diagram = MermaidParser.parse("""
            graph TD
                A --> B
                B --- C
                C -.-> D
                D ==> E
        """.trimIndent())

        assertNotNull(diagram)
        assertEquals(MermaidEdge.EdgeStyle.SOLID_ARROW, diagram.edges[0].style)
        assertEquals(MermaidEdge.EdgeStyle.SOLID_LINE, diagram.edges[1].style)
        assertEquals(MermaidEdge.EdgeStyle.DOTTED_ARROW, diagram.edges[2].style)
        assertEquals(MermaidEdge.EdgeStyle.THICK_ARROW, diagram.edges[3].style)
    }

    @Test
    fun testEdgeLabels() {
        val diagram = MermaidParser.parse("""
            graph TD
                A -->|yes| B
        """.trimIndent())

        assertNotNull(diagram)
        assertEquals("yes", diagram.edges[0].label)
    }

    @Test
    fun testCommentsIgnored() {
        val diagram = MermaidParser.parse("""
            graph TD
                %% this is a comment
                A --> B
        """.trimIndent())

        assertNotNull(diagram)
        assertEquals(1, diagram.edges.size)
    }

    @Test
    fun testInvalidInput() {
        assertNull(MermaidParser.parse(""))
        assertNull(MermaidParser.parse("not a diagram"))
        assertNull(MermaidParser.parse("sequenceDiagram\nAlice->>Bob: Hi"))
    }

    @Test
    fun testFlowchartKeyword() {
        val diagram = MermaidParser.parse("""
            flowchart LR
                A --> B
        """.trimIndent())
        assertNotNull(diagram)
        assertEquals(2, diagram.nodes.size)
    }
}
