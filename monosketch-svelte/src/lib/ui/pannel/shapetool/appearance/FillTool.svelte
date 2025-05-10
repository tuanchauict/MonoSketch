<script lang="ts">
    import Tool from './common/Tool.svelte';
    import CloudItem from './common/CloudItem.svelte';
    import { onDestroy, onMount } from "svelte";
    import { getShapeToolViewModel } from "$ui/pannel/shapetool/constants";
    import { LifecycleOwner } from "$libs/flow";
    import { OneTimeAction } from "$mono/action-manager/one-time-actions";
    import type { CloudItemSelectionState } from "$ui/pannel/shapetool/viewmodel/models";

    let viewModel = getShapeToolViewModel();
    let lifecycleOwner = new LifecycleOwner();

    let shapeFillToolSelectionState: CloudItemSelectionState | null = viewModel.shapeFillTypeFlow.value ?? null;

    $: selectedId = shapeFillToolSelectionState?.isChecked ? shapeFillToolSelectionState.selectedId : null;

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.shapeFillTypeFlow.observe(lifecycleOwner, (value) => {
            shapeFillToolSelectionState = value ?? null;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });

    function onItemSelect(id: string | null) {
        viewModel.update(OneTimeAction.ChangeShapeFillExtra({ isEnabled: id !== null, newFillStyleId: id }));
    }
</script>
{#if shapeFillToolSelectionState}
    <Tool title="Fill" available="{true}">
        <div>
            <!-- Disable-item -->
            <CloudItem
                    id="{null}"
                    selected="{selectedId === null}"
                    useDashBorder="{true}"
                    onSelect="{onItemSelect}">×
            </CloudItem>

            {#each viewModel.fillOptions as option}
                <CloudItem
                        id="{option.id}"
                        selected="{option.id === selectedId}"
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
