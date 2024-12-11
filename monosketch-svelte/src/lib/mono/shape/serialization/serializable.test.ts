/*
 * Copyright (c) 2024, tuanchauict
 */

import { Jsonizable, serializer, Serializer, SerialName } from "$mono/shape/serialization/serializable";
import { describe, expect, it } from "vitest";

enum AnEnum {
    A,
    B,
    C
}

const AnEnumSerializer = {
    serialize: (value: AnEnum): string => {
        switch (value) {
            case AnEnum.A:
                return "xA";
            case AnEnum.B:
                return "xB";
            case AnEnum.C:
                return "xC";
        }
    },

    deserialize: (value: string): AnEnum => {
        switch (value) {
            case "xA":
                return AnEnum.A;
            case "xB":
                return AnEnum.B;
            case "xC":
                return AnEnum.C;
            default:
                throw new Error(`Unrecognizable value ${value}`);
        }
    },
}

@Jsonizable
class Bar {
    @SerialName("a")
    public aString: string = "";

    public aNumber: number = 123;

    private constructor() {
    }

    static create(aString: string, aNumber: number): Bar {
        const instance = new Bar();
        instance.aString = aString;
        instance.aNumber = aNumber;
        return instance;
    }
}

class Pair {
    public first: number = 0;
    public second: number = 0;

    constructor(first: number, second: number) {
        this.first = first;
        this.second = second;
    }
}

const PairSerializer = serializer(
    (value: Pair) => `${value.first}|${value.second}`,
    (value: string) => {
        const [first, second] = value.split("|").map(Number);
        if (isNaN(first) || isNaN(second)) {
            throw new Error(`Invalid Pair format: ${value}`);
        }
        return new Pair(first, second);
    }
)

@Jsonizable
class Foo {
    @SerialName("b")
    public bar: Bar = Bar.create("default", 0);

    public aBoolean: boolean = true;

    @Serializer(AnEnumSerializer)
    public anEnum: AnEnum = AnEnum.A;

    @SerialName("p")
    @Serializer(PairSerializer)
    public pair: Pair = new Pair(1, 2);

    private constructor() {
    }

    static create(bar: Bar, aBoolean: boolean, anEnum: AnEnum): Foo {
        const instance = new Foo();
        instance.bar = bar;
        instance.aBoolean = aBoolean;
        instance.anEnum = anEnum;
        return instance;
    }
}

describe("Serializable", () => {
    it("should serialize and deserialize correctly", () => {
        const foo = Foo.create(Bar.create("foo-bar", 100), false, AnEnum.C);
        // @ts-ignore
        expect(foo.toJson()).toStrictEqual({
            b: { a: "foo-bar", aNumber: 100 },
            aBoolean: false,
            anEnum: 'xC',
            p: "1|2",
        });

        const json = {
            b: { a: "a new string", aNumber: 999 },
            aBoolean: false,
            anEnum: 'xB',
            p: "3|4",
        }
        // @ts-ignore
        const foo2 = Foo.fromJson(json);
        expect(foo2.bar.aString).toBe("a new string");
        expect(foo2.bar.aNumber).toBe(999);
        expect(foo2.aBoolean).toBe(false);
        expect(foo2.anEnum).toBe(AnEnum.B);
        expect(foo2.pair.first).toBe(3);
        expect(foo2.pair.second).toBe(4);
    })
})
