import { DirectedPoint, Direction, Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { ShapeExtraManager } from "$mono/shape/extra/extra-manager";
import { LineExtra, type ShapeExtra } from "$mono/shape/extra/shape-extra";
import { AbstractSerializableShape, SerializableLine } from "$mono/shape/serialization/shapes";
import { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { LineHelper, LineEdge, LineAnchor, type LineAnchorPointUpdate } from "$mono/shape/shape/linehelper";

/**
 * A line shape which connects two end-dots with a collection of straight lines.
 *
 * A Line shape is defined by two end points which have direction. The inner algorithm will use the
 * defined direction to generate straight lines by creating joint points.
 * Line shapes are able to be modified by moving end points or moving connecting edges. Once the
 * edge is modified, the line won't depend on seeding direction.
 *
 * First initial line's edges will be decided by the direction inside the two end points
 * Examples
 *
 * 1. Same line
 * Start: (0, 0, Horizontal)
 * End  : (5, 0, Vertical)
 * Result:
 * ```
 * x----x
 * ```
 *
 * 2. Different line
 * 2.1.
 * Start: (0, 0, Horizontal)
 * End  : (5, 3, Vertical)
 * Result:
 * ```
 * x----+
 *      |
 *      |
 *      x
 * ```
 * 2.2.
 * Start: (0, 0, Horizontal)
 * End  : (5, 3, Horizontal)
 * Result:
 * ```
 * x-+
 *   |
 *   |
 *   +--x
 * ```
 * 2.3.
 * Start: (0, 0, Vertical)
 * End  : (5, 3, Horizontal)
 * Result:
 * ```
 * x
 * |
 * |
 * +----x
 * ```
 * 2.4.
 * Start: (0, 0, Vertical)
 * End  : (5, 4, Vertical)
 * Result:
 * ```
 * x
 * |
 * +----+
 *      |
 *      x
 * ```
 *
 * TODO: Extract move anchor point and move edge code to use case class
 */
export class Line extends AbstractShape {
    startPoint: DirectedPoint;
    endPoint: DirectedPoint;
    private jointPoints: Point[];

    edges: LineEdge[];
    extraInner: LineExtra = LineExtra.create(ShapeExtraManager.defaultLineExtra);

    /**
     * A list of joint points which is determined once an edge is updated.
     */
    private confirmedJointPoints: Point[] = [];

    private constructor(
        startPoint: DirectedPoint,
        endPoint: DirectedPoint,
        id: string | null = null,
        parentId: string | null = null,
    ) {
        super(id, parentId);
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.jointPoints = LineHelper.createJointPoints([startPoint, endPoint]);
        this.edges = LineHelper.createEdges(this.jointPoints);
    }

    static fromPoints({ startPoint, endPoint, id = null, parentId = null }: {
        startPoint: DirectedPoint,
        endPoint: DirectedPoint,
        id?: string | null,
        parentId?: string | null
    }): Line {
        return new Line(startPoint, endPoint, id, parentId);
    }

    get reducedJoinPoints(): Point[] {
        return LineHelper.reduce(this.jointPoints);
    }

    get bound(): Rect {
        const points = this.reducedJoinPoints;
        const left = Math.min(...points.map(p => p.left));
        const right = Math.max(...points.map(p => p.left));
        const top = Math.min(...points.map(p => p.top));
        const bottom = Math.max(...points.map(p => p.top));
        return Rect.byLTRB(left, top, right, bottom);
    }

    static fromSerializable(serializableLine: SerializableLine, parentId: string): Line {
        const line = new Line(serializableLine.startPoint, serializableLine.endPoint, serializableLine.actualId, parentId);
        line.jointPoints = serializableLine.jointPoints;
        if (serializableLine.wasMovingEdge) {
            line.confirmedJointPoints = line.jointPoints;
        }
        line.edges = LineHelper.createEdges(line.jointPoints);
        line.extraInner = LineExtra.fromSerializable(serializableLine.extra);
        line.versionCode = serializableLine.versionCode;
        return line;
    }

    toSerializableShape(isIdIncluded: boolean): AbstractSerializableShape {
        return SerializableLine.create({
            id: this.id,
            isIdTemporary: !isIdIncluded,
            versionCode: this.versionCode,
            startPoint: this.startPoint,
            endPoint: this.endPoint,
            jointPoints: this.jointPoints,
            extra: this.extraInner.toSerializableExtra(),
            wasMovingEdge: this.wasMovingEdge(),
        });
    }

    setBound(newBound: Rect): void {
        const left = Math.min(...this.jointPoints.map(p => p.left));
        const top = Math.min(...this.jointPoints.map(p => p.top));
        const offsetPoint = new Point(newBound.left - left, newBound.top - top);
        if (offsetPoint.left === 0 && offsetPoint.top === 0) {
            return;
        }
        this.update(() => {
            this.startPoint = this.startPoint.plus(offsetPoint);
            this.endPoint = this.endPoint.plus(offsetPoint);
            this.jointPoints = this.jointPoints.map(p => p.plus(offsetPoint));
            this.confirmedJointPoints = this.confirmedJointPoints.map(p => p.plus(offsetPoint));
            this.edges = LineHelper.createEdges(this.jointPoints);
            return true;
        });
    }

    get extra(): LineExtra {
        return this.extraInner;
    }

    setExtra(newExtra: ShapeExtra): void {
        if (!(newExtra instanceof LineExtra)) {
            throw new Error(`New extra is not a LineExtra (${newExtra.constructor.name})`);
        }
        if (newExtra === this.extra) {
            return;
        }
        this.update(() => {
            this.extraInner = newExtra;
            return true;
        });
    }

    /**
     * Move start point or end point to new location decided by [AnchorPointUpdate.anchor] of
     * [anchorPointUpdate].
     * If the line's edges have never been moved, new edges will be decided by new anchor point and
     * unaffected point with their direction like at the initial step.
     * Otherwise, the anchor point is just moved to new point.
     * New point in the middle will be introduced if the new position is not on the same line with
     * the previous containing edge.
     *
     * Examples for moved:
     * Case 1. Line's edges are never moved or the number of joint points is 2:
     * See examples in the class doc
     *
     * Case 2. Line's edges have been moved and [justMoveAnchor] is true and number of joint points
     * is larger than 2: update the position of the anchor and the adjacent point:
     * Input
     * ```
     * +-----+
     *       |
     *       o
     * ```
     * Output
     * ```
     * +------------+
     *              |
     *              |
     *              o
     * ```
     *
     * Case 3. Line's edges have been moved and [justMoveAnchor] is false:
     * Input
     * ```
     * +-------o
     * ```
     * Result:
     * 3.1. Same line: only update the anchor's position
     * ```
     * +---------------x
     * ```
     *
     * 3.2. Different lines: create new joint point
     * ```
     * +----------+
     *            |
     *            x
     * ```
     */
    moveAnchorPoint(
        anchorPointUpdate: LineAnchorPointUpdate,
        isReduceRequired: boolean,
        justMoveAnchor: boolean,
    ): void {
        this.update(() => {
            switch (anchorPointUpdate.anchor) {
                case LineAnchor.START:
                    this.startPoint = anchorPointUpdate.point;
                    break;
                case LineAnchor.END:
                    this.endPoint = anchorPointUpdate.point;
                    break;
            }

            const isEdgeUpdated = this.confirmedJointPoints.length > 0;
            let newJointPoints: Point[];
            if (!isEdgeUpdated || this.confirmedJointPoints.length === 2) {
                newJointPoints = LineHelper.createJointPoints([this.startPoint, this.endPoint]);
            } else if (justMoveAnchor && this.confirmedJointPoints.length > 2) {
                newJointPoints = [...this.confirmedJointPoints];
                const [anchorIndex, affectedIndex] = anchorPointUpdate.anchor === LineAnchor.START ? [0, 1] : [newJointPoints.length - 1, newJointPoints.length - 2];
                const newPoint = anchorPointUpdate.point.point;
                newJointPoints[affectedIndex] = newJointPoints[anchorIndex].left === newJointPoints[affectedIndex].left
                    ? newJointPoints[affectedIndex].copy({ left: newPoint.left })
                    : newJointPoints[affectedIndex].copy({ top: newPoint.top });
                newJointPoints[anchorIndex] = newPoint;
            } else {
                const newJointPoint = this.createNewJointPoint(anchorPointUpdate);
                newJointPoints = [...this.confirmedJointPoints];
                const [anchorIndex, newJointPointIndex] = anchorPointUpdate.anchor === LineAnchor.START ? [0, 1] : [newJointPoints.length - 1, newJointPoints.length];
                newJointPoints[anchorIndex] = anchorPointUpdate.point.point;
                if (newJointPoint) {
                    newJointPoints.splice(newJointPointIndex, 0, newJointPoint);
                }
            }
            const isUpdated = this.jointPoints !== newJointPoints;
            this.jointPoints = isReduceRequired ? LineHelper.reduce(newJointPoints) : newJointPoints;
            if (isReduceRequired && isEdgeUpdated) {
                this.confirmedJointPoints = this.jointPoints;
            }
            this.edges = LineHelper.createEdges(this.jointPoints);
            return isUpdated;
        });
    }

    private createNewJointPoint(anchorPointUpdate: LineAnchorPointUpdate): Point | null {
        const [anchorPointIndex, previousPointIndex] = anchorPointUpdate.anchor === LineAnchor.START ? [0, 1] : [this.jointPoints.length - 1, this.jointPoints.length - 2];
        const anchorEndPoint = this.jointPoints[anchorPointIndex];
        const previousJointPoint = this.jointPoints[previousPointIndex];
        const updatePoint = anchorPointUpdate.point.point;

        const isOnSameLine = LineHelper.isOnStraightLine(anchorEndPoint, previousJointPoint, updatePoint, false);
        if (isOnSameLine) {
            return null;
        }

        return LineHelper.isHorizontal(anchorEndPoint, previousJointPoint)
            ? new Point(updatePoint.left, anchorEndPoint.top)
            : new Point(anchorEndPoint.left, updatePoint.top);
    }

    /**
     * Move the targeted edge by [edgeId] to make its line contains [point].
     * During moving edge, two anchor points won't be moved.
     * If [edgeId] is the first or last edge, new edge will be introduced.
     *
     * Examples:
     * 1. Move single edge
     * ```
     * x---o---x
     * ```
     * Result:
     * ```
     * x       x
     * |       |
     * +---o---+
     * ```
     *
     * 2. Move 1st/last edge
     * ```
     * x----o----+       x---------+
     *           |                 |
     *           |                 o
     *           |                 |
     *           x                 x
     * ```
     * Result:
     * ```
     * x                x-----+
     * |                      |
     * +---o---+              o
     *         |              |
     *         x              +----x
     * ```
     * 3. Move edge in the middle
     * ```
     * x-------+
     *         |
     *         o
     *         |
     * x-------+
     * ```
     * Result
     * ```
     * x--------------+
     *                |
     *                o
     *                |
     * x--------------+
     * ```
     * Once the edge is moved successfully, the line becomes independent of direction in the
     * stored in two anchor points.
     *
     * When reduce move is on ([isReduceRequired]), all adjacent edges which are on the same line
     * by the same direction will be merged.
     * For example
     * ```
     * 1-----2-----3   ->   1-----------3
     *
     * 1-----3-----2   ->   1-----3-----2
     * ```
     */
    moveEdge(edgeId: number, point: Point, isReduceRequired: boolean): void {
        this.update(() => {
            const edgeIndex = this.edges.findIndex(edge => edge.id === edgeId);
            if (edgeIndex < 0) {
                return false;
            }

            const edge = this.edges[edgeIndex];
            const newEdge = edge.translate(point);
            if (!isReduceRequired && edge.equals(newEdge)) {
                return false;
            }

            const newJointPoints = [...this.jointPoints];
            if (edgeIndex === 0 && edgeIndex === this.edges.length - 1) {
                newJointPoints.splice(1, 0, newEdge.startPoint);
                newJointPoints.splice(2, 0, newEdge.endPoint);
            } else if (edgeIndex === 0) {
                newJointPoints.splice(1, 0, newEdge.startPoint);
                newJointPoints[2] = newEdge.endPoint;
            } else if (edgeIndex === this.edges.length - 1) {
                const startPointIndex = newJointPoints.length - 2;
                newJointPoints[startPointIndex] = newEdge.startPoint;
                newJointPoints.splice(startPointIndex + 1, 0, newEdge.endPoint);
            } else {
                const startPointIndex = newJointPoints.findIndex(p => p.equals(edge.startPoint));
                newJointPoints[startPointIndex] = newEdge.startPoint;
                newJointPoints[startPointIndex + 1] = newEdge.endPoint;
            }

            const isUpdated = this.jointPoints !== newJointPoints;
            this.jointPoints = isReduceRequired ? LineHelper.reduce(newJointPoints) : newJointPoints;
            this.confirmedJointPoints = this.jointPoints;

            const newEdges = LineHelper.createEdges(this.jointPoints);
            if (!isReduceRequired) {
                const newEdgeIndex = edgeIndex === 0 ? 1 : edgeIndex;
                newEdges[newEdgeIndex] = newEdges[newEdgeIndex].copy({ id: edge.id });
            }
            this.edges = newEdges;

            return isUpdated;
        });
    }

    getDirection(anchor: LineAnchor): Direction {
        return anchor === LineAnchor.START ? this.startPoint.direction : this.endPoint.direction;
    }

    wasMovingEdge(): boolean {
        return this.confirmedJointPoints.length > 0;
    }

    contains(point: Point): boolean {
        return this.edges.some(edge => edge.contains(point));
    }

    isVertex(point: Point): boolean {
        return false; // TODO: Correct this to any of its joint points
    }

    isOverlapped(rect: Rect): boolean {
        return this.edges.some(edge => {
            const edgeBound = Rect.byLTRB(edge.startPoint.left, edge.startPoint.top, edge.endPoint.left, edge.endPoint.top);
            return edgeBound.isOverlapped(rect);
        });
    }
}
