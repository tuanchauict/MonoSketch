<script lang="ts">
    import CommonLineAnchorTool from './common/CommonLineAnchorTool.svelte';
    import { onDestroy, onMount } from "svelte";
    import { getShapeToolViewModel } from "$ui/pannel/shapetool/constants";
    import { OneTimeAction } from "$mono/action-manager/one-time-actions";
    import { LifecycleOwner } from "$libs/flow";

    let viewModel = getShapeToolViewModel();
    let lifecycleOwner = new LifecycleOwner();

    let anchorItem = viewModel.lineEndHeadFlow.value;

    function onItemSelect(id: string | null) {
        viewModel.update(OneTimeAction.ChangeLineEndAnchorExtra({ newHeadId: id, isEnabled: id !== null }));
    }

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.lineEndHeadFlow.observe(lifecycleOwner, (value) => {
            anchorItem = value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>
{#if anchorItem}
    <CommonLineAnchorTool title="End head" selectedId="{anchorItem.selectedId}" {onItemSelect}/>
{/if}
