/*
 * Copyright (c) 2024, tuanchauict
 */

import type { ProjectActionType } from "$mono/action-manager/one-time-actions";
import type { MonoBitmapManager } from "$mono/monobitmap/manager/mono-bitmap-manager";
import { ShapeConnector } from "$mono/shape/connector/shape-connector";
import type { ShapeClipboardManager } from "$mono/shape/shape-clipboard-manager";
import { Group, RootGroup } from "$mono/shape/shape/group";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { FileMediator } from "$mono/state-manager/onetimeaction/file-mediator";
import type { WorkspaceDao } from "$mono/store-manager/dao/workspace-dao";

export class FileRelatedActionsHelper {
    private fileMediator: FileMediator = new FileMediator();
    // private exportShapesHelper: ExportShapesHelper;

    constructor(
        private environment: CommandEnvironment,
        bitmapManager: MonoBitmapManager,
        shapeClipboardManager: ShapeClipboardManager,
        private workspaceDao: WorkspaceDao
    ) {
        // this.exportShapesHelper = new ExportShapesHelper(
        //     bitmapManager.getBitmap.bind(bitmapManager),
        //     shapeClipboardManager.setClipboardText.bind(shapeClipboardManager)
        // );
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
        const currentRootId = this.environment.shapeManager.root.id;
        this.workspaceDao.getObject(currentRootId).name = newName;
        this.environment.shapeManager.notifyProjectUpdate();
    }

    private saveCurrentShapesToFile() {
        // const currentRoot = this.environment.shapeManager.root;
        // const objectDao = this.workspaceDao.getObject(currentRoot.id);
        // const name = objectDao.name;
        // const offset = objectDao.offset;
        // const jsonString = ShapeSerializationUtil.toMonoFileJson({
        //     name,
        //     serializableShape: currentRoot.toSerializableShape(true),
        //     connectors: this.environment.shapeManager.shapeConnector.toSerializable(),
        //     offset
        // });
        // this.fileMediator.saveFile(name, jsonString);
    }

    private loadShapesFromFile() {
        // this.fileMediator.openFile(jsonString => {
        //     const monoFile = ShapeSerializationUtil.fromMonoFileJson(jsonString);
        //     if (monoFile) {
        //         this.applyMonoFileToWorkspace(monoFile);
        //     } else {
        //         console.warn("Failed to load shapes from file.");
        //         // TODO: Show error dialog
        //     }
        // });
    }

    // private applyMonoFileToWorkspace(monoFile: MonoFile) {
    //     const rootGroup = RootGroup(monoFile.root);
    //     const existingProject = this.workspaceDao.getObject(rootGroup.id);
    //     if (existingProject.rootGroup) {
    //         showExitingProjectDialog(
    //             existingProject.name,
    //             existingProject.lastModifiedTimestampMillis,
    //             () => {
    //                 this.prepareAndApplyNewRoot(RootGroup(monoFile.root.copy({ isIdTemporary: true })), monoFile.extra);
    //             },
    //             () => {
    //                 this.prepareAndApplyNewRoot(rootGroup, monoFile.extra);
    //             }
    //         );
    //     } else {
    //         this.prepareAndApplyNewRoot(rootGroup, monoFile.extra);
    //     }
    // }

    // private prepareAndApplyNewRoot(rootGroup: Group, extra: Extra) {
    //     const objectDao = this.workspaceDao.getObject(rootGroup.id);
    //     objectDao.name = extra.name || WorkspaceObjectDao.DEFAULT_NAME;
    //     objectDao.offset = extra.offset;
    //     this.replaceWorkspace(rootGroup);
    // }

    exportSelectedShapes(isModalRequired: boolean) {
        // const selectedShapes = this.environment.getSelectedShapes();
        // const extractableShapes = selectedShapes.size > 0
        //     ? this.environment.workingParentGroup.items.filter(item => selectedShapes.includes(item))
        //     : isModalRequired
        //         ? [this.environment.workingParentGroup]
        //         : [];
        // this.exportShapesHelper.exportText(extractableShapes, isModalRequired);
    }

    private replaceWorkspace(rootGroup: Group) {
        // TODO: load from storage
        const shapeConnector = new ShapeConnector();
        this.environment.replaceRoot(rootGroup, shapeConnector);
    }
}
