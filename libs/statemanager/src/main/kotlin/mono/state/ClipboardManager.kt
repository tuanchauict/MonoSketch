package mono.state

import mono.graphics.geo.Point
import mono.lifecycle.LifecycleOwner
import mono.shape.add
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.remove
import mono.shape.serialization.AbstractSerializableShape
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
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
        shapeClipboardManager.clipboardShapeLiveData.observe(
            lifecycleOwner,
            listener = ::pasteShapes
        )
    }

    fun copySelectedShapes(isRemoveRequired: Boolean) {
        val serializableShapes = selectedShapes.map { it.toSerializableShape(false) }
        shapeClipboardManager.setClipboard(serializableShapes)
        if (isRemoveRequired) {
            for (shape in selectedShapes) {
                commandEnvironment.shapeManager.remove(shape)
            }
            commandEnvironment.clearSelectedShapes()
        }
    }

    private fun pasteShapes(serializableShapes: List<AbstractSerializableShape>) {
        if (serializableShapes.isEmpty()) {
            return
        }
        commandEnvironment.clearSelectedShapes()
        val bound = commandEnvironment.getWindowBound()
        val left = bound.left + bound.width / 5
        val top = bound.top + bound.height / 5
        insertShapes(left, top, serializableShapes)
    }

    fun duplicateSelectedShapes() {
        if (selectedShapes.isEmpty()) {
            return
        }
        val currentSelectedShapes = selectedShapes
        val serializableShapes = currentSelectedShapes.map { it.toSerializableShape(false) }
        val minLeft = currentSelectedShapes.minOf { it.bound.left }
        val minTop = currentSelectedShapes.minOf { it.bound.top }

        commandEnvironment.clearSelectedShapes()
        insertShapes(minLeft + 1, minTop + 1, serializableShapes)
    }

    private fun insertShapes(
        left: Int,
        top: Int,
        serializableShapes: List<AbstractSerializableShape>
    ) {
        val currentParentId = commandEnvironment.workingParentGroup.id
        val shapes = serializableShapes.map { Group.toShape(currentParentId, it) }
        val minLeft = shapes.minOf { it.bound.left }
        val minTop = shapes.minOf { it.bound.top }

        val offset = Point(minLeft - left, minTop - top)
        for (shape in shapes) {
            val shapeBound = shape.bound
            val newShapeBound = shapeBound.copy(position = shapeBound.position.minus(offset))
            shape.setBound(newShapeBound)

            commandEnvironment.shapeManager.add(shape)
            commandEnvironment.addSelectedShape(shape)
        }
    }
}
