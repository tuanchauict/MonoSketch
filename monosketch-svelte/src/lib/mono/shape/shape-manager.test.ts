/*
 * Copyright (c) 2024, tuanchauict
 */

import { Rect } from "$libs/graphics-geo/rect";
import {
    AddShape,
    ChangeOrder,
    ChangeOrderType,
    GroupShapes,
    RemoveShape,
    Ungroup,
} from "$mono/shape/command/shape-manager-commands";
import { ShapeManager } from "$mono/shape/shape-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Group } from "$mono/shape/shape/group";
import { MockShape } from "$mono/shape/shape/mock-shape";
import { Rectangle } from "$mono/shape/shape/rectangle";
import { beforeEach, describe, expect, test } from "vitest";

describe('ShapeManagerTest', () => {
    let target: ShapeManager;

    beforeEach(() => {
        target = new ShapeManager();
    });

    const addShape = (shape: AbstractShape) => {
        target.execute(new AddShape(shape));
    };

    test('testExecute_Add', () => {
        const shape1 = new MockShape(Rect.ZERO);
        target.execute(new AddShape(shape1));
        expect(target.root.itemArray).toStrictEqual([shape1]);

        const group1 = Group.create();
        target.execute(new AddShape(group1));
        expect(target.root.itemArray).toEqual([shape1, group1]);

        const shape2 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group1.id });
        target.execute(new AddShape(shape2));
        expect(target.root.itemArray).toEqual([shape1, group1]);
        expect(group1.itemArray).toEqual([shape2]);

        const shape3 = new MockShape(Rect.ZERO, '1000');
        target.execute(new AddShape(shape3));
        expect(target.root.itemArray).toEqual([shape1, group1]);
        expect(group1.itemArray).toEqual([shape2]);
    });

    test('testExecute_Remove_singleGroupItem_removeGroup', () => {
        const group = Group.create();
        const shape = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });

        addShape(group);
        addShape(shape);
        target.execute(new RemoveShape(shape));
        expect(target.root.itemCount).toBe(0);
    });

    test('testExecute_Remove_removeGroupItem_ungroup', () => {
        const group = Group.create();
        const shape1 = Rectangle.fromRect({ rect: Rect.ZERO });
        const shape2 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });
        const shape3 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });

        addShape(group);
        addShape(shape1);
        addShape(shape2);
        addShape(shape3);

        target.execute(new RemoveShape(shape3));
        expect(target.root.itemArray).toEqual([shape2, shape1]);
        expect(shape2.parentId).toBe(target.root.id);
    });

    test('testExecute_Remove_removeGroupItem_unchangeRoot', () => {
        const group = Group.create();
        const shape1 = Rectangle.fromRect({ rect: Rect.ZERO });
        const shape2 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });
        const shape3 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });
        const shape4 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });

        addShape(group);
        addShape(shape1);
        addShape(shape2);
        addShape(shape3);
        addShape(shape4);

        target.execute(new RemoveShape(shape4));
        expect(target.root.itemArray).toEqual([group, shape1]);
        expect(group.itemArray).toEqual([shape2, shape3]);
    });

    test('testExecute_Group_invalid', () => {
        const group = Group.create();
        const shape1 = Rectangle.fromRect({ rect: Rect.ZERO });
        const shape2 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });

        addShape(group);
        addShape(shape1);
        addShape(shape2);

        target.execute(new GroupShapes([shape1]));
        expect(target.root.itemArray).toEqual([group, shape1]);

        target.execute(new GroupShapes([shape1, shape2]));
        expect(target.root.itemArray).toEqual([group, shape1]);
        expect(group.itemArray).toEqual([shape2]);
    });

    test('testExecute_Group_valid', () => {
        const shape0 = Rectangle.fromRect({ rect: Rect.ZERO });
        const shape1 = Rectangle.fromRect({ rect: Rect.ZERO });
        const shape2 = Rectangle.fromRect({ rect: Rect.ZERO });
        const shape3 = Rectangle.fromRect({ rect: Rect.ZERO });

        addShape(shape0);
        addShape(shape1);
        addShape(shape2);
        addShape(shape3);
        target.execute(new GroupShapes([shape1, shape2]));

        const items = target.root.itemArray;

        expect(target.root.itemCount).toBe(3);
        expect(items[0]).toBe(shape0);
        expect(items[2]).toBe(shape3);
        const group = items[1] as Group;
        expect(group.itemArray).toEqual([shape1, shape2]);
        expect(target.getGroup(shape1.parentId)).toBe(group);
        expect(target.getGroup(shape2.parentId)).toBe(group);
    });

    test('testExecute_Ungroup', () => {
        const group = Group.create();
        const shape0 = Rectangle.fromRect({ rect: Rect.ZERO });
        const shape1 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });
        const shape2 = Rectangle.fromRect({ rect: Rect.ZERO, parentId: group.id });
        const shape3 = Rectangle.fromRect({ rect: Rect.ZERO });

        addShape(shape0);
        addShape(group);
        addShape(shape1);
        addShape(shape2);
        addShape(shape3);

        target.execute(new Ungroup(group));

        expect(target.root.itemArray).toStrictEqual([shape0, shape1, shape2, shape3]);
        expect(shape1.parentId).toBe(target.root.id);
        expect(shape2.parentId).toBe(target.root.id);
    });

    test('testExecute_ChangeOrder', () => {
        const shape1 = new MockShape(Rect.ZERO);
        const shape2 = new MockShape(Rect.ZERO);
        const shape3 = new MockShape(Rect.ZERO);

        addShape(shape1);
        addShape(shape2);
        addShape(shape3);

        target.execute(new ChangeOrder(shape3, ChangeOrderType.BACK));
        expect(target.root.itemArray).toEqual([shape1, shape2, shape3]);

        target.execute(new ChangeOrder(shape1, ChangeOrderType.BACK));
        expect(target.root.itemArray).toEqual([shape2, shape3, shape1]);

        target.execute(new ChangeOrder(shape1, ChangeOrderType.FRONT));
        expect(target.root.itemArray).toEqual([shape1, shape2, shape3]);

        target.execute(new ChangeOrder(shape3, ChangeOrderType.FRONT));
        expect(target.root.itemArray).toEqual([shape3, shape1, shape2]);

        target.execute(new ChangeOrder(shape1, ChangeOrderType.FORWARD));
        expect(target.root.itemArray).toEqual([shape1, shape3, shape2]);

        target.execute(new ChangeOrder(shape3, ChangeOrderType.BACKWARD));
        expect(target.root.itemArray).toEqual([shape1, shape2, shape3]);
    });

    test('testRecursiveVersionUpdate', () => {
        const group0 = Group.create();
        addShape(group0);
        const group1 = Group.create({ parentId: group0.id });
        addShape(group1);

        const rootOldVersion = target.root.versionCode;
        const group0OldVersion = group0.versionCode;
        const group1OldVersion = group1.versionCode;

        const group2 = Group.create({ parentId: group1.id });
        addShape(group2);

        expect(target.root.versionCode).not.toBe(rootOldVersion);
        expect(group0.versionCode).not.toEqual(group0OldVersion);
        expect(group1.versionCode).not.toBe(group1OldVersion);
    });
});
