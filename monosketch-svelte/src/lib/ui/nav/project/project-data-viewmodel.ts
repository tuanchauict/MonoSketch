/*
 * Copyright (c) 2025, tuanchauict
 */

import type { AppContext } from "$app/app-context";
import { Flow } from '$libs/flow';
import { type ProjectItem } from 'lib/ui/nav/project/model';
import { UUID } from '$mono/uuid';

export class ProjectDataViewModel {
    private _projectFlow: Flow<ProjectItem[]> = new Flow();
    projectFlow = this._projectFlow.immutable();

    renamingProjectIdFlow: Flow<string> = new Flow('');
    openingProjectFlow: Flow<ProjectItem>;
    deletingProjectIdFlow: Flow<string> = new Flow('');


    constructor(private appContext: AppContext) {
        this.updateProjectList();
        this.openingProjectFlow = Flow.combine2(
            appContext.shapeManager.rootIdFlow,
            this.renamingProjectIdFlow,
            (id) => this.getProject(id),
        );
    }

    updateProjectList() {
        this._projectFlow.value = this.appContext.workspaceDao.getObjects().map((dao) => {
            return {
                id: dao.objectId,
                name: dao.name,
            };
        });
    }

    newProject() {
        const newProject: ProjectItem = {
            id: UUID.generate(),
            name: 'New Project',
        };
        this.updateProjectList();
        this.openProject(newProject.id);
        this.setRenamingProject(newProject.id);
    }

    openProject(id: string) {
        // TODO: notify changing project with id
        console.log('Open project with id:', id);
    }

    confirmDeletingProject(id: string) {
        this.deletingProjectIdFlow.value = id;
    }

    deleteProject(id: string) {
        this._projectFlow.value = this._projectFlow.value!.filter((item) => item.id !== id);
    }

    cancelDeletingProject() {
        this.deletingProjectIdFlow.value = '';
    }

    setRenamingProject(id: string) {
        this.renamingProjectIdFlow.value = id;
    }

    setProjectName(id: string, name: string) {
        this.appContext.workspaceDao.getObject(id).name = name;
    }

    getProject(id: string): ProjectItem {
        const objectDao = this.appContext.workspaceDao.getObject(id);
        return {
            id,
            name: objectDao.name,
        };
    }
}
