package mono.html.canvas.canvas

import mono.shapebound.InteractionBound
import org.w3c.dom.HTMLCanvasElement

/**
 * A canvas view controller to render the interaction indicators.
 */
internal class InteractionCanvasViewController(
    canvas: HTMLCanvasElement
) : BaseCanvasViewController(canvas) {

    var interactionBounds: List<InteractionBound> = emptyList()

    override fun drawInternal() {
        for (bound in interactionBounds) {
            // TODO: draw
        }
    }
}
