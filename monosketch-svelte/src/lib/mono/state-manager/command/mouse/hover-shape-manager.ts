import type { Point } from "$libs/graphics-geo/point";
import { getOrNull } from "$libs/sequence";
import { ShapeConnectorUseCase } from "$mono/shape/connector/line-connector";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";

/**
 * A short live-time class to manage hover shape.
 * This class is used to avoid searching for hover shape multiple times.
 */
export class HoverShapeManager {
    private pointToTargetMap: Map<Point, AbstractShape | null> = new Map();

    private constructor(
        private searcher: (environment: CommandEnvironment, point: Point) => AbstractShape | null,
    ) {
    }

    getHoverShape(environment: CommandEnvironment, point: Point): AbstractShape | null {
        return this.pointToTargetMap.get(point) ?? this.searchAndCache(environment, point);
    }

    private searchAndCache(environment: CommandEnvironment, point: Point): AbstractShape | null {
        const shape = this.searcher(environment, point);
        this.pointToTargetMap.set(point, shape);
        return shape;
    }

    resetCache(): void {
        this.pointToTargetMap.clear();
    }

    static forLineConnectHover(): HoverShapeManager {
        return new HoverShapeManager((environment, point) =>
            ShapeConnectorUseCase.getConnectableShape(point, environment.getShapes(point)),
        );
    }

    static forHoverShape(): HoverShapeManager {
        return new HoverShapeManager((environment, point) => {
            const shapes = Array.from(environment.getShapes(point));
            return getOrNull(shapes, shapes.length - 1);
        });
    }
}
