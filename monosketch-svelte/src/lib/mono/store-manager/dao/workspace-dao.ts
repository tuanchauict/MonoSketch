import { StorageDocument } from "$mono/store-manager";
import { WorkspaceObjectDao } from "$mono/store-manager/dao/workspace-object-dao";

/**
 * A dao for workspace
 */
export class WorkspaceDao {
    private static _instance: WorkspaceDao | null = null;

    private readonly workspaceDocument: StorageDocument;
    private objectDaos: Map<string, WorkspaceObjectDao>;

    private constructor(workspaceDocument: StorageDocument) {
        this.workspaceDocument = workspaceDocument;
        this.objectDaos = new Map<string, WorkspaceObjectDao>();
    }

    // Accessor property for lastOpenedObjectId
    get lastOpenedObjectId(): string | null {
        return this.workspaceDocument.get(WorkspaceDao.LAST_OPEN);
    }

    set lastOpenedObjectId(value: string | null) {
        if (value !== null) {
            this.workspaceDocument.set(WorkspaceDao.LAST_OPEN, value);
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
            key.startsWith(WorkspaceDao.WORKSPACE + WorkspaceDao.PATH_SEPARATOR) &&
            key.endsWith(WorkspaceDao.PATH_SEPARATOR + WorkspaceDao.OBJECT_CONTENT)
        ))
            .map(key => this.getObject(key.split(WorkspaceDao.PATH_SEPARATOR)[1]))
            .sort((a, b) => b.lastOpened - a.lastOpened); // Assuming lastOpened is a numeric timestamp
    }

    // Singleton instance accessor
    static get instance(): WorkspaceDao {
        if (!WorkspaceDao._instance) {
            WorkspaceDao._instance = new WorkspaceDao(StorageDocument.get(WorkspaceDao.WORKSPACE));
        }
        return WorkspaceDao._instance;
    }

    // Constants
    private static readonly LAST_OPEN = "lastOpen";
    private static readonly WORKSPACE = "workspace";
    private static readonly PATH_SEPARATOR = "/";
    private static readonly OBJECT_CONTENT = "objectContent";
}
