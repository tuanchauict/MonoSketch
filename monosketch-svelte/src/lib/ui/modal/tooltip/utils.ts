import { TargetBounds } from '../model';
import { TooltipDirection } from './model';
import type { Rect } from '$libs/graphics-geo/rect';

export function calcArrowLeft(arrow: HTMLElement, direction: TooltipDirection, targetBounds: Rect): number {
    if (!arrow) {
        return -1000;
    }
    const arrowBounds = TargetBounds.fromElement(arrow);
    switch (direction) {
        case TooltipDirection.TOP:
            return TargetBounds.centerHorizontal(targetBounds) - arrowBounds.width / 2;
        case TooltipDirection.BOTTOM:
            return TargetBounds.centerHorizontal(targetBounds) - arrowBounds.width / 2;
        case TooltipDirection.LEFT:
            return targetBounds.top + (targetBounds.height - arrowBounds.height) / 2;
        case TooltipDirection.RIGHT:
            return targetBounds.top + (targetBounds.height - arrowBounds.height) / 2;
    }
}

export function calcArrowTop(arrow: HTMLElement, direction: TooltipDirection, targetBounds: Rect): number {
    if (!arrow) {
        return -1000;
    }
    const arrowBounds = TargetBounds.fromElement(arrow);
    switch (direction) {
        case TooltipDirection.TOP:
            return targetBounds.top - arrowBounds.height;
        case TooltipDirection.BOTTOM:
            return targetBounds.top + targetBounds.height;
        case TooltipDirection.LEFT:
            return targetBounds.top + (targetBounds.height - arrowBounds.height) / 2;
        case TooltipDirection.RIGHT:
            return targetBounds.top + (targetBounds.height - arrowBounds.height) / 2;
    }
}

export function calcBodyLeft(body: HTMLElement, arrow: HTMLElement, direction: TooltipDirection, targetBounds: Rect): number {
    if (!body || !arrow) {
        return -1000;
    }
    const bodyBounds = TargetBounds.fromElement(body);
    const arrowBounds = TargetBounds.fromElement(arrow);
    switch (direction) {
        case TooltipDirection.TOP:
            return adjustHorizontalPosition(
                TargetBounds.centerHorizontal(targetBounds) - bodyBounds.width / 2,
                bodyBounds,
            );
        case TooltipDirection.BOTTOM:
            return adjustHorizontalPosition(
                TargetBounds.centerHorizontal(targetBounds) - bodyBounds.width / 2,
                bodyBounds,
            );
        case TooltipDirection.LEFT:
            return targetBounds.left - bodyBounds.width - arrowBounds.width;
        case TooltipDirection.RIGHT:
            return targetBounds.left + targetBounds.width + arrowBounds.width;
    }
}

export function calcBodyTop(body: HTMLElement, arrow: HTMLElement, direction: TooltipDirection, targetBounds: Rect): number {
    if (!body || !arrow) {
        return -1000;
    }
    const bodyBounds = TargetBounds.fromElement(body);
    const arrowBounds = TargetBounds.fromElement(arrow);
    switch (direction) {
        case TooltipDirection.TOP:
            return targetBounds.top - bodyBounds.height - arrowBounds.height;
        case TooltipDirection.BOTTOM:
            return targetBounds.top + targetBounds.height + arrowBounds.height;
        case TooltipDirection.LEFT:
            return TargetBounds.centerVertical(targetBounds) - bodyBounds.height / 2;
        case TooltipDirection.RIGHT:
            return TargetBounds.centerVertical(targetBounds) - bodyBounds.height / 2;
    }
}

function adjustHorizontalPosition(leftPx: number, bodyBounds: Rect): number {
    const bodyRightPx = leftPx + bodyBounds.width;
    if (bodyRightPx > document.body.clientWidth) {
        return document.body.clientWidth - bodyBounds.width - 4;
    }
    if (leftPx < 0) {
        return 4;
    }
    return leftPx;
}
