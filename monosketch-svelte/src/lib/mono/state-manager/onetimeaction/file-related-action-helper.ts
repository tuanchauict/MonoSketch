/*
 * Copyright (c) 2024, tuanchauict
 */

import type { ProjectActionType } from "$mono/action-manager/one-time-actions";
import { Extra, MonoFile } from "$mono/file/mono-file";
import { ShapeSerializationUtil } from "$mono/file/shape-serialization-util";
import type { MonoBitmapManager } from "$mono/monobitmap/manager/mono-bitmap-manager";
import { ShapeConnector } from "$mono/shape/connector/shape-connector";
import type { ShapeClipboardManager } from "$mono/shape/shape-clipboard-manager";
import { Group, RootGroup } from "$mono/shape/shape/group";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { ExportShapesHelper } from "$mono/state-manager/export/export-shapes-helper";
import { FileMediator } from "$mono/state-manager/onetimeaction/file-mediator";
import type { WorkspaceDao } from "$mono/store-manager/dao/workspace-dao";
import { DEFAULT_NAME } from "$mono/store-manager/dao/workspace-object-dao";
import { modalViewModel } from "$ui/modal/viewmodel";

/**
 * A helper class to handle file-related one-time actions in the application.
 * This class provides methods such as creating new projects, saving and loading shapes to/from
 * files, exporting selected shapes to text format, etc.
 */
export class FileRelatedActionsHelper {
    private readonly fileMediator: FileMediator = new FileMediator();

    private exportShapesHelper: ExportShapesHelper;

    constructor(
        private environment: CommandEnvironment,
        bitmapManager: MonoBitmapManager,
        shapeClipboardManager: ShapeClipboardManager,
        private workspaceDao: WorkspaceDao,
    ) {
        this.exportShapesHelper = new ExportShapesHelper(
            (shape) => bitmapManager.getBitmap(shape),
            shapeClipboardManager.setClipboardText.bind(shapeClipboardManager)
        );
    }

    handleProjectAction(projectAction: ProjectActionType) {
        switch (projectAction.type) {
            case "NewProject":
                this.newProject();
                break;
            case "SwitchProject":
                this.switchProject(projectAction.projectId);
                break;
            case "RemoveProject":
                this.removeProject(projectAction.projectId);
                break;
            case "RenameCurrentProject":
                this.renameProject(projectAction.newName);
                break;
            case "SaveShapesAs":
                this.saveCurrentShapesToFile();
                break;
            case "OpenShapes":
                this.loadShapesFromFile();
                break;
            case "ExportSelectedShapes":
                this.exportSelectedShapes(true);
                break;
        }
    }

    private newProject() {
        this.replaceWorkspace(RootGroup(null));
    }

    private switchProject(projectId: string) {
        const serializableRoot = this.workspaceDao.getObject(projectId).rootGroup;
        if (serializableRoot) {
            this.replaceWorkspace(RootGroup(serializableRoot));
        }
    }

    private removeProject(projectId: string) {
        const currentProjectId = this.environment.shapeManager.root.id;
        this.workspaceDao.getObject(projectId).removeSelf();
        if (projectId !== currentProjectId) {
            return;
        }
        const nextProject = this.workspaceDao.getObjects().find(obj => obj.objectId !== currentProjectId);
        if (nextProject) {
            this.switchProject(nextProject.objectId);
        } else {
            this.newProject();
        }
    }

    private renameProject(newName: string) {
        this.workspaceDao.getObject(this.environment.shapeManager.root.id).name = newName;
        this.environment.shapeManager.notifyProjectUpdate();
    }

    private saveCurrentShapesToFile() {
        const currentRoot = this.environment.shapeManager.root;
        const objectDao = this.workspaceDao.getObject(currentRoot.id);
        const name = objectDao.name;
        const offset = objectDao.offset;
        const monoFile = MonoFile.create(
            currentRoot.toSerializableShape(true),
            this.environment.shapeManager.shapeConnector.toSerializable(),
            Extra.create(name, offset),
        );
        const jsonString = ShapeSerializationUtil.toMonoFileJson(monoFile);
        this.fileMediator.saveFile(name, jsonString);
    }

    private loadShapesFromFile() {
        this.fileMediator.openFile(jsonString => {
            const monoFile = ShapeSerializationUtil.fromMonoFileJson(jsonString);
            if (monoFile) {
                this.applyMonoFileToWorkspace(monoFile);
            } else {
                console.warn("Failed to load shapes from file.");
                // TODO: Show error dialog
            }
        });
    }

    private applyMonoFileToWorkspace(monoFile: MonoFile) {
        const rootGroup = RootGroup(monoFile.root);
        const existingProject = this.workspaceDao.getObject(rootGroup.id);
        if (existingProject.rootGroup) {
            modalViewModel.existingProjectFlow.value = {
                projectName: existingProject.name,
                lastEditedTimeMillis: existingProject.lastModifiedTimestampMillis,
                onReplace: () => {
                    this.prepareAndApplyNewRoot(RootGroup(monoFile.root.copy({ isIdTemporary: true })), monoFile.extra);
                },
                onKeepBoth: () => {
                    this.prepareAndApplyNewRoot(rootGroup, monoFile.extra);
                }
            };
        } else {
            this.prepareAndApplyNewRoot(rootGroup, monoFile.extra);
        }
    }

    private prepareAndApplyNewRoot(rootGroup: Group, extra: Extra) {
        const objectDao = this.workspaceDao.getObject(rootGroup.id);
        objectDao.name = extra.name || DEFAULT_NAME;
        objectDao.offset = extra.offset;
        this.replaceWorkspace(rootGroup);
    }

    exportSelectedShapes(isModalRequired: boolean) {
        this.exportShapesHelper.exportText(this.createExtractableShapes(isModalRequired), isModalRequired);
    }

    private createExtractableShapes(isModalRequired: boolean) {
        const selectedShapes = this.environment.getSelectedShapes();
        if (selectedShapes.size === 0) {
            return isModalRequired ? [this.environment.workingParentGroup] : [];
        }
        return Array.from(this.environment.workingParentGroup.items).filter(item => selectedShapes.has(item));
    }

    private replaceWorkspace(rootGroup: Group) {
        // TODO: load from storage
        const shapeConnector = new ShapeConnector();
        this.environment.replaceRoot(rootGroup, shapeConnector);
    }
}
