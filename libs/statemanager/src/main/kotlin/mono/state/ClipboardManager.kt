package mono.state

import mono.lifecycle.LifecycleOwner
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.shape.AbstractShape
import mono.state.command.CommandEnvironment

/**
 * A manager class to handle clipboard related data.
 */
internal class ClipboardManager(
    lifecycleOwner: LifecycleOwner,
    commandEnvironment: CommandEnvironment,
    shapeClipboardManager: ShapeClipboardManager
) {
    private var selectedShapes: Collection<AbstractShape> = emptyList()

    init {
        commandEnvironment.selectedShapesLiveData.observe(lifecycleOwner) {
            selectedShapes = it
        }
        shapeClipboardManager.clipboardShapeLiveData.observe(lifecycleOwner) {
            TODO("Create shapes with abstract shapes")
        }
    }

    fun copySelectedShapes() {
        TODO("Implement this method")
    }

    fun cutSelectedShapes() {
        TODO("Implement this method")
    }

    fun duplicateSelectedShapes() {
        TODO("Implement this method")
    }
}
