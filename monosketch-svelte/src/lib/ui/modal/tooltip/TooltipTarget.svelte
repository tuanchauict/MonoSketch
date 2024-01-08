<script lang="ts">
import { modalViewModel } from '../viewmodel';
import { TargetBounds } from '../model';
import { Direction, Tooltip } from './model';
import { onDestroy } from 'svelte';

export let text: string;
export let direction: Direction = Direction.BOTTOM;
export let offsetHorizontal: number = 0;
export let offsetVertical: number = 0;

let timeoutId: number;
function showTooltip(e: MouseEvent) {
    const target = e.currentTarget as HTMLElement;
    const bounds = TargetBounds.fromElement(target);
    bounds.left -= offsetHorizontal;
    bounds.top -= offsetVertical;
    bounds.width += offsetHorizontal * 2;
    bounds.height += offsetVertical * 2;

    timeoutId = setTimeout(() => {
        modalViewModel.tooltipFlow.value = new Tooltip(text, direction, bounds);
    }, 600);
}

function hideTooltip() {
    clearTimeout(timeoutId);
    modalViewModel.tooltipFlow.value = null;
}

onDestroy(hideTooltip);
</script>

<div on:mouseover="{showTooltip}" on:mouseout="{hideTooltip}">
    <slot />
</div>

<style lang="scss">
div {
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
