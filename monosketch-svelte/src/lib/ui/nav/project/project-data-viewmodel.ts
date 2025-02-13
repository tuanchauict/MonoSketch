/*
 * Copyright (c) 2025, tuanchauict
 */

import type { AppContext } from "$app/app-context";
import { Flow, LifecycleOwner } from '$libs/flow';
import { OneTimeAction } from "$mono/action-manager/one-time-actions";
import { type ProjectItem } from 'lib/ui/nav/project/model';

export class ProjectDataViewModel {
    private _projectFlow: Flow<ProjectItem[]> = new Flow();
    projectFlow = this._projectFlow.immutable();

    renamingProjectIdFlow: Flow<string> = new Flow('');
    openingProjectFlow: Flow<ProjectItem>;
    deletingProjectIdFlow: Flow<string> = new Flow('');

    lifecycleOwner: LifecycleOwner = new LifecycleOwner();

    constructor(private appContext: AppContext) {
        this.updateProjectList();
        this.openingProjectFlow = Flow.combine2(
            appContext.shapeManager.rootIdFlow,
            appContext.workspaceDao.workspaceUpdateFlow,
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
        this.appContext.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.NewProject);

        // Delay for a short time to ensure the new project is created before renaming.
        // TODO: Find a better way to handle this.
        setTimeout(() => {
            this.setRenamingProject(this.appContext.shapeManager.rootIdFlow.value || '');
        }, 100);
    }

    openProject(id: string) {
        this.appContext.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.SwitchProject(id));
    }

    confirmDeletingProject(id: string) {
        this.deletingProjectIdFlow.value = id;
    }

    deleteProject(id: string) {
        this.appContext.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.RemoveProject(id));
        this.updateProjectList();
    }

    cancelDeletingProject() {
        this.deletingProjectIdFlow.value = '';
    }

    setRenamingProject(id: string) {
        this.renamingProjectIdFlow.value = id;
    }

    setCurrentProjectName(name: string) {
        this.appContext.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.RenameCurrentProject(name));
    }

    private getProject(id: string): ProjectItem {
        const objectDao = this.appContext.workspaceDao.getObject(id);
        return {
            id,
            name: objectDao.name,
        };
    }
}
