import { Flow } from '$libs/flow';
import { DrawingInfo } from '$mono/workspace/drawing-info/drawing-info';
import { SizeF } from '$libs/graphics-geo/size';
import { Point } from '$libs/graphics-geo/point';

export const DEFAULT_FONT = "'Jetbrains Mono'";

export class DrawingInfoController {
    private readonly context: CanvasRenderingContext2D;
    private readonly drawingInfoMutableFlow = new Flow<DrawingInfo>(new DrawingInfo());
    readonly drawingInfoFlow: Flow<DrawingInfo> =
        this.drawingInfoMutableFlow.distinctUntilChanged();

    constructor(
        private readonly canvas: HTMLCanvasElement,
    ) {
        this.context = this.canvas.getContext('2d')!;
    }

    setFont = (fontSize: number) => {
        const currentInfo = this.drawingInfoMutableFlow.value!;
        const cellSizePx = getCellSizePx(this.context, currentInfo.font, fontSize);
        const cellCharOffsetPx = SizeF.of(0.0, cellSizePx.height * 0.2);
        this.drawingInfoMutableFlow.value = currentInfo.copyWith({
            offsetPx: adjustOffsetPx(currentInfo.offsetPx, currentInfo.cellSizePx, cellSizePx),
            cellSizePx,
            cellCharOffsetPx,
            font: `normal normal normal ${fontSize}px ${DEFAULT_FONT}`,
            fontSize,
        });
    };

    setSize = (widthPx: number, heightPx: number) => {
        this.drawingInfoMutableFlow.value = this.drawingInfoMutableFlow.value!.copyWith({
            canvasSizePx: SizeF.of(widthPx, heightPx),
        });
    };

    setOffset = (offsetPx: Point) => {
        this.drawingInfoMutableFlow.value = this.drawingInfoMutableFlow.value!.copyWith({
            offsetPx,
        });
    };
}

const getCellSizePx = (
    context: CanvasRenderingContext2D,
    font: string,
    fontSize: number,
): SizeF => {
    context.font = font;
    context.textAlign = 'left';
    context.textBaseline = 'middle';
    const cWidth = Math.floor(fontSize * 0.6275);
    const cHeight = Math.floor(fontSize * 1.314);
    return SizeF.of(cWidth, cHeight);
};

const adjustOffsetPx = (offsetPx: Point, oldSize: SizeF, newSize: SizeF): Point => {
    const left = Math.round((offsetPx.left * newSize.width) / oldSize.width);
    const top = Math.round((offsetPx.top * newSize.height) / oldSize.height);
    return Point.of(left, top);
};
