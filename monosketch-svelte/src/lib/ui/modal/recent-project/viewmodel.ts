import { Flow } from '../../../libs/flow';
import { type ProjectItem, sampleProjectItems } from './model';
import { UUID } from '../../../mono/uuid';
import type { ActionManager } from '$mono/action-manager/action-manager';
import { OneTimeAction } from '$mono/action-manager/one-time-actions';
import { AppContext } from '$app/app-context';

class ProjectDataViewModel {
    private _projectFlow: Flow<ProjectItem[]> = new Flow();
    projectFlow = this._projectFlow.immutable();

    openingProjectIdFlow: Flow<string> = new Flow('');
    deletingProjectIdFlow: Flow<string> = new Flow('');
    renamingProjectIdFlow: Flow<string> = new Flow('');

    constructor(private actionManager: ActionManager) {}

    setProjectList(projectList: ProjectItem[]) {
        this._projectFlow.value = projectList;
        this.openingProjectIdFlow.value = projectList[0].id;
    }

    newProject() {
        const newProject: ProjectItem = {
            id: UUID.generate(),
            name: 'New Project',
        };
        this.setProjectList([newProject, ...this._projectFlow.value!]);
        this.openProject(newProject.id);
        this.setRenamingProject(newProject.id, newProject.name);
        this.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.NewProject);
    }

    openProject(id: string) {
        this.openingProjectIdFlow.value = id;
        // Reorder the project list, move the opened project to the top
        const currentProject = this._projectFlow.value!.find((item) => item.id === id);
        if (currentProject) {
            this._projectFlow.value = [currentProject, ...this._projectFlow.value!.filter((item) => item.id !== id)];
        }
        this.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.SwitchProject(id));
    }

    confirmDeletingProject(id: string) {
        this.deletingProjectIdFlow.value = id;
    }

    deleteProject(id: string) {
        this._projectFlow.value = this._projectFlow.value!.filter((item) => item.id !== id);
        this.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.RemoveProject(id));
    }

    cancelDeletingProject() {
        this.deletingProjectIdFlow.value = '';
    }

    setRenamingProject(id: string, newName: string) {
        console.log("Project id: " + id);
        this.renamingProjectIdFlow.value = id;
        this.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.RenameCurrentProject(newName));
    }

    setProjectName(id: string, name: string) {
        this._projectFlow.value = this._projectFlow.value!.map((item) => {
            if (item.id === id) {
                return {
                    ...item,
                    name,
                };
            }
            return item;
        });
        this.actionManager.setOneTimeAction(OneTimeAction.ProjectAction.RenameCurrentProject(name));
    }

    getProject(id: string): ProjectItem | undefined {
        console.log(this._projectFlow.value);
        return this._projectFlow.value!.find((item) => item.id === id);
    }
}

const appContext = new AppContext();
export const projectDataViewModel = new ProjectDataViewModel(appContext.actionManager);
projectDataViewModel.setProjectList(sampleProjectItems);
