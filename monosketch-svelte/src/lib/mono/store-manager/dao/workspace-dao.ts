import { Flow } from "$libs/flow";
import { StorageDocument, StoreKeys } from "$mono/store-manager";
import { WorkspaceObjectDao } from "$mono/store-manager/dao/workspace-object-dao";

/**
 * A dao for workspace
 */
export class WorkspaceDao {
    private static _instance: WorkspaceDao | null = null;

    private readonly workspaceDocument: StorageDocument;
    private objectDaos: Map<string, WorkspaceObjectDao>;

    private workspaceUpdateMutableFlow: Flow<string> = new Flow('');
    workspaceUpdateFlow: Flow<string> = this.workspaceUpdateMutableFlow.immutable();

    private constructor(workspaceDocument: StorageDocument) {
        this.workspaceDocument = workspaceDocument;
        this.objectDaos = new Map<string, WorkspaceObjectDao>();

        workspaceDocument.setObserver(StoreKeys.LAST_MODIFIED_TIME, () => {
            this.workspaceUpdateMutableFlow.value = this.lastOpenedObjectId ?? '';
        });
    }

    // Accessor property for lastOpenedObjectId
    get lastOpenedObjectId(): string | null {
        return this.workspaceDocument.get(StoreKeys.LAST_MODIFIED_PROJECT_ID);
    }

    set lastOpenedObjectId(value: string | null) {
        if (value !== null) {
            this.workspaceDocument.set(StoreKeys.LAST_MODIFIED_PROJECT_ID, value);
        }
    }

    getObject(objectId: string): WorkspaceObjectDao {
        if (!this.objectDaos.has(objectId)) {
            const objectDao = new WorkspaceObjectDao(objectId, this.workspaceDocument);
            this.objectDaos.set(objectId, objectDao);
        }
        return this.objectDaos.get(objectId)!;
    }

    removeObject(objectId: string): void {
        const objectDao = this.getObject(objectId);
        objectDao.removeSelf();
        // TODO:
        //  If the object is currently open, choose a latest opened object and make it active.
        //  If no object left, create a blank workspace.
        this.objectDaos.delete(objectId);
    }

    // Gets list of all objects in the storage, ordered by last opened time desc.
    getObjects(): WorkspaceObjectDao[] {
        return Array.from(this.workspaceDocument.getKeys(key =>
            key.startsWith(StoreKeys.WORKSPACE + StoreKeys.PATH_SEPARATOR) &&
            key.endsWith(StoreKeys.PATH_SEPARATOR + StoreKeys.OBJECT_CONTENT),
        ))
            .map(key => this.getObject(key.split(StoreKeys.PATH_SEPARATOR)[1]))
            .sort((a, b) => b.lastOpened - a.lastOpened); // Assuming lastOpened is a numeric timestamp
    }

    // Singleton instance accessor
    static get instance(): WorkspaceDao {
        if (!WorkspaceDao._instance) {
            WorkspaceDao._instance = new WorkspaceDao(StorageDocument.get(StoreKeys.WORKSPACE));
        }
        return WorkspaceDao._instance;
    }
}
