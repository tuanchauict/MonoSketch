<script lang="ts">
import { WorkspaceViewController } from '$mono/workspace/workspace-view-controller';
import { onDestroy, onMount } from 'svelte';

let canvasContainer: HTMLDivElement;
let drawingInfoCanvas: HTMLCanvasElement;
let axisCanvas: HTMLCanvasElement;

let workspaceViewController: WorkspaceViewController;
onMount(() => {
    workspaceViewController = new WorkspaceViewController(
        canvasContainer,
        drawingInfoCanvas,
        axisCanvas,
    );
    workspaceViewController.onStart();
});

onDestroy(() => {
    workspaceViewController.onStop();
});
</script>

<div class="canvas-container" bind:this="{canvasContainer}">
    <canvas class="drawing-info" bind:this="{drawingInfoCanvas}"></canvas>
    <canvas bind:this="{axisCanvas}"></canvas>
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
