package mono.state.command

import mono.graphics.geo.MousePointer
import mono.graphics.geo.Rect

/**
 * A [MouseCommand] to select shapes.
 */
internal class SelectShapeMouseCommand : MouseCommand {
    private var selectionBound: Rect? = null
    override fun execute(environment: CommandEnvironment, mousePointer: MousePointer): Boolean {
        console.log(mousePointer)
       return when (mousePointer) {
            is MousePointer.Down -> false
            is MousePointer.Move -> false
            is MousePointer.Up -> false
            is MousePointer.Click -> {
                val shapes = environment.shapeSearcher.getShapes(mousePointer.point)
                console.log(shapes)
                if (shapes.isNotEmpty()) {
                    val shape = shapes.last()
                    if (mousePointer.isWithShiftKey) {
                        environment.selectedShapeManager.toggleSelection(shape)
                    } else {
                        environment.selectedShapeManager.set(shape)
                    }
                }
                true
            }
            MousePointer.Idle -> true
        }
    }
}
