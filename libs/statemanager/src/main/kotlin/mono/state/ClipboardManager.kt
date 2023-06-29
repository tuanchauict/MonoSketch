/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state

import mono.graphics.geo.Point
import mono.lifecycle.LifecycleOwner
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.connector.ShapeConnector
import mono.shape.serialization.AbstractSerializableShape
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
        val serializableShapes = selectedShapes.map { it.toSerializableShape(false) }
        val serializableConnectors = selectedShapes.flatMap { target ->
            environment.shapeManager.shapeConnector.getConnectors(target).map {
                ShapeConnector.toSerializableConnector(it, target)
            }
        }

        shapeClipboardManager.setClipboard(serializableShapes, serializableConnectors)
        if (isRemoveRequired) {
            for (shape in selectedShapes) {
                environment.removeShape(shape)
            }
            environment.clearSelectedShapes()
        }
    }

    private fun pasteShapes(clipboardObject: ShapeClipboardManager.ClipboardObject) {
        val serializableShapes = clipboardObject.shapes
        if (serializableShapes.isEmpty()) {
            return
        }
        environment.clearSelectedShapes()
        val bound = environment.getWindowBound()
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

        environment.clearSelectedShapes()
        insertShapes(minLeft + 1, minTop + 1, serializableShapes)
    }

    private fun insertShapes(
        left: Int,
        top: Int,
        serializableShapes: List<AbstractSerializableShape>
    ) {
        val currentParentId = environment.workingParentGroup.id
        val shapes = serializableShapes.map { Group.toShape(currentParentId, it) }
        val minLeft = shapes.minOf { it.bound.left }
        val minTop = shapes.minOf { it.bound.top }

        val offset = Point(minLeft - left, minTop - top)
        for (shape in shapes) {
            val shapeBound = shape.bound
            val newShapeBound = shapeBound.copy(position = shapeBound.position.minus(offset))
            shape.setBound(newShapeBound)

            environment.addShape(shape)
            environment.addSelectedShape(shape)
        }
    }
}
