package mono.mermaid

import kotlin.math.max

/**
 * Computes positions for nodes using a simple layered (top-down) graph layout.
 * Assigns layers via topological sort, then spaces nodes within each layer.
 */
object MermaidLayoutEngine {

    private const val NODE_PAD_H = 2
    private const val MIN_WIDTH = 10
    private const val MIN_HEIGHT = 3
    private const val H_GAP = 6
    private const val V_GAP = 4

    data class PositionedNode(
        val node: MermaidNode,
        val left: Int,
        val top: Int,
        val width: Int,
        val height: Int
    ) {
        val centerLeft: Int get() = left + width / 2
        val centerTop: Int get() = top + height / 2
        val right: Int get() = left + width - 1
        val bottom: Int get() = top + height - 1
    }

    data class PositionedEdge(
        val edge: MermaidEdge,
        val startLeft: Int,
        val startTop: Int,
        val endLeft: Int,
        val endTop: Int
    )

    data class LayoutResult(
        val nodes: List<PositionedNode>,
        val edges: List<PositionedEdge>
    )

    fun layout(diagram: MermaidDiagram): LayoutResult {
        if (diagram.nodes.isEmpty()) return LayoutResult(emptyList(), emptyList())

        val adjacency = mutableMapOf<String, MutableList<String>>()
        val inDegree = mutableMapOf<String, Int>()
        for (node in diagram.nodes) {
            adjacency[node.id] = mutableListOf()
            inDegree[node.id] = 0
        }
        for (edge in diagram.edges) {
            if (edge.fromId in adjacency && edge.toId in adjacency) {
                adjacency[edge.fromId]!!.add(edge.toId)
                inDegree[edge.toId] = (inDegree[edge.toId] ?: 0) + 1
            }
        }

        // Topological sort into layers (Kahn's algorithm)
        val nodeById = diagram.nodes.associateBy { it.id }
        val layers = mutableListOf<List<MermaidNode>>()
        val assigned = mutableSetOf<String>()
        val remaining = inDegree.toMutableMap()

        while (assigned.size < diagram.nodes.size) {
            val sources = remaining.filter { it.value == 0 && it.key !in assigned }.keys.toList()
            if (sources.isEmpty()) {
                // Cycle: dump remaining nodes into one layer
                layers.add(diagram.nodes.filter { it.id !in assigned })
                break
            }
            layers.add(sources.mapNotNull { nodeById[it] })
            for (id in sources) {
                assigned.add(id)
                for (neighbor in adjacency[id].orEmpty()) {
                    remaining[neighbor] = (remaining[neighbor] ?: 1) - 1
                }
            }
        }

        // Build reverse adjacency (child -> list of parent IDs)
        val parents = mutableMapOf<String, MutableList<String>>()
        for (node in diagram.nodes) parents[node.id] = mutableListOf()
        for (edge in diagram.edges) {
            if (edge.toId in parents && edge.fromId in adjacency) {
                parents[edge.toId]!!.add(edge.fromId)
            }
        }

        // Position nodes layer by layer.
        // For nodes with already-positioned parents, center under the average
        // parent center. Otherwise pack left-to-right.
        val nodeSizes = diagram.nodes.associate { it.id to nodeSize(it) }
        val positioned = mutableListOf<PositionedNode>()
        val posMap = mutableMapOf<String, PositionedNode>()
        var currentTop = 0

        for (layer in layers) {
            val layerHeight = layer.maxOf { nodeSizes[it.id]!!.second }

            // First pass: compute ideal center for each node based on parents
            val idealCenters = layer.map { node ->
                val parentPositions = parents[node.id].orEmpty().mapNotNull { posMap[it]?.centerLeft }
                if (parentPositions.isNotEmpty()) {
                    parentPositions.average().toInt()
                } else {
                    -1 // no preference, will be packed
                }
            }

            // Second pass: place nodes avoiding overlaps
            val placements = mutableListOf<Pair<MermaidNode, Int>>() // node to left
            for ((i, node) in layer.withIndex()) {
                val (w, _) = nodeSizes[node.id]!!
                val idealCenter = idealCenters[i]
                val idealLeft = if (idealCenter >= 0) {
                    max(idealCenter - w / 2, 0)
                } else {
                    0
                }

                // Find the leftmost position >= idealLeft that doesn't overlap previous nodes
                val minLeft = if (placements.isEmpty()) {
                    idealLeft
                } else {
                    val prevNode = placements.last().first
                    val prevLeft = placements.last().second
                    val prevWidth = nodeSizes[prevNode.id]!!.first
                    val afterPrev = prevLeft + prevWidth + H_GAP
                    max(idealLeft, afterPrev)
                }

                placements.add(node to minLeft)
            }

            for ((node, left) in placements) {
                val (w, h) = nodeSizes[node.id]!!
                val p = PositionedNode(node, left, currentTop, w, h)
                positioned.add(p)
                posMap[node.id] = p
            }

            currentTop += layerHeight + V_GAP
        }

        // Connect edges
        val posEdges = diagram.edges.mapNotNull { edge ->
            val from = posMap[edge.fromId] ?: return@mapNotNull null
            val to = posMap[edge.toId] ?: return@mapNotNull null
            if (from.bottom < to.top) {
                PositionedEdge(edge, from.centerLeft, from.bottom, to.centerLeft, to.top)
            } else if (from.right < to.left) {
                PositionedEdge(edge, from.right, from.centerTop, to.left, to.centerTop)
            } else {
                PositionedEdge(edge, from.left, from.centerTop, to.right, to.centerTop)
            }
        }

        return LayoutResult(positioned, posEdges)
    }

    private fun nodeSize(node: MermaidNode): Pair<Int, Int> {
        val w = max(node.label.length + NODE_PAD_H * 2 + 2, MIN_WIDTH)
        return Pair(w, MIN_HEIGHT)
    }
}
