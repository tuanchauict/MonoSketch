import { DrawingInfo } from '$mono/workspace/drawing-info';
import { Size } from '$libs/graphics-geo/size';

export class BaseCanvasViewController {
    protected readonly context: CanvasRenderingContext2D;

    protected drawingInfo: DrawingInfo;

    constructor(private readonly canvas: HTMLCanvasElement) {
        this.context = canvas.getContext('2d') as CanvasRenderingContext2D;
        this.drawingInfo = new DrawingInfo().copyWith({
            canvasSizePx: Size.of(canvas.width, canvas.height),
        });
    }

    setDrawingInfo = (drawingInfo: DrawingInfo) => {
        const canvasSizePx = drawingInfo.canvasSizePx;

        const isSizeChanged = !this.drawingInfo.canvasSizePx.equals(drawingInfo);
        if (isSizeChanged) {
            this.resizeCanvas(canvasSizePx.width, canvasSizePx.height);
        }
        this.drawingInfo = drawingInfo;

        if (isSizeChanged) {
            this.draw();
        }
    };

    draw = () => {
        this.context.clearRect(
            0,
            0,
            this.drawingInfo.canvasSizePx.width,
            this.drawingInfo.canvasSizePx.height,
        );
        this.context.font = this.drawingInfo.font;
        this.context.textAlign = 'left';
        this.context.textBaseline = 'top';
        this.context.imageSmoothingEnabled = true;

        this.drawInternal();
    };

    protected drawInternal() {}

    protected drawText = (text: string, row: number, column: number) => {
        const yPx = this.drawingInfo.toYPx(row);
        const xPx = this.drawingInfo.toXPx(column);
        this.context.fillText(
            text,
            xPx + this.drawingInfo.cellCharOffsetPx.width,
            yPx + this.drawingInfo.cellCharOffsetPx.height,
        );
    };

    protected addHLine = (path: Path2D, xPx: number, yPx: number, widthPx: number) => {
        path.moveTo(xPx, yPx);
        path.lineTo(xPx + widthPx, yPx);
    };

    protected addVLine = (path: Path2D, xPx: number, yPx: number, heightPx: number) => {
        path.moveTo(xPx, yPx);
        path.lineTo(xPx, yPx + heightPx);
    };

    protected resizeCanvas = (widthPx: number, heightPx: number) => {
        const dpr = Math.max(window.devicePixelRatio, 2.0);
        this.canvas.width = Math.trunc(widthPx * dpr);
        this.canvas.height = Math.trunc(heightPx * dpr);
        this.canvas.style.width = widthPx + 'px';
        this.canvas.style.height = heightPx + 'px';
        this.context.scale(dpr, dpr)
    };
}

