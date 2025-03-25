/*
 * Copyright (c) 2025, tuanchauict
 */

import type { Point } from "$libs/graphics-geo/point";
import type { Rect } from "$libs/graphics-geo/rect";
import { ChangeBound } from "$mono/shape/command/general-shape-commands";
import { type LineConnector, ShapeConnectorUseCase } from "$mono/shape/connector/line-connector";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Line } from "$mono/shape/shape/line";
import { LineAnchor } from "$mono/shape/shape/linehelper";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";

export function moveShapes(
    environment: CommandEnvironment,
    selectedShapes: AbstractShape[],
    isUpdateConfirmed: boolean,
    newPositionCalculator: (shape: AbstractShape) => Point | null,
) {
    const dataProvider = new MoveShapeDataProvider(
        environment,
        selectedShapes,
        newPositionCalculator,
    );

    // lineId -> headCount of selected shapes.
    const lineIdToHeadCountMap = dataProvider.allConnectors.reduce(
        (sum: Map<string, number>, connector: LineConnector) => {
            sum.set(
                connector.lineId,
                1 + (sum.get(connector.lineId) || 0),
            );
            return sum;
        },
        new Map<string, number>(),
    );

    const lineIdToTotalConnectorCount = dataProvider.selectedLines.reduce(
        (map: Map<string, number>, line: Line) => {
            map.set(line.id, countNumberOfConnections(environment, line.id));
            return map;
        },
        new Map<string, number>(),
    );

    const affectedLineIds = new Set<string>();
    // Move non line shapes.
    for (const shape of dataProvider.selectedNonLineShapes) {
        const newBound = dataProvider.updatePosition(shape);
        if (!newBound) continue;

        // Update line which has 1 head connected and the other head is not connected.
        for (const connector of environment.shapeManager.shapeConnector.getConnectors(shape)) {
            // Only update the line anchor if:
            // - the of the connector has only 1 head connected in selected shape
            // - the line is not selected or is selected but has 2 connectors
            // TODO: This logic is too complicated
            const totalConnectorCount = lineIdToTotalConnectorCount.get(connector.lineId) || 2;
            const shouldUpdateLineAnchor =
                (lineIdToHeadCountMap.get(connector.lineId) === 1) &&
                totalConnectorCount === 2;

            if (shouldUpdateLineAnchor) {
                affectedLineIds.add(connector.lineId);
                updateConnector(
                    environment,
                    connector,
                    newBound,
                    isUpdateConfirmed,
                );
            }
        }
    }

    const connectorFullLineIds = Array.from(lineIdToHeadCountMap.entries())
        .filter(([_, value]) => value === 2)
        .map(([key, _]) => key);

    const unaffectedLineIds = dataProvider.selectedLines
        .map(line => line.id)
        .filter(id => !affectedLineIds.has(id));

    const lineIds = [...new Set([...connectorFullLineIds, ...unaffectedLineIds])];

    for (const lineId of lineIds) {
        const line = environment.shapeManager.getShape(lineId) as Line | null;
        if (!line) continue;

        const oldBound = line.bound;
        const newBound = dataProvider.updatePosition(line);
        if (!newBound || newBound === oldBound) {
            continue;
        }

        // Remove connectors of line if its connected shapes are not selected.
        if (!lineIdToHeadCountMap.has(lineId)) {
            environment.shapeManager.shapeConnector.removeConnector(line, LineAnchor.START);
            environment.shapeManager.shapeConnector.removeConnector(line, LineAnchor.END);
        }
    }
}

/**
 * Update connectors of a shape after its bound is changed.
 * @param environment
 * @param target
 * @param newBound
 * @param isUpdateConfirmed
 * @returns The ids of the affected lines.
 */
export function updateConnectors(
    environment: CommandEnvironment,
    target: AbstractShape,
    newBound: Rect,
    isUpdateConfirmed: boolean,
): string[] {
    const connectors = environment.shapeManager.shapeConnector.getConnectors(target);
    for (const connector of connectors) {
        updateConnector(
            environment,
            connector,
            newBound,
            isUpdateConfirmed
        );
    }
    return connectors.map(connector => connector.lineId);
}

/**
 * Updates a connector with a new bound.
 */
function updateConnector(
    environment: CommandEnvironment,
    connector: LineConnector,
    newBound: Rect,
    isUpdateConfirmed: boolean,
): void {
    const line = environment.shapeManager.getShape(connector.lineId) as Line | null;
    if (!line) {
        return;
    }

    const anchorPointUpdate = {
        anchor: connector.anchor,
        point: ShapeConnectorUseCase.getPointInNewBound(connector, line.getDirection(connector.anchor), newBound),
    };

    line.moveAnchorPoint(
        anchorPointUpdate,
        isUpdateConfirmed,
        true, // justMoveAnchor
    );
}

/**
 * Returns number of connections of a line in the given environment.
 */
function countNumberOfConnections(environment: CommandEnvironment, lineId: string): number {
    const line = environment.shapeManager.getShape(lineId) as Line | null;
    if (!line) {
        return 0;
    }

    const hasStartConnector = environment.shapeManager.shapeConnector.hasConnector(line, LineAnchor.START);
    const hasEndConnector = environment.shapeManager.shapeConnector.hasConnector(line, LineAnchor.END);

    return (hasStartConnector ? 1 : 0) + (hasEndConnector ? 1 : 0);
}

/**
 * Helper class for providing data during shape movement.
 */
class MoveShapeDataProvider {
    public readonly selectedLines: Line[];
    public readonly selectedNonLineShapes: AbstractShape[];
    public readonly allConnectors: LineConnector[];

    constructor(
        private readonly environment: CommandEnvironment,
        selectedShapes: AbstractShape[],
        private readonly newPositionCalculator: (shape: AbstractShape) => Point | null,
    ) {
        const [lines, notLines] = this.partitionShapes(selectedShapes);
        this.selectedLines = lines;
        this.selectedNonLineShapes = notLines;

        this.allConnectors = notLines.flatMap(shape =>
            environment.shapeManager.shapeConnector.getConnectors(shape),
        );
    }

    /**
     * Partitions the shapes into lines and non-lines.
     */
    private partitionShapes(shapes: AbstractShape[]): [Line[], AbstractShape[]] {
        const lines: Line[] = [];
        const notLines: AbstractShape[] = [];

        for (const shape of shapes) {
            if (shape instanceof Line) {
                lines.push(shape);
            } else {
                notLines.push(shape);
            }
        }

        return [lines, notLines];
    }

    /**
     * Updates the position of a shape based on the new position calculator.
     */
    public updatePosition(shape: AbstractShape): Rect | null {
        const newPosition = this.newPositionCalculator(shape);
        if (!newPosition) {
            return null;
        }

        const newBound = shape.bound.copy({ position: newPosition });
        this.environment.shapeManager.execute(new ChangeBound(shape, newBound));
        return newBound;
    }
}
