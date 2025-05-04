/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow, LifecycleOwner } from "$libs/flow";
import { Point } from "$libs/graphics-geo/point";
import { Rect } from "$libs/graphics-geo/rect";
import { singleOrNull } from "$libs/sequence";
import type { OneTimeActionType } from "$mono/action-manager/one-time-actions";
import type { MonoBitmapManager } from "$mono/monobitmap/manager/mono-bitmap-manager";
import { ChangeBound, ChangeExtra } from "$mono/shape/command/general-shape-commands";
import { ChangeOrder, type ChangeOrderType } from "$mono/shape/command/shape-manager-commands";
import { MakeTextEditable, UpdateTextEditingMode } from "$mono/shape/command/text-commands";
import { ShapeExtraManager } from "$mono/shape/extra/extra-manager";
import { TextAlign, type TextHorizontalAlign, TextVerticalAlign } from "$mono/shape/extra/style";
import type { ShapeClipboardManager } from "$mono/shape/shape-clipboard-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import type { Line } from "$mono/shape/shape/line";
import { Rectangle } from "$mono/shape/shape/rectangle";
import { Text } from "$mono/shape/shape/text";
import { ClipboardManager } from "$mono/state-manager/clipboard-manager";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { EditTextShapeHelper } from "$mono/state-manager/command/text/edit-text-shape-helper";
import { FileRelatedActionsHelper } from "$mono/state-manager/onetimeaction/file-related-action-helper";
import type { StateHistoryManager } from "$mono/state-manager/state-history-manager";
import { moveShapes, updateConnectors } from "$mono/state-manager/utils/UpdateShapeBoundHelper";
import type { WorkspaceDao } from "$mono/store-manager/dao/workspace-dao";
import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";

/**
 * A class to handle one time actions.
 */
export class OneTimeActionHandler {
    private readonly fileRelatedActionsHelper: FileRelatedActionsHelper;
    private readonly clipboardManager: ClipboardManager;

    constructor(
        private readonly environment: CommandEnvironment,
        private readonly bitmapManager: MonoBitmapManager,
        private readonly shapeClipboardManager: ShapeClipboardManager,
        private readonly stateHistoryManager: StateHistoryManager,
        private readonly uiStateManger: AppUiStateManager,
        private readonly workspaceDao: WorkspaceDao,
    ) {
        this.fileRelatedActionsHelper = new FileRelatedActionsHelper(
            environment,
            bitmapManager,
            shapeClipboardManager,
            workspaceDao,
        );
        this.clipboardManager = new ClipboardManager(environment, shapeClipboardManager);
    }

    observe(lifecycleOwner: LifecycleOwner, oneTimeActionFlow: Flow<OneTimeActionType>) {
        oneTimeActionFlow.observe(lifecycleOwner, (action) => {
            switch (action.type) {
                case "Idle":
                    break;

                case "RenameCurrentProject":
                case "NewProject":
                case "ExportSelectedShapes":
                case "SwitchProject":
                case "RemoveProject":
                case "SaveShapesAs":
                case "OpenShapes":
                    this.fileRelatedActionsHelper.handleProjectAction(action);
                    break;

                // ---------
                case "CopyText":
                    this.fileRelatedActionsHelper.exportSelectedShapes(false);
                    break;
                // ---------
                case "SelectAllShapes":
                    this.environment.selectAllShapes();
                    break;

                case "DeselectShapes":
                    this.environment.clearSelectedShapes();
                    break;

                case "DeleteSelectedShapes":
                    this.deleteSelectedShapes();
                    break;

                case "EditSelectedShapes":
                    this.editSelectedShape(singleOrNull(this.environment.getSelectedShapes()));
                    break;

                case "EditSelectedShape":
                    this.editSelectedShape(action.shape);
                    break;

                case "TextAlignment":
                    this.setTextAlignment(action.newHorizontalAlign, action.newVerticalAlign);
                    break;
                // ---------
                case "MoveShapes":
                    this.moveSelectedShapes(action.offsetRow, action.offsetCol);
                    break;

                case "ChangeShapeBound":
                    this.setSelectedShapeBound(action.newLeft, action.newTop, action.newWidth, action.newHeight);
                    break;
                // ---------
                case "ChangeShapeFillExtra":
                    this.setSelectedShapeFillExtra(action.isEnabled, action.newFillStyleId);
                    break;

                case "ChangeShapeBorderExtra":
                    this.setSelectedShapeBorderExtra(action.isEnabled, action.newBorderStyleId);
                    break;

                case "ChangeShapeBorderDashPatternExtra":
                    this.setSelectedShapeBorderDashPatternExtra(action.dash, action.gap, action.offset);
                    break;

                case "ChangeShapeBorderCornerExtra":
                    this.setSelectedShapeBorderCornerExtra(action.isRoundedCorner);
                    break;

                case "ChangeLineStrokeExtra":
                    this.setSelectedLineStrokeExtra(action.isEnabled, action.newStrokeStyleId);
                    break;

                case "ChangeLineStrokeDashPatternExtra":
                    this.setSelectedLineStrokeDashPattern(action.dash, action.gap, action.offset);
                    break;

                case "ChangeLineStrokeCornerExtra":
                    this.setSelectedLineStrokeCornerExtra(action.isRoundedCorner);
                    break;

                case "ChangeLineStartAnchorExtra":
                    this.setSelectedShapeStartAnchorExtra(action.isEnabled, action.newHeadId);
                    break;

                case "ChangeLineEndAnchorExtra":
                    this.setSelectedShapeEndAnchorExtra(action.isEnabled, action.newHeadId);
                    break;
                // ---------
                case "ReorderShape":
                    this.changeShapeOrder(action.orderType);
                    break;
                // ---------
                case "Copy":
                    this.clipboardManager.copySelectedShapes(action.isRemoveRequired);
                    break;

                case "Duplicate":
                    this.clipboardManager.duplicateSelectedShapes();
                    break;
                // ---------
                case "Undo":
                    this.stateHistoryManager.undo();
                    break;

                case "Redo":
                    this.stateHistoryManager.redo();
                    break;
            }
        });
    }

