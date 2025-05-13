/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow } from "$libs/flow";
import { ShapeConnector } from "$mono/shape/connector/shape-connector";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Group, RootGroup } from "$mono/shape/shape/group";

/**
 * An interface which defines common APIs for a command. A command must determine direct affected
 * parent group via `getDirectAffectedParent`. If `getDirectAffectedParent` returns null, the
 * command won't be executed.
 */
export interface Command {
    getDirectAffectedParent(shapeManager: ShapeManager): Group | null;

    execute(shapeManager: ShapeManager, parent: Group): void;
}

/**
 * An interface of a shape manager which manages shapes.
 */
export class ShapeManager {
    private rootInner: Group = RootGroup(null);
    private allShapeMap: Map<string | null, AbstractShape> = new Map([[this.rootInner.id, this.rootInner]]);

    // The initial rootInner is a default root, means the app is not ready.
    // The rootId should be undefined until `replaceRoot` is called.
    private rootIdMutableFlow: Flow<string> = new Flow();
    public readonly rootIdFlow: Flow<string> = this.rootIdMutableFlow.immutable();

    public shapeConnectorInner: ShapeConnector = new ShapeConnector();

    /**
     * Reflect the version of the root through live data. The other components are able to observe
     * this version to decide update internally.
     */
    private versionMutableFlow: Flow<number> = new Flow(this.rootInner.versionCode);
    public readonly versionFlow: Flow<number> = this.versionMutableFlow.immutable();

    constructor() {
    }

    get root(): Group {
        return this.rootInner;
    }

    get shapeConnector(): ShapeConnector {
        return this.shapeConnectorInner;
    }

    /**
     * Replace [root] with [newRoot].
     * This also wipe current stored shapes with shapes in new root.
     */
    replaceRoot(newRoot: Group, newConnector: ShapeConnector) {
        const currentVersion = this.rootInner.versionCode;
        this.rootInner = newRoot;
        this.rootIdMutableFlow.value = newRoot.id;

        this.shapeConnectorInner = newConnector;

        this.allShapeMap = this.createAllShapeMap(newRoot);

        this.versionMutableFlow.value =
            // If the version of the new root is the same as the current version, we need to
            // decrease the version to make sure the version is updated.
            // This does not affect the version of the root, but the version of the shape manager.
            currentVersion === newRoot.versionCode ? currentVersion - 1 : newRoot.versionCode;
    }

    private createAllShapeMap(group: Group): Map<string, AbstractShape> {
        const map = new Map<string, AbstractShape>();
        map.set(group.id, group);
        this.createAllShapeMapRecursive(group, map);
        return map;
    }

    private createAllShapeMapRecursive(group: Group, map: Map<string, AbstractShape>) {
        for (const shape of group.items) {
            map.set(shape.id, shape);
            if (shape instanceof Group) {
                this.createAllShapeMapRecursive(shape, map);
            }
        }
    }

    execute(command: Command) {
        const affectedParent = command.getDirectAffectedParent(this);
        if (!affectedParent) return;

        const allAncestors = this.getAllAncestors(affectedParent);
        const currentVersion = affectedParent.versionCode;

        command.execute(this, affectedParent);

        if (currentVersion === affectedParent.versionCode && this.allShapeMap.has(affectedParent.id)) {
            return;
        }
        for (const parent of allAncestors) {
            parent.update(() => true);
        }
        this.versionMutableFlow.value = this.rootInner.versionCode;
    }

    getGroup(shapeId: string | null): Group | null {
        return shapeId === null ? this.rootInner : (this.allShapeMap.get(shapeId) as Group | undefined) || null;
    }

    getShape(shapeId: string): AbstractShape | undefined {
        return this.allShapeMap.get(shapeId);
    }

    register(shape: AbstractShape) {
        this.allShapeMap.set(shape.id, shape);
    }

    unregister(shape: AbstractShape) {
        this.allShapeMap.delete(shape.id);
    }

    private getAllAncestors(group: Group): Group[] {
        const result: Group[] = [];
        let parent = this.allShapeMap.get(group.parentId) as Group | undefined;
        while (parent) {
            result.push(parent);
            parent = this.allShapeMap.get(parent.parentId) as Group | undefined;
        }
        return result;
    }

    /**
     * Notifies that the information of the working project having update.
     * The update is not only the shape list, but also the other information like name, etc.
     * This helps the subsequence actions to keep the information up-to-date.
     */
    notifyProjectUpdate() {
        this.rootIdMutableFlow.value = this.rootInner.id;
    }
}
