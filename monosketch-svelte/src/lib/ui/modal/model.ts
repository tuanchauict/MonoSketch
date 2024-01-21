import { Rect } from '$libs/graphics-geo/rect';

export namespace TargetBounds {
    export function fromElement(element: HTMLElement): Rect {
        const bounds = element.getBoundingClientRect();
        return Rect.byLTWH(bounds.left, bounds.top, bounds.width, bounds.height);
    }

    export const centerHorizontal = (rect: Rect): number => rect.left + rect.width / 2;
    export const centerVertical = (rect: Rect): number => rect.top + rect.height / 2;
}
