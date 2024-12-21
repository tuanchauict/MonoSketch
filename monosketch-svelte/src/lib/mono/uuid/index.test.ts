/*
 * Copyright (c) 2024, tuanchauict
 */

import { UUID } from "$mono/uuid/index";
import { expect, test } from "vitest";

test("Check UUID", () => {
    const now = Date.now();
    // @ts-expect-error mock now return value for testing
    UUID.now = () => now;

    const uuid = UUID.generate();
    console.log(uuid);

    expect(uuid.length).toBe(31);
    expect(uuid.slice(0, 3)).toBe("02-");

    // @ts-expect-error Access private method for testing
    expect(uuid.slice(3, 11)).toBe(UUID.toBase64(now).slice(0, -2));
});
