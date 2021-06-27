package mono.state

import mono.lifecycle.LifecycleOwner
import mono.shape.add
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.remove
import mono.shape.shape.AbstractShape
import mono.state.command.CommandEnvironment

/**
 * A manager class to handle clipboard related data.
 */
internal class ClipboardManager(
    lifecycleOwner: LifecycleOwner,
    private val commandEnvironment: CommandEnvironment,
    private val shapeClipboardManager: ShapeClipboardManager
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
        val serializableShapes = selectedShapes.map { it.toSerializableShape() }
        shapeClipboardManager.setClipboard(serializableShapes)
    }

    fun cutSelectedShapes() {
        copySelectedShapes()
        for (shape in selectedShapes) {
            commandEnvironment.shapeManager.remove(shape)
        }
        commandEnvironment.clearSelectedShapes()
    }

    fun duplicateSelectedShapes() {
        TODO("Implement this method")
    }
}
