import { Flow } from '../../../mono/flow';
import { type FileItem, sampleFileItems } from './model';

class ProjectDataViewModel {
    private _projectFlow: Flow<FileItem[]> = new Flow();
    projectFlow = this._projectFlow.immutable();

    openingProjectIdFlow: Flow<string> = new Flow();
    deletingProjectIdFlow: Flow<string> = new Flow();

    setProjectList(projectList: FileItem[]) {
        this._projectFlow.value = projectList;
    }

    openProject(id: string) {
        this.openingProjectIdFlow.value = id;
    }

    confirmDeletingProject(id: string) {
        this.deletingProjectIdFlow.value = id;
    }

    deleteProject(id: string) {
        this._projectFlow.value = this._projectFlow.value!!.filter((item) => item.id !== id);
    }

    cancelDeletingProject() {
        this.deletingProjectIdFlow.value = '';
    }
}

export const projectDataViewModel = new ProjectDataViewModel();
projectDataViewModel.setProjectList(sampleFileItems);
