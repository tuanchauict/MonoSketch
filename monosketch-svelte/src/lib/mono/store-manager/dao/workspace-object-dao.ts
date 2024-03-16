import { StorageDocument } from "$mono/store-manager";

/**
 * A dao for an object (aka project or file) in the workspace.
 */
export class WorkspaceObjectDao {
    objectId: string;
    private objectDocument: StorageDocument;

    constructor(objectId: string, workspaceDocument: StorageDocument) {
        this.objectId = objectId;
        this.objectDocument = workspaceDocument.childDocument(objectId);
    }

    get name(): string {
        // TODO: Implement this

        throw new Error("Not implemented");
    }

    get lastOpened(): number {
        // TODO: Implement this

        throw new Error("Not implemented");
    }

    removeSelf(): void {
        // TODO: Remove all the data related to this object.
    }
}
