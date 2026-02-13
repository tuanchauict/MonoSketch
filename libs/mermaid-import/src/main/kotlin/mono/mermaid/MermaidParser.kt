package mono.mermaid

/**
 * Parses Mermaid flowchart text into a [MermaidDiagram].
 * Supports `graph` and `flowchart` declarations with TB/TD/LR/RL directions.
 */
object MermaidParser {

    fun parse(text: String): MermaidDiagram? {
        val lines = text.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("%%") }
        if (lines.isEmpty()) return null

        val first = lines.first().lowercase()
        if (!first.startsWith("graph ") && !first.startsWith("flowchart")) return null

        val nodesMap = mutableMapOf<String, MermaidNode>()
        val edges = mutableListOf<MermaidEdge>()

        for (line in lines.drop(1)) {
            if (line.startsWith("subgraph") || line == "end" ||
                line.startsWith("style") || line.startsWith("class ") ||
                line.startsWith("click ")
            ) continue
            parseLine(line, nodesMap, edges)
        }

        return MermaidDiagram(nodesMap.values.toList(), edges)
    }

    /**
     * Splits a line on connector tokens, yielding alternating [node, connector, node, ...] parts.
     * This correctly handles chains like `A --> B --> C`.
     */
    private fun parseLine(
        line: String,
        nodes: MutableMap<String, MermaidNode>,
        edges: MutableList<MermaidEdge>
    ) {
        // Split the line into tokens: alternating node-defs and connectors.
        // Connectors: -->, --->, ---|label|, -->|label|, -.->  ==>, etc.
        val parts = CONNECTOR_REGEX.split(line).map { it.trim() }.filter { it.isNotEmpty() }
        val connectors = CONNECTOR_REGEX.findAll(line).map { it.value.trim() }.toList()

        if (parts.size < 2 || connectors.isEmpty()) {
            // No edge found, try as standalone node def
            val node = parseNodeDef(line)
            if (node.id.isNotEmpty() && ' ' !in node.id) {
                if (node.id !in nodes) nodes[node.id] = node
            }
            return
        }

        // Process each pair
        for (i in connectors.indices) {
            if (i + 1 >= parts.size) break
            val from = parseNodeDef(parts[i])
            val to = parseNodeDef(parts[i + 1])
            if (from.id !in nodes) nodes[from.id] = from
            if (to.id !in nodes) nodes[to.id] = to

            val conn = connectors[i]
            edges.add(MermaidEdge(from.id, to.id, extractLabel(conn), edgeStyle(conn)))
        }
    }

    private fun parseNodeDef(raw: String): MermaidNode {
        val text = raw.trim()
        for ((regex, shape) in NODE_SHAPES) {
            val m = regex.find(text)
            if (m != null) return MermaidNode(m.groupValues[1].trim(), m.groupValues[2].trim(), shape)
        }
        val id = text.split(Regex("[\\s;]")).first()
        return MermaidNode(id, id)
    }

    private fun extractLabel(connector: String): String {
        val m = Regex("\\|([^|]*)\\|").find(connector)
        return m?.groupValues?.get(1).orEmpty()
    }

    private fun edgeStyle(connector: String): MermaidEdge.EdgeStyle {
        val c = connector.replace(Regex("\\|[^|]*\\|"), "").trim()
        return when {
            "==>" in c -> MermaidEdge.EdgeStyle.THICK_ARROW
            "-.->" in c -> MermaidEdge.EdgeStyle.DOTTED_ARROW
            "-->" in c -> MermaidEdge.EdgeStyle.SOLID_ARROW
            "---" in c -> MermaidEdge.EdgeStyle.SOLID_LINE
            else -> MermaidEdge.EdgeStyle.SOLID_ARROW
        }
    }

    // Matches connectors like -->, ---|text|, -->|text|, -.->  ==>, --- etc.
    // Order matters: longer patterns first to avoid partial matches.
    private val CONNECTOR_REGEX = Regex(
        """\s+(==>|-.->|-\.->|--+>?\|[^|]*\|\s*|--+>|--+-)\s+"""
    )

    private val NODE_SHAPES = listOf(
        Regex("^(\\S+)\\(\\(([^)]+)\\)\\)$") to MermaidNode.NodeShape.CIRCLE,
        Regex("^(\\S+)\\{([^}]+)\\}$") to MermaidNode.NodeShape.DIAMOND,
        Regex("^(\\S+)\\(([^)]+)\\)$") to MermaidNode.NodeShape.ROUNDED_RECTANGLE,
        Regex("^(\\S+)\\[([^\\]]+)\\]$") to MermaidNode.NodeShape.RECTANGLE
    )
}
