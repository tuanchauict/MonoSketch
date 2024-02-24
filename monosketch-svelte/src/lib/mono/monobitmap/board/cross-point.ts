import type { Char } from '$libs/char';

/**
 * A data class that stores information of a cross point when drawing a bitmap with
 * [PainterBoard].
 * CrossPoint will then be drawn to the board after non-crossing pixels are drawn.
 *
 * - [boardRow] and [boardColumn] are the location of point on the board.
 * - [visualChar] is the character at the crossing point
 * - [leftChar], [rightChar], [topChar], and [bottomChar] are 4 characters around the
 * crossing point
 */
export interface CrossPoint {
    boardRow: number;
    boardColumn: number;
    visualChar: Char;
    directionChar: Char;
    leftChar: Char;
    rightChar: Char;
    topChar: Char;
    bottomChar: Char;
}
