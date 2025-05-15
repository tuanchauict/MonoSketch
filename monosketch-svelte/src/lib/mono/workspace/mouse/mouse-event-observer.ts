import { Flow, LifecycleOwner } from '$libs/flow';
import { DrawingInfo } from '$mono/workspace/drawing-info';
import { ScrollMode } from '$mono/ui-state-manager/states';
import { Point, PointF } from '$libs/graphics-geo/point';
import {
    MousePointer,
    MousePointerType,
} from '$mono/workspace/mouse/mouse-pointer';

const SCROLL_SPEED_RATIO = 1 / 1.3;
const SCROLL_THRESHOLD_PIXEL = 1.0;

/**
 * A class to attach and observe mouse events happening on the canvas.
 * All events will be combined into a single flow `mousePointerFlow`.
 */
export class MouseEventObserver {
    private drawingInfo: DrawingInfo;
    private mouseWheelDeltaX: number = 0.0;
    private mouseWheelDeltaY: number = 0.0;

    private drawingOffsetPointPxMutableFlow = new Flow<Point>(Point.ZERO);
    readonly drawingOffsetPointPxFlow = this.drawingOffsetPointPxMutableFlow.immutable();

    private mousePointerMutableFlow = new Flow<MousePointer>(MousePointer.idle);
    readonly mousePointerFlow = this.mousePointerMutableFlow;

    private readonly mouseDoubleClickDetector = new MouseDoubleClickDetector();

    constructor(
        container: HTMLElement,
        private drawingInfoFlow: Flow<DrawingInfo>,
        private scrollModeFlow: Flow<ScrollMode>,
    ) {
        this.drawingInfo = drawingInfoFlow.value!;

        container.onmousedown = this.setMouseDownPointer;
        container.onmouseup = this.setMouseUpPointer;
        container.onmousemove = this.setMouseMovePointer;
        container.onwheel = this.setMouseWheel;
    }

    observeEvents(lifecycleOwner: LifecycleOwner, shiftKeyStateFlow: Flow<boolean>): void {
        this.drawingInfoFlow.observe(lifecycleOwner, (drawingInfo) => {
            this.drawingInfo = drawingInfo;
        });

        shiftKeyStateFlow.observe(lifecycleOwner, (isWithShiftKey) => {
            const currentValue = this.getCurrentMousePointer();
            if (currentValue.type !== MousePointerType.DRAG) {
                return;
            }
            this.mousePointerMutableFlow.value = {
                ...currentValue,
                isWithShiftKey,
            };
        });
    }

    forceUpdateOffset(offsetPx: Point): void {
        this.drawingOffsetPointPxMutableFlow.value = offsetPx;
    }

    private setMouseDownPointer = (event: MouseEvent) => {
        const currentValue = this.getCurrentMousePointer();
        if (
            currentValue.type === MousePointerType.IDLE ||
            currentValue.type === MousePointerType.MOVE
        ) {
            this.mousePointerMutableFlow.value = MousePointer.down(
                this.toBoardCoordinate(event),
                this.toPointPx(event),
                event.shiftKey,
            );
        }
    };

    private setMouseUpPointer = (event: MouseEvent) => {
        const isDoubleClick = this.mouseDoubleClickDetector.onMouseUp();
        const currentValue = this.getCurrentMousePointer();

        const nextValue = () => {
            switch (currentValue.type) {
                case MousePointerType.DOWN:
                    return MousePointer.up(
                        this.toBoardCoordinate(event),
                        this.toBoardCoordinateF(event),
                        currentValue.boardCoordinate,
                        event.shiftKey,
                    );
                case MousePointerType.DRAG:
                    return MousePointer.up(
                        this.toBoardCoordinate(event),
                        this.toBoardCoordinateF(event),
                        currentValue.mouseDownBoardCoordinate,
                        event.shiftKey,
                    );
                default:
                    return MousePointer.idle;
            }
        };
        this.mousePointerMutableFlow.value = nextValue();

        if (currentValue.type === MousePointerType.DOWN) {
            this.mousePointerMutableFlow.value = isDoubleClick
                ? MousePointer.doubleClick(this.toBoardCoordinate(event))
                : MousePointer.click(this.toBoardCoordinate(event), event.shiftKey);
        }

        this.mousePointerMutableFlow.value = MousePointer.idle;
    };

    private setMouseMovePointer = (event: MouseEvent) => {
        this.mouseDoubleClickDetector.reset();

        const currentValue = this.mousePointerMutableFlow.value ?? MousePointer.idle;
        const nextValue = () => {
            switch (currentValue.type) {
                case MousePointerType.DOWN:
                    const mouseBoardCoordinate = this.toBoardCoordinate(event);
                    // If the mouse is moved to a different cell, it's a drag.
                    // otherwise, it remains as a down (no change to the pointer to avoid triggering
                    // new event to listeners).
                    return !mouseBoardCoordinate.equals(currentValue.boardCoordinate)
                        ? MousePointer.drag(
                            this.toBoardCoordinate(event),
                            this.toBoardCoordinateF(event),
                            currentValue.boardCoordinate,
                            event.shiftKey,
                        )
                        : null;
                case MousePointerType.DRAG:
                    return MousePointer.drag(
                        this.toBoardCoordinate(event),
                        this.toBoardCoordinateF(event),
                        currentValue.mouseDownBoardCoordinate,
                        event.shiftKey,
                    );
                case MousePointerType.IDLE:
                case MousePointerType.MOVE:
                case MousePointerType.UP:
                case MousePointerType.CLICK:
                case MousePointerType.DOUBLE_CLICK:
                default:
                    return MousePointer.move(this.toBoardCoordinate(event), this.toPointPx(event));
            }
        };
        const nextMousePointer = nextValue();
        if (nextMousePointer) {
            this.mousePointerMutableFlow.value = nextMousePointer;
        }
    };

