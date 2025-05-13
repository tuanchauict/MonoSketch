<script lang="ts">
    import { WorkspaceViewController } from '$mono/workspace/workspace-view-controller';
    import { onDestroy, onMount } from 'svelte';
    import { getAppContext } from '$mono/common/constant';
    import TooltipTarget from "$ui/modal/tooltip/TooltipTarget.svelte";
    import { TooltipDirection } from "$ui/modal/tooltip/model";
    import { AXIS_X_HEIGHT, AXIS_Y_WIDTH } from "$mono/workspace/canvas/axis-canvas-view-controller";
    import { Point } from "$libs/graphics-geo/point";

    let canvasContainer: HTMLDivElement;
    let drawingInfoCanvas: HTMLCanvasElement;
    let axisCanvas: HTMLCanvasElement;
    let gridCanvas: HTMLCanvasElement;
    let boardCanvas: HTMLCanvasElement;
    let interactionCanvas: HTMLCanvasElement;
    let selectionCanvas: HTMLCanvasElement;

    const appContext = getAppContext();

    let workspaceViewController: WorkspaceViewController;
    onMount(() => {
        workspaceViewController = new WorkspaceViewController(
            appContext,
            canvasContainer,
            drawingInfoCanvas,
            gridCanvas,
            boardCanvas,
            axisCanvas,
            interactionCanvas,
            selectionCanvas,
        );
        workspaceViewController.onStart();
    });

    onDestroy(() => {
        workspaceViewController.onStop();
    });
</script>
<div class="canvas-container">
    <canvas bind:this="{axisCanvas}"></canvas>
    <TooltipTarget text="Jump to (0, 0)" direction="{TooltipDirection.RIGHT}"
                   style="width: {AXIS_Y_WIDTH}px; height: {AXIS_X_HEIGHT}px;">
        <button tabindex="-1" class="jump"
                style="width: {AXIS_Y_WIDTH}px; height: {AXIS_X_HEIGHT}px"
                on:click={() => workspaceViewController.setDrawingOffset(Point.ZERO)}></button>
    </TooltipTarget>
</div>
<div class="canvas-container shift" bind:this="{canvasContainer}">
    <canvas class="drawing-info" bind:this="{drawingInfoCanvas}"></canvas>
    <canvas bind:this="{gridCanvas}"></canvas>
    <canvas bind:this="{boardCanvas}"></canvas>
    <canvas bind:this="{interactionCanvas}"></canvas>
    <canvas bind:this="{selectionCanvas}"></canvas>
</div>

<style lang="scss">
    .canvas-container {
        position: absolute;
        left: 0;
        top: 0;
        right: 0;
        bottom: 0;

        &.shift {
            left: 33px;
            top: 18px;
        }
    }

    canvas {
        width: 100%;
        height: 100%;
        position: absolute;
        left: 0;
        top: 0;
    }

    .drawing-info {
        position: absolute;
        left: -200%;
        top: -200%;
    }

    .jump {
        position: absolute;
        left: 0;
        top: 0;
        background: none;
        border: none;
        cursor: pointer;
    }
</style>
