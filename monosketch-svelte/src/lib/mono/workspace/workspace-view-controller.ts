import type { Point } from "$libs/graphics-geo/point";
import type { AppUiStateManager } from "$mono/ui-state-manager/app-ui-state-manager";
import { AxisCanvasViewController } from '$mono/workspace/canvas/axis-canvas-view-controller';
import { DrawingInfo, DrawingInfoController } from '$mono/workspace/drawing-info';
import { LifecycleOwner } from '$libs/flow';
import { WindowViewModel } from '$mono/window/window-viewmodel';
import { GridCanvasViewController } from '$mono/workspace/canvas/grid-canvas-view-controller';
import { InteractionCanvasViewController } from '$mono/workspace/canvas/interaction-canvas-view-controller';
import type { InteractionBound, InteractionPoint } from '$mono/shape/interaction-bound';
import { MouseEventObserver } from '$mono/workspace/mouse/mouse-event-observer';
import { MousePointerType } from '$mono/workspace/mouse/mouse-pointer';
import type { AppContext } from '$app/app-context';
import { KeyCommandType } from '$mono/keycommand';
import type { Rect } from '$libs/graphics-geo/rect';
import { SelectionCanvasViewController } from '$mono/workspace/canvas/selection-canvas-view-controller';
import { BoardCanvasViewController } from '$mono/workspace/canvas/board-canvas-view-controller';
import { MonoBoard } from '$mono/monobitmap/board/board';

/**
 * The main controller of the workspace view.
 */
export class WorkspaceViewController extends LifecycleOwner {
    private canvasViewController: CanvasViewController;
    private drawingInfoController: DrawingInfoController;

    constructor(
        private appContext: AppContext,
        private readonly container: HTMLDivElement,
        drawingInfoCanvas: HTMLCanvasElement,
        gridCanvas: HTMLCanvasElement,
        boardCanvas: HTMLCanvasElement,
        axisCanvas: HTMLCanvasElement,
        interactionCanvas: HTMLCanvasElement,
        selectionCanvas: HTMLCanvasElement,
    ) {
        super();
        this.drawingInfoController = new DrawingInfoController(drawingInfoCanvas);
        this.drawingInfoController.setFont(14);

        this.canvasViewController = new CanvasViewController(
            container,
            gridCanvas,
            boardCanvas,
            axisCanvas,
            interactionCanvas,
            selectionCanvas,
            this.appContext.appUiStateManager,
        );
    }

    protected onStartInternal() {
        WindowViewModel.windowSizeUpdateEventFlow.observe(this, () => {
            this.drawingInfoController.setSize(
                this.container.clientWidth,
                this.container.clientHeight,
            );
        });
        this.observeMouseInteractions();

        this.drawingInfoController.drawingInfoFlow?.observe(this, (drawingInfo) => {
            this?.canvasViewController.setDrawingInfo(drawingInfo);
        });
        this.appContext.appUiStateManager.themeModeFlow.observe(this, () => {
            this?.canvasViewController.fullyRedraw();
        });
        this.appContext.appUiStateManager.fontSizeFlow.observe(this, (fontSize) => {
            this.drawingInfoController.setFont(fontSize);
        });
        this.appContext.appUiStateManager.fontReadyFlow.observe(this, (fontReady) => {
            if (fontReady) {
                this.canvasViewController.fullyRedraw();
            }
        });

        this.canvasViewController.fullyRedraw();
    }

    private observeMouseInteractions = () => {
        const shiftKeyStateFlow = this.appContext.appUiStateManager.keyCommandFlow.map(
            (keyCommand) => keyCommand.command === KeyCommandType.SHIFT_KEY,
        );

        const mouseEventObserver = new MouseEventObserver(
            this,
            this.container,
            this.drawingInfoController.drawingInfoFlow,
            shiftKeyStateFlow,
            this.appContext.appUiStateManager.scrollModeFlow,
        );
        mouseEventObserver.mousePointerFlow.observe(this, (mousePointer) => {
            this?.canvasViewController?.setMouseMoving(mousePointer.type === MousePointerType.DRAG);
        });
        mouseEventObserver.drawingOffsetPointPxFlow.observe(
            this,
            this.drawingInfoController.setOffset,
        );
        // TODO: Read offset from the storage of the project and feed it to mouseEventObserver
    };
}

class CanvasViewController {
    private readonly axisCanvasViewController: AxisCanvasViewController;
    private readonly gridCanvasViewController: GridCanvasViewController;
    private readonly boardCanvasViewController: BoardCanvasViewController;
    private readonly interactionCanvasViewController: InteractionCanvasViewController;
    private readonly selectionCanvasViewController: SelectionCanvasViewController;

    constructor(
        private container: HTMLDivElement,
        gridCanvas: HTMLCanvasElement,
        boardCanvas: HTMLCanvasElement,
        axisCanvas: HTMLCanvasElement,
        interactionCanvas: HTMLCanvasElement,
        selectionCanvas: HTMLCanvasElement,
        appUiStateManager: AppUiStateManager,
    ) {
        this.axisCanvasViewController = new AxisCanvasViewController(axisCanvas, appUiStateManager);
        this.gridCanvasViewController = new GridCanvasViewController(gridCanvas, appUiStateManager);
        this.boardCanvasViewController = new BoardCanvasViewController(
            boardCanvas,
            new MonoBoard(),
            appUiStateManager,
        );
        this.interactionCanvasViewController = new InteractionCanvasViewController(
            interactionCanvas,
            appUiStateManager,
        );
        this.selectionCanvasViewController = new SelectionCanvasViewController(
            selectionCanvas,
            appUiStateManager,
        );
    }

    setDrawingInfo(drawInfo: DrawingInfo) {
        this.axisCanvasViewController.setDrawingInfo(drawInfo);
        this.gridCanvasViewController.setDrawingInfo(drawInfo);
        this.boardCanvasViewController.setDrawingInfo(drawInfo);
        this.interactionCanvasViewController.setDrawingInfo(drawInfo);
        this.selectionCanvasViewController.setDrawingInfo(drawInfo);
    }

    fullyRedraw = () => {
        this.axisCanvasViewController.draw();
        this.gridCanvasViewController.draw();
        this.boardCanvasViewController.draw();
        this.interactionCanvasViewController.draw();
        this.selectionCanvasViewController.draw();
    };

    drawInteractionBounds = (interactionBounds: InteractionBound[]) => {
        this.interactionCanvasViewController.setInteractionBounds(interactionBounds);
        this.interactionCanvasViewController.draw();
    };

    drawSelectionBound = (selectionBound?: Rect) => {
        this.selectionCanvasViewController.setSelectingBound(selectionBound);
        this.selectionCanvasViewController.draw();
    };

    getInteractionPoint = (pointPx: Point): InteractionPoint | null =>
        this.interactionCanvasViewController.getInteractionPoint(pointPx);

    setMouseMoving = (isMouseMoving: boolean) => {
        this.interactionCanvasViewController.setMouseMoving(isMouseMoving);
    };

    drawBoard = () => {
        this.boardCanvasViewController.draw();
        this.interactionCanvasViewController.draw();
        this.selectionCanvasViewController.draw();
    };
}