    private deleteSelectedShapes() {
        const environment = this.environment;
        for (const shape of environment.getSelectedShapes()) {
            environment.removeShape(shape);
            // TODO: Add removeConnectorsOfShape() to CommandEnvironment
            environment.shapeManager.shapeConnector.removeShape(shape);
        }
        environment.clearSelectedShapes();
    }

    private editSelectedShape(shape: AbstractShape | null) {
        if (shape instanceof Text) {
            this.environment.enterEditingMode();
            const oldText = shape.text;
            this.environment.shapeManager.execute(new MakeTextEditable(shape));
            this.environment.shapeManager.execute(new UpdateTextEditingMode(shape, true));

            console.log("Edit text shape", oldText);
            EditTextShapeHelper.showEditTextDialog(this.environment, shape, false, () => {
                this.environment.shapeManager.execute(new UpdateTextEditingMode(shape, false));
                this.environment.exitEditingMode(oldText !== shape.text);
            });
        }
    }

    private setTextAlignment(newHorizontalAlign: TextHorizontalAlign, newVerticalAlign: TextVerticalAlign) {
        const textShape = this.singleSelectedShape() as Text | null;
        if (textShape === null) {
            ShapeExtraManager.setDefaultValues({
                textHorizontalAlign: newHorizontalAlign,
                textVerticalAlign: newVerticalAlign,
            });
            return;
        }
        const extra = textShape.extra;
        const newTextAlign = new TextAlign(
            newHorizontalAlign ?? extra.textAlign.horizontalAlign,
            newVerticalAlign ?? extra.textAlign.verticalAlign,
        );
        const newExtra = extra.copy({ textAlign: newTextAlign });
        this.environment.shapeManager.execute(new ChangeExtra(textShape, newExtra));
    }

    private moveSelectedShapes(offsetRow: number, offsetCol: number) {
        const offset = Point.of(offsetCol, offsetRow);
        moveShapes(
            this.environment,
            Array.from(this.environment.getSelectedShapes()),
            true,
            (shape) => shape.bound.position.plus(offset),
        );
        this.environment.updateInteractionBounds();
    }

    private setSelectedShapeBound(newLeft: number, newTop: number, newWidth: number, newHeight: number) {
        const singleShape = this.singleSelectedShape();
        if (singleShape === null) {
            return;
        }
        const newBound = Rect.byLTWH(
            newLeft ?? singleShape.bound.left,
            newTop ?? singleShape.bound.top,
            newWidth ?? singleShape.bound.width,
            newHeight ?? singleShape.bound.height,
        );
        updateConnectors(this.environment, singleShape, newBound, true);
        this.environment.shapeManager.execute(new ChangeBound(singleShape, newBound));
        this.environment.updateInteractionBounds();
    }