    private setMouseWheel = (event: WheelEvent) => {
        event.stopPropagation();
        event.preventDefault();

        const scrollHorizontalThresholdPx: number = SCROLL_THRESHOLD_PIXEL;
        const scrollVerticalThresholdPx: number = SCROLL_THRESHOLD_PIXEL;

        const [scrollDeltaX, scrollDeltaY]: [number, number] = this.getScrollDelta(event);

        const wheelDeltaLeft: number =
            (scrollDeltaX * SCROLL_SPEED_RATIO) / scrollHorizontalThresholdPx;
        const wheelDeltaTop: number =
            (scrollDeltaY * SCROLL_SPEED_RATIO) / scrollVerticalThresholdPx;
        const accumulateWheelDeltaLeft: number = this.mouseWheelDeltaX + wheelDeltaLeft;
        const accumulateWheelDeltaTop: number = this.mouseWheelDeltaY + wheelDeltaTop;

        const usableDeltaLeft: number = Math.floor(accumulateWheelDeltaLeft);
        const usableDeltaTop: number = Math.floor(accumulateWheelDeltaTop);

        if (usableDeltaLeft !== 0 || usableDeltaTop !== 0) {
            const offsetLeft =
                this.drawingInfo.offsetPx.left - usableDeltaLeft * scrollHorizontalThresholdPx;
            const offsetTop =
                this.drawingInfo.offsetPx.top - usableDeltaTop * scrollVerticalThresholdPx;
            this.drawingOffsetPointPxMutableFlow.value = Point.ofF(offsetLeft, offsetTop);
        }

        this.mouseWheelDeltaX = accumulateWheelDeltaLeft - usableDeltaLeft;
        this.mouseWheelDeltaY = accumulateWheelDeltaTop - usableDeltaTop;
    };

    private getCurrentMousePointer = (): MousePointer => this.mousePointerMutableFlow.value!;

    /**
     * Returns scroll delta x and y of the wheel event after adjusting with meta keys:
     * - When Alt/Option key is pressed, swap delta x and delta y (this is for those who use mouse)
     * - When Shift key is pressed, scroll mode other than [ScrollMode.BOTH] will be flipped
     *   (vertical -> horizontal, horizontal -> vertical)
     * - Returns the delta x and delta y of the wheel event according to adjusted scroll mode (with
     *   Shift key) and delta value (with Alt/Option key):
     *   - Vertical: delta x = 0
     *   - Horizontal: delta y = 0
     *   - Both: delta x and delta y = delta value
     */
    private getScrollDelta(wheelEvent: WheelEvent): [number, number] {
        const deltaX: number = wheelEvent.deltaX;
        const deltaY: number = wheelEvent.deltaY;
        const scrollDeltaX: number = wheelEvent.altKey ? deltaY : deltaX;
        const scrollDeltaY: number = wheelEvent.altKey ? deltaX : deltaY;

        const scrollMode: ScrollMode = this.getScrollMode(wheelEvent);

        switch (scrollMode) {
            case ScrollMode.BOTH:
                return [scrollDeltaX, scrollDeltaY];
            case ScrollMode.VERTICAL:
                return [0.0, scrollDeltaY];
            case ScrollMode.HORIZONTAL:
                return [scrollDeltaX, 0.0];
        }
    }

    private getScrollMode = (wheelEvent: WheelEvent): ScrollMode => {
        switch (this.scrollModeFlow.value) {
            case ScrollMode.BOTH:
                return this.scrollModeFlow.value;
            case ScrollMode.VERTICAL:
                return wheelEvent.shiftKey ? ScrollMode.HORIZONTAL : ScrollMode.VERTICAL;
            case ScrollMode.HORIZONTAL:
                return wheelEvent.shiftKey ? ScrollMode.VERTICAL : ScrollMode.HORIZONTAL;
            default:
                throw Error(`Unknown scroll mode: ${this.scrollModeFlow.value}`);
        }
    };

    private toBoardCoordinate = (mouseEvent: MouseEvent): Point =>
        new Point(
            this.drawingInfo.toBoardColumn(mouseEvent.offsetX),
            this.drawingInfo.toBoardRow(mouseEvent.offsetY),
        );

    private toBoardCoordinateF = (mouseEvent: MouseEvent): PointF =>
        new PointF(
            this.drawingInfo.toBoardColumnF(mouseEvent.offsetX),
            this.drawingInfo.toBoardRowF(mouseEvent.offsetY),
        );

    private toPointPx = (mouseEvent: MouseEvent): Point =>
        new Point(mouseEvent.offsetX, mouseEvent.offsetY);
}

/**
 * A class for detecting double click.
 * If two mouse ups happen within 500ms, it's a double click.
 */
class MouseDoubleClickDetector {
    private lastMouseUpMillis: number = 0;
    private mouseUpCount: number = 0;

    onMouseUp(): boolean {
        const currentTime = Date.now();
        const isDoubleClick = currentTime - this.lastMouseUpMillis < 500 && this.mouseUpCount > 0;
        this.lastMouseUpMillis = currentTime;
        this.mouseUpCount += 1;

        return isDoubleClick;
    }

    reset(): void {
        this.lastMouseUpMillis = 0;
        this.mouseUpCount = 0;
    }
}
