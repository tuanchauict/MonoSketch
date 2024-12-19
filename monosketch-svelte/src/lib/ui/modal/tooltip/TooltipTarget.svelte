<script lang="ts">
import { modalViewModel } from '../viewmodel';
import { TargetBounds } from '../model';
import { Direction } from './model';
import { onDestroy } from 'svelte';

export let text: string;
export let direction: Direction = Direction.BOTTOM;
export let offsetHorizontal: number = 0;
export let offsetVertical: number = 0;

export let style: string = '';

let timeoutId: number;

function showTooltip(e: MouseEvent) {
    const target = e.currentTarget as HTMLElement;
    const targetBounds = TargetBounds.expandTargetBounds(
        TargetBounds.fromElement(target),
        offsetHorizontal,
        offsetVertical,
    );

    timeoutId = setTimeout(() => {
        modalViewModel.tooltipFlow.value = {
            text,
            direction,
            targetBounds,
        };
    }, 600);
}

function hideTooltip() {
    clearTimeout(timeoutId);
    modalViewModel.tooltipFlow.value = null;
}

onDestroy(hideTooltip);
</script>

<div
    role="button"
    tabindex="-1"
    style="{style}"
    on:mouseover="{showTooltip}"
    on:mouseout="{hideTooltip}"
    on:focus="{() => {}}"
    on:blur="{hideTooltip}"
>
    <slot/>
</div>

<style lang="scss">
div {
    display: flex;
    align-items: center;
    justify-content: center;
}
</style>
