/*
 * Copyright (c) 2024, tuanchauict
 */


import { type Command, ShapeManager } from "$mono/shape/shape-manager";
import type { Group } from "$mono/shape/shape/group";
import type { Text } from "$mono/shape/shape/text";

/**
 * A [Command] for changing text for Text shape.
 */
export class ChangeText implements Command {
    constructor(private readonly target: Text, private readonly newText: string) {
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.target.parentId);
    }

    execute(_shapeManager: ShapeManager, parent: Group) {
        const currentVersion = this.target.versionCode;
        this.target.setText(this.newText);
        parent.update(() => currentVersion !== this.target.versionCode);
    }
}

/**
 * A [Command] for making an uneditable text to be editable.
 */
export class MakeTextEditable implements Command {
    constructor(private readonly target: Text) {
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.target.parentId);
    }

    execute(_shapeManager: ShapeManager, parent: Group) {
        const currentVersion = this.target.versionCode;
        this.target.makeTextEditable();
        parent.update(() => currentVersion !== this.target.versionCode);
    }
}

/**
 * A [Command] for updating text shape's text editing mode.
 */
export class UpdateTextEditingMode implements Command {
    constructor(private readonly target: Text, private readonly isEditing: boolean) {
    }

    getDirectAffectedParent(shapeManager: ShapeManager): Group | null {
        return shapeManager.getGroup(this.target.parentId);
    }

    execute(_shapeManager: ShapeManager, parent: Group) {
        const currentVersion = this.target.versionCode;
        this.target.setTextEditingMode(this.isEditing);
        parent.update(() => currentVersion !== this.target.versionCode);
    }
}