    private setSelectedShapeFillExtra(isEnabled: boolean, newFillStyleId: string | null) {
        const singleShape = this.singleSelectedShape();
        if (singleShape === null) {
            ShapeExtraManager.setDefaultValues({ isFillEnabled: isEnabled, fillStyleId: newFillStyleId ?? undefined });
            return;
        }

        const currentRectangleExtra = singleShape instanceof Text ? singleShape.extra.boundExtra :
            (singleShape instanceof Rectangle ? singleShape.extra : null);
        if (currentRectangleExtra === null) {
            return;
        }

        const newIsFillEnabled = isEnabled ?? currentRectangleExtra.isFillEnabled;
        const newFillStyle = ShapeExtraManager.getRectangleFillStyle(
            newFillStyleId ?? undefined,
            currentRectangleExtra.userSelectedFillStyle,
        );
        const rectangleExtra = currentRectangleExtra.copy({
            isFillEnabled: newIsFillEnabled,
            userSelectedFillStyle: newFillStyle,
        });
        const newExtra =
            singleShape instanceof Text ? singleShape.extra.copy({ boundExtra: rectangleExtra }) : rectangleExtra;
        this.environment.shapeManager.execute(new ChangeExtra(singleShape, newExtra));
    }

    private setSelectedShapeBorderExtra(isEnabled: boolean, newBorderStyleId: string | null) {

    }

    private setSelectedShapeBorderDashPatternExtra(dash: number, gap: number, offset: number) {

    }

    private setSelectedShapeBorderCornerExtra(isRoundedCorner: boolean) {

    }

    private setSelectedLineStrokeExtra(isEnabled: boolean, newStrokeStyleId: string | null) {

    }

    private setSelectedLineStrokeDashPattern(dash: number, gap: number, offset: number) {

    }

    private setSelectedLineStrokeCornerExtra(isRoundedCorner: boolean) {
        const line = this.singleSelectedShape() as Line | null;
        if (line === null) {
            ShapeExtraManager.setDefaultValues({ isLineStrokeRoundedCorner: isRoundedCorner });
            return;
        }
        const currentExtra = line.extra;
        const newExtra = currentExtra.copy({ isRoundedCorner: isRoundedCorner });
        this.environment.shapeManager.execute(new ChangeExtra(line, newExtra));
    }

    private setSelectedShapeStartAnchorExtra(isEnabled: boolean, newHeadId: string | null) {
        const line = this.singleSelectedShape() as Line | null;
        if (line === null) {
            ShapeExtraManager.setDefaultValues({
                isStartHeadAnchorCharEnabled: isEnabled,
                startHeadAnchorCharId: newHeadId ?? undefined,
            });
            return;
        }
        const currentExtra = line.extra;
        const newAnchor = ShapeExtraManager.getStartHeadAnchorChar(
            newHeadId ?? undefined,
            currentExtra.userSelectedEndAnchor,
        );
        const newExtra = currentExtra.copy({
            isStartAnchorEnabled: isEnabled ?? currentExtra.isEndAnchorEnabled,
            userSelectedStartAnchor: newAnchor,
        });
        this.environment.shapeManager.execute(new ChangeExtra(line, newExtra));
    }

    private setSelectedShapeEndAnchorExtra(isEnabled: boolean, newHeadId: string | null) {
        const line = this.singleSelectedShape() as Line | null;
        if (line === null) {
            ShapeExtraManager.setDefaultValues({
                isEndHeadAnchorCharEnabled: isEnabled,
                endHeadAnchorCharId: newHeadId ?? undefined,
            });
            return;
        }
        const currentExtra = line.extra;
        const newAnchor = ShapeExtraManager.getEndHeadAnchorChar(
            newHeadId ?? undefined,
            currentExtra.userSelectedEndAnchor,
        );
        const newExtra = currentExtra.copy({
            isEndAnchorEnabled: isEnabled ?? currentExtra.isEndAnchorEnabled,
            userSelectedEndAnchor: newAnchor,
        });
        this.environment.shapeManager.execute(new ChangeExtra(line, newExtra));
    }

    private changeShapeOrder(orderType: ChangeOrderType) {
        const singleSelectedShape = this.singleSelectedShape();
        if (singleSelectedShape !== null) {
            this.environment.shapeManager.execute(new ChangeOrder(singleSelectedShape, orderType));
        }
    }

    private singleSelectedShape(): AbstractShape | null {
        return singleOrNull(this.environment.getSelectedShapes());
    }
}
