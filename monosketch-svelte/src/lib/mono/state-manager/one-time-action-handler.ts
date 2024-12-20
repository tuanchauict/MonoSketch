/*
 * Copyright (c) 2024, tuanchauict
 */

import { Flow, LifecycleOwner } from "$libs/flow";
import { singleOrNull } from "$libs/sequence";
import type { OneTimeActionType } from "$mono/action-manager/one-time-actions";
import type { MonoBitmapManager } from "$mono/monobitmap/manager/mono-bitmap-manager";
import type { ChangeOrderType } from "$mono/shape/command/shape-manager-commands";
import { MakeTextEditable, UpdateTextEditingMode } from "$mono/shape/command/text-commands";
import { type TextHorizontalAlign, TextVerticalAlign } from "$mono/shape/extra/style";
import type { ShapeClipboardManager } from "$mono/shape/shape-clipboard-manager";
import type { AbstractShape } from "$mono/shape/shape/abstract-shape";
import { Text } from "$mono/shape/shape/text";
import { ClipboardManager } from "$mono/state-manager/clipboard-manager";
import type { CommandEnvironment } from "$mono/state-manager/command-environment";
import { FileRelatedActionsHelper } from "$mono/state-manager/onetimeaction/file-related-action-helper";
import type { StateHistoryManager } from "$mono/state-manager/state-history-manager";
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
            // TODO: Uncomment this code after implementing EditTextShapeHelper
            // EditTextShapeHelper.showEditTextDialog(this.environment, shape, false, () => {
            //     this.environment.shapeManager.execute(new UpdateTextEditingMode(shape, false));
            //     this.environment.exitEditingMode(oldText !== shape.text);
            // });
        }
    }

    private setTextAlignment(newHorizontalAlign: TextHorizontalAlign, newVerticalAlign: TextVerticalAlign) {

    }

    private moveSelectedShapes(offsetRow: number, offsetCol: number) {

    }

    private setSelectedShapeBound(newLeft: number, newTop: number, newWidth: number, newHeight: number) {

    }

    private setSelectedShapeFillExtra(isEnabled: boolean, newFillStyleId: string | null) {

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

    }

    private setSelectedShapeStartAnchorExtra(isEnabled: boolean, newHeadId: string | null) {

    }

    private setSelectedShapeEndAnchorExtra(isEnabled: boolean, newHeadId: string | null) {

    }

    private changeShapeOrder(orderType: ChangeOrderType) {

    }
}
