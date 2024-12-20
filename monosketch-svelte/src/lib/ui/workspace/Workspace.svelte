<script lang="ts">
    import { WorkspaceViewController } from '$mono/workspace/workspace-view-controller';
    import { getContext, onDestroy, onMount } from 'svelte';
    import type { AppContext } from '$app/app-context';
    import { APP_CONTEXT } from '$mono/common/constant';
    import TooltipTarget from "$ui/modal/tooltip/TooltipTarget.svelte";
    import { Direction } from "$ui/modal/tooltip/model";
    import { AXIS_X_HEIGHT, AXIS_Y_WIDTH } from "$mono/workspace/canvas/axis-canvas-view-controller";
    import { Point } from "$libs/graphics-geo/point";

    let canvasContainer: HTMLDivElement;
    let drawingInfoCanvas: HTMLCanvasElement;
    let axisCanvas: HTMLCanvasElement;
    let gridCanvas: HTMLCanvasElement;
    let boardCanvas: HTMLCanvasElement;
    let interactionCanvas: HTMLCanvasElement;
    let selectionCanvas: HTMLCanvasElement;

    const appContext = getContext<AppContext>(APP_CONTEXT);

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

<div class="canvas-container" bind:this="{canvasContainer}">
    <canvas class="drawing-info" bind:this="{drawingInfoCanvas}"></canvas>
    <canvas bind:this="{gridCanvas}"></canvas>
    <canvas bind:this="{boardCanvas}"></canvas>
    <canvas bind:this="{axisCanvas}"></canvas>
    <canvas bind:this="{interactionCanvas}"></canvas>
    <canvas bind:this="{selectionCanvas}"></canvas>

    <TooltipTarget text="Jump to (0, 0)" direction="{Direction.RIGHT}"
                   style="width: {AXIS_Y_WIDTH}px; height: {AXIS_X_HEIGHT}px;">
        <button tabindex="-1" class="jump"
                style="width: {AXIS_Y_WIDTH}px; height: {AXIS_X_HEIGHT}px"
                on:click={() => workspaceViewController.setOffset(Point.ZERO)}></button>
    </TooltipTarget>
</div>

<style lang="scss">
    .canvas-container {
        width: 100%;
        height: 100%;
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
        left: -1000px;
        top: -1000px;
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
