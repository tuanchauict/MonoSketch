<script lang="ts">
    import NumberTextField from '../../common/NumberTextField.svelte';
    import Section from '../../common/Section.svelte';
    import type { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
    import { LifecycleOwner } from "$libs/flow";
    import { getContext, onDestroy, onMount } from "svelte";
    import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants";

    let viewModel: ShapeToolViewModel = getContext(SHAPE_TOOL_VIEWMODEL);

    const lifecycleOwner = new LifecycleOwner();

    let x = 0;
    let y = 0;
    let height = 0;
    let width = 0;
    let isResizeable = viewModel.singleShapeResizeableFlow.value;

    // TODO: Notify value changed to shape manager

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.singleShapeBoundFlow.observe(lifecycleOwner, (value) => {
            if (!value) {
                return;
            }
            x = value.left;
            y = value.top;
            height = value.height;
            width = value.width;
        });

        viewModel.singleShapeResizeableFlow.observe(lifecycleOwner, (value) => {
            isResizeable = value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>

<Section title="TRANSFORM">
    <div class="grid">
        <div class="cell">
            <NumberTextField
                    label="X"
                    bind:value="{x}"
                    boundIncludesLabel="{true}"
            />
        </div>
        <div class="cell">
            <NumberTextField
                    label="W"
                    bind:value="{width}"
                    minValue="{1}"
                    boundIncludesLabel="{true}"
                    isEnabled="{isResizeable}"
            />
        </div>
        <div class="cell">
            <NumberTextField
                    label="Y"
                    bind:value="{y}"
                    boundIncludesLabel="{true}"
            />
        </div>
        <div class="cell">
            <NumberTextField
                    label="H"
                    bind:value="{height}"
                    minValue="{1}"
                    boundIncludesLabel="{true}"
                    isEnabled="{isResizeable}"
            />
        </div>
    </div>
</Section>

<style lang="scss">
    .grid {
        display: flex;
        flex-wrap: wrap;
    }

    .cell {
        width: 50%;
        padding: 0 8px 12px 0;

        &:nth-child(even) {
            padding: 0 0 12px 8px;
        }
    }
</style>
