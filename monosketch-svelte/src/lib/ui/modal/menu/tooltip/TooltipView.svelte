<script lang="ts">
    import {Direction, directionToIconPathMap, type Tooltip} from "./model";
    import {calcArrowLeft, calcArrowTop, calcBodyLeft, calcBodyTop} from "./utils";

    export let tooltip: Tooltip;

    let arrow: HTMLDivElement;
    let body: HTMLDivElement;

    $: arrowLeft = calcArrowLeft(arrow, tooltip);
    $: arrowTop = calcArrowTop(arrow, tooltip);
    $: bodyLeft = calcBodyLeft(body, arrow, tooltip);
    $: bodyTop = calcBodyTop(body, arrow, tooltip);

    $: arrowSize = tooltip.direction === Direction.TOP || tooltip.direction === Direction.BOTTOM ? [10, 5, 20, 10] : [5, 10, 10, 20];
</script>

<div class="arrow" bind:this={arrow} style="left:{arrowLeft}px; top:{arrowTop}px">
    <svg width="{arrowSize[0]}" height="{arrowSize[1]}" viewBox="0 0 {arrowSize[2]} {arrowSize[3]}" fill="currentColor">
        <path d="{directionToIconPathMap[tooltip.direction]}" />
    </svg>
</div>
<div class="body" bind:this={body} style="left:{bodyLeft}px; top:{bodyTop}px">
    <span>{tooltip.text}</span>
</div>

<style lang="scss">
    @import "../../../../style/variables.scss";

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
