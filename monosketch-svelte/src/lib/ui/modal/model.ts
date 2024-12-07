import { Rect } from '$libs/graphics-geo/rect';

export namespace TargetBounds {
    export const fromElement = (element: HTMLElement): Rect => {
        const bounds = element.getBoundingClientRect();
        return Rect.byLTWHf(bounds.left, bounds.top, bounds.width, bounds.height);
    };

    export const expandTargetBounds = (
        targetBounds: Rect,
        horizontalExpand: number,
        verticalExpand: number,
    ): Rect =>
        Rect.byLTWHf(
            targetBounds.left - horizontalExpand,
            targetBounds.top - verticalExpand,
            targetBounds.width + horizontalExpand * 2,
            targetBounds.height + verticalExpand * 2,
        );

    export const centerHorizontal = (rect: Rect): number => rect.left + rect.width / 2;

    export const centerVertical = (rect: Rect): number => rect.top + rect.height / 2;
}
