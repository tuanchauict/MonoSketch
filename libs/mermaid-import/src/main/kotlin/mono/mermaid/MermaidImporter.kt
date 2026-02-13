package mono.mermaid

import mono.shape.shape.AbstractShape

/**
 * Entry point: parses Mermaid text, computes layout, and returns MonoSketch shapes.
 */
object MermaidImporter {

    fun import(mermaidText: String, parentId: String): List<AbstractShape>? {
        val diagram = MermaidParser.parse(mermaidText) ?: return null
        if (diagram.nodes.isEmpty()) return null
        val layout = MermaidLayoutEngine.layout(diagram)
        return MermaidShapeGenerator.generate(layout, parentId)
    }
}
