/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.onetimeaction

import mono.actionmanager.OneTimeActionType
import mono.bitmap.manager.MonoBitmapManager
import mono.export.ExportShapesHelper
import mono.html.modal.compose.showExitingProjectDialog
import mono.shape.clipboard.ShapeClipboardManager
import mono.shape.connector.ShapeConnector
import mono.shape.serialization.Extra
import mono.shape.serialization.MonoFile
import mono.shape.serialization.ShapeSerializationUtil
import mono.shape.shape.RootGroup
import mono.state.FileMediator
import mono.state.command.CommandEnvironment
import mono.store.dao.workspace.WorkspaceDao
import mono.store.dao.workspace.WorkspaceObjectDao

/**
 * A helper class to handle file-related one-time actions in the application.
 * This class provides methods such as creating new projects, saving and loading shapes to/from
 * files, exporting selected shapes to text format, etc.
 */
internal class FileRelatedActionsHelper(
    private val environment: CommandEnvironment,
    bitmapManager: MonoBitmapManager,
    shapeClipboardManager: ShapeClipboardManager,
    private val workspaceDao: WorkspaceDao = WorkspaceDao.instance
) {
    private val fileMediator: FileMediator = FileMediator()
    private val exportShapesHelper = ExportShapesHelper(
        bitmapManager::getBitmap,
        shapeClipboardManager::setClipboardText
    )

    fun handleProjectAction(projectAction: OneTimeActionType.ProjectAction) {
        when (projectAction) {
            OneTimeActionType.ProjectAction.NewProject ->
                newProject()

            is OneTimeActionType.ProjectAction.SwitchProject ->
                switchProject(projectAction.projectId)

            is OneTimeActionType.ProjectAction.RemoveProject ->
                removeProject(projectAction.projectId)

            is OneTimeActionType.ProjectAction.RenameCurrentProject ->
                renameProject(projectAction.newName)

            OneTimeActionType.ProjectAction.SaveShapesAs ->
                saveCurrentShapesToFile()

            OneTimeActionType.ProjectAction.OpenShapes ->
                loadShapesFromFile()

            OneTimeActionType.ProjectAction.ExportSelectedShapes ->
                exportSelectedShapes(true)
        }
    }

    private fun newProject() {
        replaceWorkspace(RootGroup(null)) // passing null to let the ID generated automatically
    }

    private fun switchProject(projectId: String) {
        val serializableRoot = workspaceDao.getObject(projectId).rootGroup ?: return
        replaceWorkspace(RootGroup(serializableRoot))
    }

    private fun removeProject(projectId: String) {
        val currentProjectId = environment.shapeManager.root.id
        workspaceDao.getObject(projectId).removeSelf()
        if (projectId != currentProjectId) {
            return
        }
        // Next active project selection when the current active project is removed.
        val nextProject =
            workspaceDao.getObjects().filter { it.objectId != currentProjectId }.firstOrNull()
        if (nextProject == null) {
            newProject()
        } else {
            switchProject(nextProject.objectId)
        }
    }

    private fun renameProject(newName: String) {
        val currentRootId = environment.shapeManager.root.id
        workspaceDao.getObject(currentRootId).name = newName
        environment.shapeManager.notifyProjectUpdate()
    }

    private fun saveCurrentShapesToFile() {
        val currentRoot = environment.shapeManager.root
        val objectDao = workspaceDao.getObject(currentRoot.id)
        val name = objectDao.name
        val offset = objectDao.offset
        val jsonString = ShapeSerializationUtil.toMonoFileJson(
            name = name,
            serializableShape = currentRoot.toSerializableShape(true),
            connectors = environment.shapeManager.shapeConnector.toSerializable(),
            offset = offset
        )
        fileMediator.saveFile(name, jsonString)
    }

    private fun loadShapesFromFile() {
        fileMediator.openFile { jsonString ->
            val monoFile = ShapeSerializationUtil.fromMonoFileJson(jsonString)
            if (monoFile == null) {
                console.warn("Failed to load shapes from file.")
                // TODO: Show error dialog
                return@openFile
            }
            applyMonoFileToWorkspace(monoFile)
        }
    }

    private fun applyMonoFileToWorkspace(monoFile: MonoFile) {
        val rootGroup = RootGroup(monoFile.root)
        val existingProject = workspaceDao.getObject(rootGroup.id)
        if (existingProject.rootGroup != null) {
            showExitingProjectDialog(
                existingProject.name,
                existingProject.lastModifiedTimestampMillis,
                onKeepBothClick = {
                    prepareAndApplyNewRoot(
                        RootGroup(monoFile.root.copy(isIdTemporary = true)),
                        monoFile.extra
                    )
                },
                onReplaceClick = {
                    prepareAndApplyNewRoot(rootGroup, monoFile.extra)
                }
            )
            return
        }

        prepareAndApplyNewRoot(rootGroup, monoFile.extra)
    }

    private fun prepareAndApplyNewRoot(rootGroup: RootGroup, extra: Extra) {
        // Prepare the object to be replaced since the data on the UI rely on the current root
        // id to know an update.
        // - Set name to the storage
        // - Set offset to the storage
        workspaceDao.getObject(rootGroup.id).run {
            name = extra.name.takeIf { it.isNotEmpty() } ?: WorkspaceObjectDao.DEFAULT_NAME
            offset = extra.offset
        }

        replaceWorkspace(rootGroup)
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

    private fun replaceWorkspace(rootGroup: RootGroup) {
        // TODO: load from storage
        val shapeConnector = ShapeConnector()
        environment.replaceRoot(rootGroup, shapeConnector)
    }
}
