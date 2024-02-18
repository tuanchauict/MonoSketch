import { AxisCanvasViewController } from '$mono/workspace/canvas/axis-canvas-view-controller';
import { ThemeManager } from '$mono/ui-state-manager/theme-manager';
import { DrawingInfo, DrawingInfoController } from '$mono/workspace/drawing-info';
import { LifecycleOwner } from '$libs/flow';
import { WindowViewModel } from '$mono/window/window-viewmodel';
import { GridCanvasViewController } from '$mono/workspace/canvas/grid-canvas-view-controller';
import { InteractionCanvasViewController } from '$mono/workspace/canvas/interaction-canvas-view-controller';
import type { InteractionBound } from '$mono/shape/interaction-bound';

export class WorkspaceViewController extends LifecycleOwner {
    private canvasViewController?: CanvasViewController;
    private drawingInfoController?: DrawingInfoController;
    private themeManager: ThemeManager = ThemeManager.getInstance();

    constructor(
        private readonly container: HTMLDivElement,
        drawingInfoCanvas: HTMLCanvasElement,
        gridCanvas: HTMLCanvasElement,
        axisCanvas: HTMLCanvasElement,
        interactionCanvas: HTMLCanvasElement,
    ) {
        super();
        this.drawingInfoController = new DrawingInfoController(drawingInfoCanvas);

        this.drawingInfoController.setFont(14);

        this.canvasViewController = new CanvasViewController(
            container,
            gridCanvas,
            axisCanvas,
            interactionCanvas,
            this.themeManager,
        );
    }

    protected onStartInternal() {
        WindowViewModel.windowSizeUpdateEventFlow.observe(this, () => {
            this.drawingInfoController?.setSize(
                this.container.clientWidth,
                this.container.clientHeight,
            );
        });
        this.drawingInfoController?.drawingInfoFlow?.observe(this, (drawingInfo) => {
            this?.canvasViewController?.setDrawingInfo(drawingInfo);
        });
        this.themeManager.themeModeFlow.observe(this, () => {
            this?.canvasViewController?.fullyRedraw();
        });
        this.canvasViewController?.fullyRedraw();

        // TODO: observe mouse moving event and set mouse moving state to canvas view controller
    }
}

class CanvasViewController {
    private readonly axisCanvasViewController: AxisCanvasViewController;
    private readonly gridCanvasViewController: GridCanvasViewController;
    private readonly interactionCanvasViewController: InteractionCanvasViewController;

    constructor(
        private container: HTMLDivElement,
        gridCanvas: HTMLCanvasElement,
        axisCanvas: HTMLCanvasElement,
        interactionCanvas: HTMLCanvasElement,
        themeManager: ThemeManager,
    ) {
        this.axisCanvasViewController = new AxisCanvasViewController(axisCanvas, themeManager);
        this.gridCanvasViewController = new GridCanvasViewController(gridCanvas, themeManager);
        this.interactionCanvasViewController = new InteractionCanvasViewController(
            interactionCanvas,
            themeManager,
        );
    }

    setDrawingInfo(drawInfo: DrawingInfo) {
        this.axisCanvasViewController.setDrawingInfo(drawInfo);
        this.gridCanvasViewController.setDrawingInfo(drawInfo);
        this.interactionCanvasViewController.setDrawingInfo(drawInfo);
    }

    fullyRedraw = () => {
        this.axisCanvasViewController.draw();
        this.gridCanvasViewController.draw();
        this.interactionCanvasViewController.draw();
    };

    drawInteractionBounds = (interactionBounds: InteractionBound[]) => {
        this.interactionCanvasViewController.setInteractionBounds(interactionBounds);
        this.interactionCanvasViewController.draw();
    }

    setMouseMoving = (isMouseMoving: boolean) => {
        this.interactionCanvasViewController.setMouseMoving(isMouseMoving);
    };

    drawBoard = () => {
        // TODO: draw board
    };
}
