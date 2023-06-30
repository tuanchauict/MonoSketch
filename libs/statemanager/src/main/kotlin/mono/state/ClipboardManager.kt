/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state

import mono.graphics.geo.Point
import mono.lifecycle.LifecycleOwner
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.connector.ShapeConnector
import mono.shape.shape.AbstractShape
import mono.shape.shape.Group
import mono.state.command.CommandEnvironment

/**
 * A manager class to handle clipboard related data.
 */
internal class ClipboardManager(
    lifecycleOwner: LifecycleOwner,
    private val environment: CommandEnvironment,
    private val shapeClipboardManager: ShapeClipboardManager
) {
    private var selectedShapes: Collection<AbstractShape> = emptyList()

    init {
        environment.selectedShapesLiveData.observe(lifecycleOwner) {
            selectedShapes = it
        }
        shapeClipboardManager.clipboardShapeLiveData.observe(
            lifecycleOwner,
            listener = ::pasteShapes
        )
    }

    fun copySelectedShapes(isRemoveRequired: Boolean) {
        shapeClipboardManager.setClipboard(createClipboardObject())
        if (isRemoveRequired) {
            for (shape in selectedShapes) {
                environment.removeShape(shape)
            }
            environment.clearSelectedShapes()
        }
    }

    private fun pasteShapes(clipboardObject: ShapeClipboardManager.ClipboardObject) {
        if (clipboardObject.shapes.isEmpty()) {
            return
        }
        environment.clearSelectedShapes()
        val bound = environment.getWindowBound()
        val left = bound.left + bound.width / 5
        val top = bound.top + bound.height / 5
        insertShapes(left, top, clipboardObject)
    }

    fun duplicateSelectedShapes() {
        if (selectedShapes.isEmpty()) {
            return
        }
        val currentSelectedShapes = selectedShapes
        val clipboardObject = createClipboardObject()
        val minLeft = currentSelectedShapes.minOf { it.bound.left }
        val minTop = currentSelectedShapes.minOf { it.bound.top }

        environment.clearSelectedShapes()
        insertShapes(minLeft + 1, minTop + 1, clipboardObject)
    }

    private fun insertShapes(
        left: Int,
        top: Int,
        clipboardObject: ShapeClipboardManager.ClipboardObject
    ) {
        val currentParentId = environment.workingParentGroup.id

        val srcIdToShapeMap =
            clipboardObject.shapes.associate { it.id to Group.toShape(currentParentId, it) }
        val minLeft = srcIdToShapeMap.values.minOf { it.bound.left }
        val minTop = srcIdToShapeMap.values.minOf { it.bound.top }

        val offset = Point(minLeft - left, minTop - top)
        for (shape in srcIdToShapeMap.values) {
            val shapeBound = shape.bound
            val newShapeBound = shapeBound.copy(position = shapeBound.position.minus(offset))
            shape.setBound(newShapeBound)

            environment.addShape(shape)
            environment.addSelectedShape(shape)
        }

        for (connector in clipboardObject.connectors) {
            val line = srcIdToShapeMap[connector.lineId] as? mono.shape.shape.Line ?: continue
            val target = srcIdToShapeMap[connector.targetId] ?: continue
            environment.shapeManager.shapeConnector.addConnector(line, connector.anchor, target)
        }
    }

    private fun createClipboardObject(): ShapeClipboardManager.ClipboardObject {
        val serializableShapes = selectedShapes.map { it.toSerializableShape(false) }
        val serializableConnectors = selectedShapes.flatMap { target ->
            environment.shapeManager.shapeConnector.getConnectors(target).map {
                ShapeConnector.toSerializableConnector(it, target)
            }
        }
        return ShapeClipboardManager.ClipboardObject(serializableShapes, serializableConnectors)
    }
}
