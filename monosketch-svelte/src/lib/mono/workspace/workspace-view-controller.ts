import { AxisCanvasViewController } from '$mono/workspace/canvas/axis-canvas-view-controller';
import { ThemeManager } from '$mono/ui-state-manager/theme-manager';
import { DrawingInfo, DrawingInfoController } from '$mono/workspace/drawing-info';
import { LifecycleOwner } from '$libs/flow';
import { WindowViewModel } from '$mono/window/window-viewmodel';

export class WorkspaceViewController extends LifecycleOwner {
    private canvasViewController?: CanvasViewController;
    private drawingInfoController?: DrawingInfoController;
    private themeManager: ThemeManager = ThemeManager.getInstance();

    constructor(
        private readonly container: HTMLDivElement,
        drawingInfoCanvas: HTMLCanvasElement,
        axisCanvas: HTMLCanvasElement,
    ) {
        super();
        this.drawingInfoController = new DrawingInfoController(drawingInfoCanvas);

        this.drawingInfoController.setFont(14);

        this.canvasViewController = new CanvasViewController(
            container,
            axisCanvas,
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
    }
}

class CanvasViewController {
    private readonly axisCanvasViewController: AxisCanvasViewController;

    constructor(
        private container: HTMLDivElement,
        axisCanvas: HTMLCanvasElement,
        themeManager: ThemeManager,
    ) {
        this.axisCanvasViewController = new AxisCanvasViewController(axisCanvas, themeManager);
    }

    setDrawingInfo(drawInfo: DrawingInfo) {
        this.axisCanvasViewController.setDrawingInfo(drawInfo);
    }

    fullyRedraw = () => {
        this.axisCanvasViewController.draw();
    };

    drawBoard = () => {
        // TODO: draw board
    };
}
