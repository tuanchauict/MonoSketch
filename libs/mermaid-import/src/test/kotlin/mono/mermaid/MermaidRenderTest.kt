package mono.mermaid

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Integration tests that parse → layout → render onto an ASCII grid,
 * so we can eyeball what the import would produce.
 */
class MermaidRenderTest {

    @Test
    fun testSimpleLinear() {
        val result = renderToAscii("""
            graph TD
                A[Start] --> B[Process] --> C[End]
        """.trimIndent())
        assertNotNull(result)
        assertTrue("Start" in result, "Missing 'Start' in:\n$result")
        assertTrue("Process" in result, "Missing 'Process' in:\n$result")
        assertTrue("End" in result, "Missing 'End' in:\n$result")
        println(result)
    }

    @Test
    fun testDecisionBranch() {
        val result = renderToAscii("""
            graph TD
                A[Login] --> B{Valid?}
                B -->|Yes| C[Dashboard]
                B -->|No| D[Error]
        """.trimIndent())
        assertNotNull(result)
        assertTrue("Login" in result, "Missing 'Login' in:\n$result")
        assertTrue("Valid?" in result, "Missing 'Valid?' in:\n$result")
        assertTrue("Dashboard" in result, "Missing 'Dashboard' in:\n$result")
        assertTrue("Error" in result, "Missing 'Error' in:\n$result")
        println(result)
    }

    @Test
    fun testCICD() {
        val result = renderToAscii("""
            graph TD
                A[Push Code] --> B[Run Tests]
                B --> C{Tests Pass?}
                C -->|Yes| D[Deploy]
                C -->|No| E[Fix]
        """.trimIndent())
        assertNotNull(result)
        assertTrue("Push Code" in result, "Missing 'Push Code' in:\n$result")
        assertTrue("Run Tests" in result, "Missing 'Run Tests' in:\n$result")
        assertTrue("Tests Pass?" in result, "Missing 'Tests Pass?' in:\n$result")
        assertTrue("Deploy" in result, "Missing 'Deploy' in:\n$result")
        assertTrue("Fix" in result, "Missing 'Fix' in:\n$result")
        println(result)
    }

    @Test
    fun testFanOutFanIn() {
        val result = renderToAscii("""
            graph TD
                Start[Start] --> A[Task A]
                Start --> B[Task B]
                Start --> C[Task C]
                A --> Done[Done]
                B --> Done
                C --> Done
        """.trimIndent())
        assertNotNull(result)
        assertTrue("Start" in result, "Missing 'Start' in:\n$result")
        assertTrue("Task A" in result, "Missing 'Task A' in:\n$result")
        assertTrue("Task B" in result, "Missing 'Task B' in:\n$result")
        assertTrue("Task C" in result, "Missing 'Task C' in:\n$result")
        assertTrue("Done" in result, "Missing 'Done' in:\n$result")
        println(result)
    }

    /**
     * Renders a mermaid flowchart to an ASCII character grid.
     * Draw order: box borders → edges → labels (so labels are always visible).
     */
    private fun renderToAscii(mermaidText: String): String? {
        val diagram = MermaidParser.parse(mermaidText) ?: return null
        val layout = MermaidLayoutEngine.layout(diagram)
        if (layout.nodes.isEmpty()) return null

        val maxRight = layout.nodes.maxOf { it.left + it.width }
        val maxBottom = layout.nodes.maxOf { it.top + it.height }
        val grid = Array(maxBottom) { CharArray(maxRight) { ' ' } }

        fun inBounds(row: Int, col: Int) = row in grid.indices && col in grid[0].indices
        fun setIfEmpty(row: Int, col: Int, ch: Char) {
            if (inBounds(row, col) && grid[row][col] == ' ') grid[row][col] = ch
        }

        // 1. Draw box borders
        for (n in layout.nodes) {
            for (col in n.left until n.left + n.width) {
                if (inBounds(n.top, col)) grid[n.top][col] = '-'
                if (inBounds(n.bottom, col)) grid[n.bottom][col] = '-'
            }
            for (row in n.top..n.bottom) {
                if (inBounds(row, n.left)) grid[row][n.left] = '|'
                if (inBounds(row, n.right)) grid[row][n.right] = '|'
            }
        }

        // 2. Draw edges
        for (e in layout.edges) {
            if (e.startLeft == e.endLeft) {
                val col = e.startLeft
                for (row in minOf(e.startTop, e.endTop)..maxOf(e.startTop, e.endTop))
                    setIfEmpty(row, col, '|')
            } else if (e.startTop == e.endTop) {
                val row = e.startTop
                for (col in minOf(e.startLeft, e.endLeft)..maxOf(e.startLeft, e.endLeft))
                    setIfEmpty(row, col, '-')
            } else {
                // L-shaped: vertical then horizontal
                for (row in minOf(e.startTop, e.endTop)..maxOf(e.startTop, e.endTop))
                    setIfEmpty(row, e.startLeft, '|')
                for (col in minOf(e.startLeft, e.endLeft)..maxOf(e.startLeft, e.endLeft))
                    setIfEmpty(e.endTop, col, '-')
            }
        }

        // 3. Draw labels last (always on top)
        for (n in layout.nodes) {
            val labelRow = n.top + n.height / 2
            val labelStart = n.left + (n.width - n.node.label.length) / 2
            for ((i, ch) in n.node.label.withIndex()) {
                val col = labelStart + i
                if (inBounds(labelRow, col)) grid[labelRow][col] = ch
            }
        }

        return grid.joinToString("\n") { it.concatToString().trimEnd() }
    }
}
