/**
 * An interface to define all possible interaction bound types
 */
export interface InteractionBound {
    readonly interactionPoints: InteractionPoint[];
}

/**
 * An interface to define the interaction point for a shape and common APIs.
 *
 * @left the center position of the interaction point in the x-axis, in board-related unit.
 * @top the center position of the interaction point in the y-axis, in board-related unit.
 */
export interface InteractionPoint {
    shapeId: string;
    left: number;
    top: number;
    mouseCursor: MouseCursor;
}
