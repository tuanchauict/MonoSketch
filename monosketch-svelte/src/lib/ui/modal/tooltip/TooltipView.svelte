<script lang="ts">
    import { TooltipDirection, directionToIconPathMap } from './model';
    import { calcArrowLeft, calcArrowTop, calcBodyLeft, calcBodyTop } from './utils';
    import SvgIcon from '$ui/common/SvgIcon.svelte';
    import Portal from "$ui/modal/common/Portal.svelte";
    import type { Rect } from "$libs/graphics-geo/rect";

    export let text: string;
    export let direction: TooltipDirection;
    export let targetBounds: Rect;

    let arrow: HTMLDivElement;
    let body: HTMLDivElement;

    $: arrowLeft = calcArrowLeft(arrow, direction, targetBounds);
    $: arrowTop = calcArrowTop(arrow, direction, targetBounds);
    $: bodyLeft = calcBodyLeft(body, arrow, direction, targetBounds);
    $: bodyTop = calcBodyTop(body, arrow, direction, targetBounds);

    $: isVertical = direction === TooltipDirection.TOP || direction === TooltipDirection.BOTTOM;
    $: arrowSize = isVertical ? [10, 5] : [5, 10];
    $: arrowViewBoxSize = isVertical ? [20, 10] : [10, 20];
</script>

<Portal parent="#tooltip">
    <div class="arrow" bind:this="{arrow}" style="left:{arrowLeft}px; top:{arrowTop}px">
        <SvgIcon size="{arrowSize}" viewBoxSize="{arrowViewBoxSize}">
            <path d="{directionToIconPathMap[direction]}"></path>
        </SvgIcon>
    </div>
    <div class="body" bind:this="{body}" style="left:{bodyLeft}px; top:{bodyTop}px">
        <span>{text}</span>
    </div>
</Portal>

<style lang="scss">
    @import '$style/variables';

    $tooltip-background-color: var(--tooltip-bg);
    $tooltip-color: var(--tooltip-color);

    .arrow {
        display: flex;
        position: fixed;
        color: $tooltip-background-color;
    }

    .body {
        position: fixed;
        background: $tooltip-background-color;
        color: $tooltip-color;
        padding: 2px 8px 1px;
        border-radius: 4px;
        font-size: 11px;
        font-family: $monospaceFont;
    }
</style>
