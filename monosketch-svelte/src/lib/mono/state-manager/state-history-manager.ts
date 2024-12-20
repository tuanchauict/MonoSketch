/*
 * Copyright (c) 2024, tuanchauict
 */

import type { Workspace } from "$app/workspace";
import { LifecycleOwner } from "$libs/flow";
import { Flow } from '$libs/flow/flow';
import { getOrNull } from "$libs/sequence";
import { DEBUG_MODE } from "$mono/build_environment";
import { ShapeConnector } from '$mono/shape/connector/shape-connector';
import type { SerializableLineConnector } from "$mono/shape/serialization/connector";
import type { SerializableGroup } from "$mono/shape/serialization/shapes";
import { RootGroup } from "$mono/shape/shape/group";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import type { WorkspaceDao } from "$mono/store-manager/dao/workspace-dao";
import type { WorkspaceObjectDao } from "$mono/store-manager/dao/workspace-object-dao";
import { UUID } from "$mono/uuid";

/**
 * A class which manages state history of the shapes.
 */
export class StateHistoryManager {
    private historyStack = new HistoryStack();

    constructor(
        private environment: CommandEnvironment,
        private workspace: Workspace,
        private workspaceDao: WorkspaceDao,
    ) {
    }

    /**
     * Restores and starts observing the state change of the object with [rootId].
     * If [rootId] is empty, the last opened object is used. In case of there is no objects in the
     * database, a new ID is generated with [UUID.generate].
     *
     * If [rootId] is not in the database, a new object will be created and use [rootId] as the id
     * of the root shape.
     */
    restoreState(rootId: string) {
        const objectDao = this.getObjectDaoById(rootId);

        this.restoreShapes(objectDao);
        this.workspace.setDrawingOffset(objectDao.offset);
    }

    observeStateChange(lifecycleOwner: LifecycleOwner) {
        const versionCodeWIthEditingModeFlow = Flow.combine2(
            // TODO: Add versionFlow to environment.
            this.environment.shapeManager.versionFlow,
            this.environment.editingModeFlow,
            (versionCode, editingMode) => ({ versionCode, editingMode }),
        );
        versionCodeWIthEditingModeFlow.observe(lifecycleOwner, ({ versionCode, editingMode }) => {
            if (!editingMode.isEditing && versionCode !== editingMode.skippedVersion) {
                this.registerBackupShapes(versionCode);
            }
        });
    }

    /**
     * Gets the [WorkspaceObjectDao] with [rootId].
     * If [rootId] is not empty, the object with [rootId] is returned even if it is not in the
     * database.
     * If [rootId] is empty, the last opened object is used. In case of there is no objects in the
     * database, a new ID is generated with [UUID.generate].
     */
    private getObjectDaoById(rootId: string): WorkspaceObjectDao {
        if (rootId) {
            return this.workspaceDao.getObject(rootId);
        }

        return getOrNull(this.workspaceDao.getObjects(), 0) || this.workspaceDao.getObject(UUID.generate());
    }

    clear() {
        this.historyStack.clear();
    }

    undo() {
        this.historyStack.undo()?.apply(this.environment);
    }

    redo() {
        this.historyStack.redo()?.apply(this.environment);
    }

    private registerBackupShapes(version: number) {
        setTimeout(() => {
            // Only backup if the shape manager is idle.
            if (this.environment.shapeManager.versionFlow.value === version) {
                this.backupShapes();
            }
        }, 300);
    }

    private backupShapes() {
        const root = this.environment.currentRootGroup;
        const serializableGroup = root.toSerializableShape(true);
        // TODO: Add connectors to environment.
        const shapeConnector = this.environment.shapeManager.shapeConnector;
        const serializableLineConnectors = shapeConnector.toSerializable();

        this.historyStack.pushState(
            root.versionCode,
            serializableGroup,
            serializableLineConnectors,
        );

        const objectDao = this.workspaceDao.getObject(root.id);
        objectDao.rootGroup = serializableGroup;
        objectDao.connectors = serializableLineConnectors;
    }

    private restoreShapes(objectDao: WorkspaceObjectDao) {
        const serializableGroup = objectDao.rootGroup;
        const rootGroup = serializableGroup ? RootGroup(serializableGroup) : RootGroup(objectDao.objectId);
        const shapeConnector = ShapeConnector.fromSerializable(objectDao.connectors);
        this.environment.replaceRoot(rootGroup, shapeConnector);
    }
}

class HistoryStack {
    private undoStack: History[] = [];
    private redoStack: History[] = [];

    pushState(version: number, state: SerializableGroup, connectors: SerializableLineConnector[]) {
        if (version === this.undoStack[this.undoStack.length - 1]?.versionCode) {
            return;
        }
        this.undoStack.push(new History(version, state, connectors));
        this.redoStack = [];

        if (DEBUG_MODE) {
            console.log(`Push history stack ${this.undoStack.map((it) => it.versionCode)}`);
        }
    }

    clear() {
        this.undoStack = [];
        this.redoStack = [];
    }

    undo(): History | undefined {
        if (this.undoStack.length <= 1) {
            return undefined;
        }
        const currentState = this.undoStack.pop();
        if (currentState) {
            this.redoStack.push(currentState);
        }
        return this.undoStack[this.undoStack.length - 1];
    }

    redo(): History | undefined {
        const currentState = this.redoStack.pop();
        if (currentState) {
            this.undoStack.push(currentState);
        }
        return currentState;
    }
}

class History {
    constructor(
        public versionCode: number,
        public serializableGroup: SerializableGroup,
        public connectors: SerializableLineConnector[],
    ) {
    }

    apply(environment: CommandEnvironment) {
        const root = RootGroup(this.serializableGroup);
        const shapeConnector = ShapeConnector.fromSerializable(this.connectors);
        environment.replaceRoot(root, shapeConnector);
    }
}
