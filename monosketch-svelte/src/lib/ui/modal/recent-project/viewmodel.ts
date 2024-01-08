import { Flow } from '../../../mono/flow';
import { type FileItem, sampleFileItems } from './model';
import { UUID } from '../../../mono/uuid';

class ProjectDataViewModel {
    private _projectFlow: Flow<FileItem[]> = new Flow();
    projectFlow = this._projectFlow.immutable();

    openingProjectIdFlow: Flow<string> = new Flow();
    deletingProjectIdFlow: Flow<string> = new Flow();
    renamingProjectIdFlow: Flow<string> = new Flow();

    setProjectList(projectList: FileItem[]) {
        this._projectFlow.value = projectList;
        this.openingProjectIdFlow.value = projectList[0].id;
    }

    newProject() {
        const newFile: FileItem = {
            id: UUID.generate(),
            name: 'New Project',
        }
        this.setProjectList([newFile, ...this._projectFlow.value!!]);
        this.openProject(newFile.id);
        this.setRenamingProject(newFile.id);
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

    setRenamingProject(id: string) {
        this.renamingProjectIdFlow.value = id;
    }

    setProjectName(id: string, name: string) {
        this._projectFlow.value = this._projectFlow.value!!.map((item) => {
            if (item.id === id) {
                return {
                    ...item,
                    name,
                };
            }
            return item;
        });
    }

    getProject(id: string): FileItem | undefined {
        return this._projectFlow.value!!.find((item) => item.id === id);
    }
}

export const projectDataViewModel = new ProjectDataViewModel();
projectDataViewModel.setProjectList(sampleFileItems);
