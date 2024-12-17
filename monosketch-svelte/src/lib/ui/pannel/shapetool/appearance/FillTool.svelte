<script lang="ts">
    import Tool from './common/Tool.svelte';
    import CloudItem from './common/CloudItem.svelte';
    import type { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
    import { getContext, onDestroy, onMount } from "svelte";
    import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants";
    import { LifecycleOwner } from "$libs/flow";
    import { OneTimeAction } from "$mono/action-manager/one-time-actions";
    import type { CloudItemSelectionState } from "$ui/pannel/shapetool/viewmodel/models";

    let viewModel = getContext<ShapeToolViewModel>(SHAPE_TOOL_VIEWMODEL);
    let lifecycleOwner = new LifecycleOwner();

    let shapeFillToolSelectionState: CloudItemSelectionState | null | undefined = viewModel.shapeFillTypeFlow.value;

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.shapeFillTypeFlow.observe(lifecycleOwner, (value) => {
            shapeFillToolSelectionState = value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });

    function onItemSelect(id: string | null) {
        viewModel.update(OneTimeAction.ChangeShapeFillExtra({isEnabled: id !== null, newFillStyleId: id}));
    }
</script>
{#if shapeFillToolSelectionState}
    <Tool title="Fill" available="{true}">
        <div>
            <!-- Disable-item -->
            <CloudItem
                    id="{null}"
                    selected="{shapeFillToolSelectionState.selectedId === null}"
                    useDashBorder="{true}"
                    onSelect="{onItemSelect}">&nbsp;
            </CloudItem>

            {#each viewModel.fillOptions as option}
                <CloudItem
                        id="{option.id}"
                        selected="{option.id === shapeFillToolSelectionState.selectedId}"
                        onSelect="{onItemSelect}">{option.name}</CloudItem
                >
            {/each}
        </div>
    </Tool>
{/if}

<style lang="scss">
    div {
        display: flex;
        flex-wrap: wrap;
        gap: 7px;
    }
</style>
