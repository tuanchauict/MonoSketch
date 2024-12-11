/*
 * Copyright (c) 2024, tuanchauict
 */

/**
 * An interface for custom serialization and deserialization.
 */
export interface Serializable {
    serialize(value: any): any;

    deserialize(value: any): any;
}

export const serializer =
    (serialize: (value: any) => any, deserialize: (value: any) => any): Serializable => ({ serialize, deserialize });

/**
 * A decorator to define a serial name for a field of a class.
 *
 * @param name - The serial name of the field. For example, if the field name is `foo` and the serial name is `f`,
 * the field will be serialized as `{f: value}` instead of `{foo: value}`.
 * @see Serializable
 */
export function SerialName(name: string) {
    return function (target: any, propertyKey: string | symbol) {
        if (!target.constructor.serialNames) {
            target.constructor.serialNames = {};
        }
        target.constructor.serialNames[propertyKey] = name;
    };
}

/**
 * A decorator to define a serializer for a field of a class.
 *
 * @param serializer - The serializer object.
 * @see Serializable
 */
export function Serializer(serializer: Serializable) {
    return function (target: any, propertyKey: string | symbol) {
        if (!target.constructor.serializers) {
            target.constructor.serializers = {};
        }
        target.constructor.serializers[propertyKey] = serializer;
    };
}

/**
 * A decorator to make a class serializable with customizable field names and serializer functions.
 * This decorator adds `toJson` and `fromJson` methods to the class, allowing instances to be
 * serialized to JSON and deserialized from JSON.
 *
 * @param constructor - The constructor function of the class to be decorated.
 */
export function Jsonizable(constructor: Function) {
    // @ts-ignore
    if (!constructor.serializers) {
        // @ts-ignore
        constructor.serializers = {};
    }
    // @ts-ignore
    if (!constructor.serialNames) {
        // @ts-ignore
        constructor.serialNames = {};
    }

    // @ts-ignore
    const serialNames = constructor.serialNames;
    // @ts-ignore
    const serializers = constructor.serializers;

    constructor.prototype.toJson = function () {
        const json: any = {};
        const instance = this;
        for (const key in instance) {
            if (!instance.hasOwnProperty(key)) {
                continue;
            }
            // If the key is not defined in serialNames, use the key itself
            const serializedKey = serialNames[key] ?? key;

            // 1st: Check if the field has a serializer
            // 2nd: Check if the field has a toJson method
            // 3rd: Use the value directly
            if (serializers[key]) {
                json[serializedKey] = serializers[key].serialize(instance[key]);
            } else if (instance[key].toJson) {
                json[serializedKey] = instance[key].toJson();
            } else {
                json[serializedKey] = instance[key];
            }
        }
        return json;
    };

    // @ts-ignore
    constructor.fromJson = function (data: any) {
        // @ts-ignore
        const instance = new constructor();
        for (const key of Object.keys(instance)) {
            // If the key is not defined in serialNames, use the key itself
            const serializedKey = serialNames[key] ?? key;
            const value = data[serializedKey];
            if (value === undefined) {
                continue;
            }

            const field = instance[key];
            // 1st: Check if the field has a serializer
            // 2nd: Check if the field has a fromJson method
            // 3rd: Use the value directly
            console.log(field, key, value);
            if (serializers[key]) {
                instance[key] = serializers[key].deserialize(value);
            } else if (field && field.constructor && field.constructor.fromJson) {
                instance[key] = field.constructor.fromJson(value);
            } else {
                instance[key] = value;
            }
        }

        return instance;
    };
}
