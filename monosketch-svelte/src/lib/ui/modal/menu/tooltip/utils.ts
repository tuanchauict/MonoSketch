import { TargetBounds } from '../../model';
import { Direction, type Tooltip } from './model';

export function calcArrowLeft(arrow: HTMLElement, target: Tooltip): number {
    if (!arrow) {
        return -1000;
    }
    const arrowBounds = TargetBounds.fromElement(arrow);
    const targetBounds = target.targetBounds;
    switch (target.direction) {
        case Direction.TOP:
            return targetBounds.top + targetBounds.height;
        case Direction.BOTTOM:
            return targetBounds.centerHorizontal - arrowBounds.width / 2;
        case Direction.LEFT:
            return targetBounds.top + (targetBounds.height - arrowBounds.height) / 2;
        case Direction.RIGHT:
            return targetBounds.top + (targetBounds.height - arrowBounds.height) / 2;
    }
}

export function calcArrowTop(arrow: HTMLElement, target: Tooltip): number {
    if (!arrow) {
        return -1000;
    }
    const arrowBounds = TargetBounds.fromElement(arrow);
    const targetBounds = target.targetBounds;
    switch (target.direction) {
        case Direction.TOP:
            return targetBounds.top - arrowBounds.height;
        case Direction.BOTTOM:
            return targetBounds.top + targetBounds.height;
        case Direction.LEFT:
            return targetBounds.top + (targetBounds.height - arrowBounds.height) / 2;
        case Direction.RIGHT:
            return targetBounds.top + (targetBounds.height - arrowBounds.height) / 2;
    }
}

export function calcBodyLeft(body: HTMLElement, arrow: HTMLElement, target: Tooltip): number {
    if (!body || !arrow) {
        return -1000;
    }
    const bodyBounds = TargetBounds.fromElement(body);
    const arrowBounds = TargetBounds.fromElement(arrow);
    const targetBounds = target.targetBounds;
    switch (target.direction) {
        case Direction.TOP:
            return targetBounds.centerHorizontal - bodyBounds.width / 2;
        case Direction.BOTTOM:
            return targetBounds.centerHorizontal - bodyBounds.width / 2;
        case Direction.LEFT:
            return targetBounds.left - bodyBounds.width - arrowBounds.width;
        case Direction.RIGHT:
            return targetBounds.left + targetBounds.width + arrowBounds.width;
    }
}

export function calcBodyTop(body: HTMLElement, arrow: HTMLElement, target: Tooltip): number {
    if (!body || !arrow) {
        return -1000;
    }
    const bodyBounds = TargetBounds.fromElement(body);
    const arrowBounds = TargetBounds.fromElement(arrow);
    const targetBounds = target.targetBounds;
    switch (target.direction) {
        case Direction.TOP:
            return targetBounds.top - bodyBounds.height - arrowBounds.height;
        case Direction.BOTTOM:
            return targetBounds.top + targetBounds.height + arrowBounds.height;
        case Direction.LEFT:
            return targetBounds.centerVertical - bodyBounds.height / 2;
        case Direction.RIGHT:
            return targetBounds.centerVertical - bodyBounds.height / 2;
    }
}
