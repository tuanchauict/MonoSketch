package mono.mermaid

import mono.graphics.geo.DirectedPoint
import mono.graphics.geo.Rect
import mono.shape.shape.AbstractShape
import mono.shape.shape.Line
import mono.shape.shape.Rectangle
import mono.shape.shape.Text

/**
 * Converts [MermaidLayoutEngine.LayoutResult] into MonoSketch shapes.
 * Nodes become [Rectangle] + [Text], edges become [Line].
 */
object MermaidShapeGenerator {

    fun generate(
        layout: MermaidLayoutEngine.LayoutResult,
        parentId: String
    ): List<AbstractShape> {
        val shapes = mutableListOf<AbstractShape>()

        for (n in layout.nodes) {
            val rect = Rect.byLTWH(n.left, n.top, n.width, n.height)
            shapes.add(Rectangle(rect, parentId = parentId))
            if (n.node.label.isNotEmpty()) {
                val text = Text(rect, parentId = parentId)
                text.setText(n.node.label)
                shapes.add(text)
            }
        }

        for (e in layout.edges) {
            val startDir = direction(e.startLeft, e.startTop, e.endLeft, e.endTop)
            val endDir = direction(e.endLeft, e.endTop, e.startLeft, e.startTop)
            shapes.add(
                Line(
                    DirectedPoint(startDir, e.startLeft, e.startTop),
                    DirectedPoint(endDir, e.endLeft, e.endTop),
                    parentId = parentId
                )
            )

            if (e.edge.label.isNotEmpty()) {
                val midLeft = (e.startLeft + e.endLeft) / 2
                val midTop = (e.startTop + e.endTop) / 2
                val w = e.edge.label.length + 2
                val labelRect = Rect.byLTWH(midLeft - w / 2, midTop, w, 1)
                val label = Text(labelRect, parentId = parentId, isTextEditable = true)
                label.setText(e.edge.label)
                shapes.add(label)
            }
        }

        return shapes
    }

    private fun direction(
        fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int
    ): DirectedPoint.Direction {
        val dx = kotlin.math.abs(toLeft - fromLeft)
        val dy = kotlin.math.abs(toTop - fromTop)
        return if (dx >= dy) DirectedPoint.Direction.HORIZONTAL else DirectedPoint.Direction.VERTICAL
    }
}
