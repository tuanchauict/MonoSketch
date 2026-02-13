package mono.mermaid

data class MermaidDiagram(
    val nodes: List<MermaidNode>,
    val edges: List<MermaidEdge>
)

data class MermaidNode(
    val id: String,
    val label: String,
    val shape: NodeShape = NodeShape.RECTANGLE
) {
    enum class NodeShape {
        RECTANGLE,
        ROUNDED_RECTANGLE,
        DIAMOND,
        CIRCLE
    }
}

data class MermaidEdge(
    val fromId: String,
    val toId: String,
    val label: String = "",
    val style: EdgeStyle = EdgeStyle.SOLID_ARROW
) {
    enum class EdgeStyle {
        SOLID_ARROW,
        SOLID_LINE,
        DOTTED_ARROW,
        THICK_ARROW
    }
}
