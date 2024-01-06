/**
 * Bounds of the target element on the screen.
 */
export class TargetBounds {
    static fromElement(element: HTMLElement): TargetBounds {
        const bounds = element.getBoundingClientRect();
        return new TargetBounds(bounds.left, bounds.top, bounds.width, bounds.height);
    }

    constructor(
        public left: number,
        public top: number,
        public width: number,
        public height: number,
    ) {}

    get centerHorizontal(): number {
        return this.left + this.width / 2;
    }

    get centerVertical(): number {
        return this.top + this.height / 2;
    }
}
