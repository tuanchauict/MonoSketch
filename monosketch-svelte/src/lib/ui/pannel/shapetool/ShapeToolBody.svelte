<!--
  - Copyright (c) 2024, tuanchauict
  -->

<script lang="ts">
    import TransformTool from "$ui/pannel/shapetool/transform/TransformTool.svelte";
    import ReorderTool from "$ui/pannel/shapetool/reorder/ReorderTool.svelte";
    import AppearanceTool from "$ui/pannel/shapetool/appearance/AppearanceTool.svelte";
    import TextTool from "$ui/pannel/shapetool/text/TextTool.svelte";
    import { getContext, onDestroy, onMount } from "svelte";
    import { LifecycleOwner } from "$libs/flow";
    import { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
    import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants";

    let viewModel: ShapeToolViewModel = getContext<ShapeToolViewModel>(SHAPE_TOOL_VIEWMODEL);

    const lifecycleOwner = new LifecycleOwner();

    let isReorderToolVisible = viewModel.reorderToolVisibilityFlow.value;
    let isTransformToolVisible = !!viewModel.singleShapeBoundFlow.value; // not null nor undefined
    let isAppearanceToolVisible = viewModel.appearanceVisibilityFlow.value;
    let isTextToolVisible = !!viewModel.textAlignFlow.value; // not null nor undefined

    $: hasAnyVisibleTool =
            isReorderToolVisible || isTransformToolVisible || isAppearanceToolVisible || isTextToolVisible;

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.reorderToolVisibilityFlow.observe(lifecycleOwner, (value) => {
            isReorderToolVisible = value;
        });

        viewModel.singleShapeBoundFlow.observe(lifecycleOwner, (value) => {
            isTransformToolVisible = !!value;
        });

        viewModel.appearanceVisibilityFlow.observe(lifecycleOwner, (value) => {
            isAppearanceToolVisible = value;
        });

        viewModel.textAlignFlow.observe(lifecycleOwner, (value) => {
            isTextToolVisible = !!value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>

{#if isReorderToolVisible }
    <ReorderTool/>
{/if}

{#if isTransformToolVisible }
    <TransformTool/>
{/if}

{#if isAppearanceToolVisible || true}
    <AppearanceTool/>
{/if}

{#if isTextToolVisible }
    <TextTool/>
{/if}

{#if !hasAnyVisibleTool}
    <div class="empty">
        <span>Select a shape for updating its properties here</span>
    </div>
{/if}

<style lang="scss">
    @import '../../../style/variables.scss';

    .empty {
        height: 100%;
        justify-content: center;
        margin-bottom: 200px;
        margin-top: 28px;
        padding: 10px;
    }

    span {
        font-size: 13px;
        font-family: $monospaceFont;
        color: var(--shapetool-indicator-color);
        font-weight: 300;
        display: block;
        text-align: center;
        margin: 120px 8px;
    }
</style>
