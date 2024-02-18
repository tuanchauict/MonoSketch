<script lang="ts">
import { WorkspaceViewController } from '$mono/workspace/workspace-view-controller';
import { getContext, onDestroy, onMount } from 'svelte';
import type { AppContext } from '$app/app-context';
import { APP_CONTEXT } from '$mono/common/constant';

let canvasContainer: HTMLDivElement;
let drawingInfoCanvas: HTMLCanvasElement;
let axisCanvas: HTMLCanvasElement;
let gridCanvas: HTMLCanvasElement;
let interactionCanvas: HTMLCanvasElement;

const appContext = getContext<AppContext>(APP_CONTEXT);

let workspaceViewController: WorkspaceViewController;
onMount(() => {
    workspaceViewController = new WorkspaceViewController(
        appContext,
        canvasContainer,
        drawingInfoCanvas,
        gridCanvas,
        axisCanvas,
        interactionCanvas,
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
    <canvas bind:this="{axisCanvas}"></canvas>
    <canvas bind:this="{interactionCanvas}"></canvas>
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
</style>
