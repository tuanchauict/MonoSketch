<script lang="ts">
    import { TargetBounds } from '../model';
    import { TooltipDirection } from './model';
    import { onDestroy } from 'svelte';
    import TooltipView from "$ui/modal/tooltip/TooltipView.svelte";
    import type { Rect } from "$libs/graphics-geo/rect";

    export let text: string;
    export let direction: TooltipDirection = TooltipDirection.BOTTOM;
    export let offsetHorizontal: number = 0;
    export let offsetVertical: number = 0;

    export let style: string = '';

    let timeoutId: number;
    let targetBounds: Rect | undefined;

    let ref: HTMLDivElement;

    function showTooltip() {
        timeoutId = setTimeout(() => {
            targetBounds = ref ? TargetBounds.expandTargetBounds(
                    TargetBounds.fromElement(ref),
                    offsetHorizontal,
                    offsetVertical,
            ) : undefined;
        }, 600);
    }

    function hideTooltip() {
        clearTimeout(timeoutId);
        targetBounds = undefined;
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
        bind:this="{ref}"
>
    <slot/>
</div>
{#if targetBounds}
    <TooltipView {text} {direction} {targetBounds}/>
{/if}
<style lang="scss">
    div {
        display: flex;
        align-items: center;
        justify-content: center;
    }
</style>
