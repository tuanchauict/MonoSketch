import type { Identifier } from "$mono/shape/collection/identifier";
import { beforeEach, describe, expect, test } from "vitest";
import { QuickList, AddPosition, MoveActionType } from './quick-list';

describe('QuickList', () => {
    let target: QuickList<Item>;

    class Item implements Identifier {
        id: string;

        constructor(id: string) {
            this.id = id;
        }
    }

    const TestData = {
        ITEM_0: new Item('0'),
        ITEM_1: new Item('1'),
        ITEM_2: new Item('2'),
        ITEM_3: new Item('3'),
        get ITEMS() {
            return [this.ITEM_0, this.ITEM_1, this.ITEM_2, this.ITEM_3];
        },
    };

    beforeEach(() => {
        target = new QuickList<Item>();
    });

    test('add', () => {
        expect(target.isEmpty()).toBeTruthy();

        expect(target.add(TestData.ITEM_1)).toBeTruthy();
        expect(target.add(TestData.ITEM_1)).toBeFalsy();
        expect(target.size).toBe(1);
        expect(target.get('1')).toEqual(TestData.ITEM_1);

        target.add(TestData.ITEM_0, AddPosition.First);
        expect(target.get('0')).toEqual(TestData.ITEM_0);
        expect(target.get('1')).toEqual(TestData.ITEM_1);
        expect(Array.from(target)).toEqual([TestData.ITEM_0, TestData.ITEM_1]);

        target.add(TestData.ITEM_3);
        target.add(TestData.ITEM_2, AddPosition.After(TestData.ITEM_1));
        expect(Array.from(target)).toEqual([TestData.ITEM_0, TestData.ITEM_1, TestData.ITEM_2, TestData.ITEM_3]);
    });

    describe('addAll', () => {
        test('last', () => {
            target.add(TestData.ITEM_0);
            target.add(TestData.ITEM_1);
            target.addAll(TestData.ITEMS.slice(2));
            expect(Array.from(target)).toEqual(TestData.ITEMS);
        });

        test('first', () => {
            target.add(TestData.ITEM_2);
            target.add(TestData.ITEM_3);
            target.addAll(TestData.ITEMS.slice(0, 2), AddPosition.First);
            expect(Array.from(target)).toEqual(TestData.ITEMS);
        });

        test('after', () => {
            target.add(TestData.ITEM_0);
            target.add(TestData.ITEM_3);
            target.addAll(TestData.ITEMS.slice(1, 3), AddPosition.After(TestData.ITEM_0));
            expect(Array.from(target)).toEqual(TestData.ITEMS);
        });
    });

    test('remove', () => {
        target.add(TestData.ITEM_0);
        target.add(TestData.ITEM_1);

        expect(target.remove(TestData.ITEM_2)).toBeNull();

        expect(target.remove(new Item('1'))).toEqual(TestData.ITEM_1);
        expect(target.size).toBe(1);
        expect(target.get('1')).toBeNull();

        expect(target.remove(new Item('0'))).toEqual(TestData.ITEM_0);
        expect(target.isEmpty()).toBeTruthy();
    });

    test('removeAll', () => {
        target.addAll(TestData.ITEMS);
        target.removeAll();
        expect(target.isEmpty()).toBeTruthy();
    });

    describe('move', () => {
        test('up', () => {
            target.addAll(TestData.ITEMS);

            expect(target.move(new Item('1'), MoveActionType.UP)).toBeTruthy();
            expect(Array.from(target)).toEqual([TestData.ITEM_0, TestData.ITEM_2, TestData.ITEM_1, TestData.ITEM_3]);
        });

        test('down', () => {
            target.addAll(TestData.ITEMS);

            expect(target.move(new Item('2'), MoveActionType.DOWN)).toBeTruthy();
            expect(Array.from(target)).toEqual([TestData.ITEM_0, TestData.ITEM_2, TestData.ITEM_1, TestData.ITEM_3]);
        });

        test('top', () => {
            target.addAll(TestData.ITEMS);

            expect(target.move(new Item('1'), MoveActionType.TOP)).toBeTruthy();
            expect(Array.from(target)).toEqual([TestData.ITEM_0, TestData.ITEM_2, TestData.ITEM_3, TestData.ITEM_1]);
        });

        test('bottom', () => {
            target.addAll(TestData.ITEMS);

            expect(target.move(new Item('2'), MoveActionType.BOTTOM)).toBeTruthy();
            expect(Array.from(target)).toEqual([TestData.ITEM_2, TestData.ITEM_0, TestData.ITEM_1, TestData.ITEM_3]);
        });
    });
});
