import { Rect } from "$libs/graphics-geo/rect";
import {
    InteractionPoint,
    LineInteractionPoint,
    ScaleInteractionPoint,
} from "$mono/shape-interaction-bound/interaction-point";
import { LineAnchor, LineEdge } from "$mono/shape/shape/linehelper";

/**
 * An interface to define all possible interaction bound types
 */
export interface InteractionBound {
    readonly interactionPoints: InteractionPoint[];
}

/**
 * A class which defines interaction bound for scalable shapes.
 */
export class ScalableInteractionBound implements InteractionBound {
    private constructor(
        public readonly interactionPoints: ScaleInteractionPoint.Base[],
        public readonly left: number,
        public readonly top: number,
        public readonly right: number,
        public readonly bottom: number,
    ) {
    }

    static of(targetedShapeId: string, shapeBound: Rect): ScalableInteractionBound {
        const left = shapeBound.left - 0.25;
        const top = shapeBound.top - 0.25;
        const right = shapeBound.right + 1.0 + 0.25;
        const bottom = shapeBound.bottom + 1.0 + 0.25;
        const horizontalMiddle = (left + right) / 2;
        const verticalMiddle = (top + bottom) / 2;

        const interactionPoints: ScaleInteractionPoint.Base[] = [
            new ScaleInteractionPoint.TopLeft(targetedShapeId, left, top),
            new ScaleInteractionPoint.TopMiddle(targetedShapeId, horizontalMiddle, top),
            new ScaleInteractionPoint.TopRight(targetedShapeId, right, top),
            new ScaleInteractionPoint.MiddleLeft(targetedShapeId, left, verticalMiddle),
            new ScaleInteractionPoint.MiddleRight(targetedShapeId, right, verticalMiddle),
            new ScaleInteractionPoint.BottomLeft(targetedShapeId, left, bottom),
            new ScaleInteractionPoint.BottomMiddle(targetedShapeId, horizontalMiddle, bottom),
            new ScaleInteractionPoint.BottomRight(targetedShapeId, right, bottom),
        ];
        return new ScalableInteractionBound(interactionPoints, left, top, right, bottom);
    }
}

/**
 * A class which defines interaction bound for lines.
 */
export class LineInteractionBound implements InteractionBound {
    private constructor(
        private readonly reducedEdge: LineEdge[],
        readonly interactionPoints: LineInteractionPoint.Base[],
    ) {
    }

    static of(targetedShapeId: string, edges: LineEdge[]): LineInteractionBound {
        const noIdenticalPointsEdges = edges.filter(edge => edge.startPoint !== edge.endPoint);
        const reducedEdges = noIdenticalPointsEdges.length > 0 ? noIdenticalPointsEdges : [edges[0]];

        const anchorPoints = [
            this.createInteractionAnchor(targetedShapeId, reducedEdges, LineAnchor.START),
            this.createInteractionAnchor(targetedShapeId, reducedEdges, LineAnchor.END),
        ];

        const middleEdgePoints = reducedEdges.map(edge => {
            if (edge.startPoint === edge.endPoint) {
                return null;
            }
            return new LineInteractionPoint.Edge(
                targetedShapeId,
                edge.id,
                edge.middleLeft + 0.5,
                edge.middleTop + 0.5,
                edge.isHorizontal,
            );
        }).filter(point => point !== null) as LineInteractionPoint.Edge[];

        const interactionPoints = [...anchorPoints, ...middleEdgePoints];
        return new LineInteractionBound(reducedEdges, interactionPoints);
    }

    private static createInteractionAnchor(
        targetedShapeId: string,
        reducedEdges: LineEdge[],
        anchor: LineAnchor,
    ): LineInteractionPoint.Anchor {
        const edge = anchor === LineAnchor.START ? reducedEdges[0] : reducedEdges[reducedEdges.length - 1];
        const [point, anotherPoint] = anchor === LineAnchor.START ? [edge.startPoint, edge.endPoint] : [edge.endPoint, edge.startPoint];

        const horizontalOffset =
            !edge.isHorizontal
                ? 0.5
                : (point === anotherPoint
                    ? (anchor === LineAnchor.START ? 0.0 : 1.0)
                    : (point.left < anotherPoint.left ? 0.0 : 1.0));
        const verticalOffset = edge.isHorizontal ? 0.5 : (point.top <= anotherPoint.top ? 0.0 : 1.0);

        return new LineInteractionPoint.Anchor(
            targetedShapeId,
            anchor,
            point.left + horizontalOffset,
            point.top + verticalOffset,
        );
    }
}
