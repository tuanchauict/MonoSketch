/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.onetimeaction

import mono.bitmap.manager.MonoBitmapManager
import mono.export.ExportShapesHelper
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.serialization.SerializableGroup
import mono.shape.serialization.ShapeSerializationUtil
import mono.shape.shape.RootGroup
import mono.state.FileMediator
import mono.state.StateHistoryManager
import mono.state.command.CommandEnvironment
import mono.store.dao.workspace.WorkspaceDao

/**
 * A helper class to handle file-related one-time actions in the application.
 * This class provides methods such as creating new projects, saving and loading shapes to/from
 * files, exporting selected shapes to text format, etc.
 */
internal class FileRelatedActionsHelper(
    private val environment: CommandEnvironment,
    private val stateHistoryManager: StateHistoryManager,
    bitmapManager: MonoBitmapManager,
    shapeClipboardManager: ShapeClipboardManager,
    private val workspaceDao: WorkspaceDao = WorkspaceDao.instance
) {
    private val fileMediator: FileMediator = FileMediator()
    private val exportShapesHelper = ExportShapesHelper(
        bitmapManager::getBitmap,
        shapeClipboardManager::setClipboardText
    )

    fun newProject() {
        replaceWorkspace(RootGroup(null)) // passing null to let the ID generated automatically
    }

    fun switchProject(projectId: String) {
        val serializableRoot = workspaceDao.getObject(projectId).rootGroup ?: return
        replaceWorkspace(RootGroup(serializableRoot))
    }

    fun renameProject(newName: String) {
        val currentRootId = environment.shapeManager.root.id
        workspaceDao.getObject(currentRootId).name = newName
    }

    fun saveCurrentShapesToFile() {
        val serializableRoot = environment.shapeManager.root.toSerializableShape(true)
        fileMediator.saveFile(ShapeSerializationUtil.toJson(serializableRoot))
    }

    fun loadShapesFromFile() {
        fileMediator.openFile { jsonString ->
            val serializableRoot = ShapeSerializationUtil.fromJson(jsonString) as? SerializableGroup
            val rootGroup = serializableRoot?.let { RootGroup(it) }
            replaceWorkspace(rootGroup)
        }
    }

    fun exportSelectedShapes(isModalRequired: Boolean) {
        val selectedShapes = environment.getSelectedShapes()
        val extractableShapes = when {
            selectedShapes.isNotEmpty() ->
                environment.workingParentGroup.items.filter { it in selectedShapes }

            isModalRequired ->
                listOf(environment.workingParentGroup)

            else ->
                emptyList()
        }

        exportShapesHelper.exportText(extractableShapes, isModalRequired)
    }

    private fun replaceWorkspace(rootGroup: RootGroup?) {
        if (rootGroup != null) {
            stateHistoryManager.clear()
            environment.replaceRoot(rootGroup)
        }
    }
}
