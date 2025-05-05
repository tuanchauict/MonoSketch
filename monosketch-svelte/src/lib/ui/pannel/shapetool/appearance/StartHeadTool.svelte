<script lang="ts">
    import CommonLineAnchorTool from './common/CommonLineAnchorTool.svelte';
    import { onDestroy, onMount } from "svelte";
    import { getShapeToolViewModel } from "$ui/pannel/shapetool/constants";
    import { OneTimeAction } from "$mono/action-manager/one-time-actions";
    import { LifecycleOwner } from "$libs/flow";

    let viewModel = getShapeToolViewModel();
    let lifecycleOwner = new LifecycleOwner();

    let anchorItem = viewModel.lineStartHeadFlow.value;

    function onItemSelect(id: string | null) {
        viewModel.update(OneTimeAction.ChangeLineStartAnchorExtra({ newHeadId: id, isEnabled: id !== null }));
    }

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.lineStartHeadFlow.observe(lifecycleOwner, (value) => {
            anchorItem = value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>

{#if anchorItem}
    <CommonLineAnchorTool title="Start head" selectedId="{anchorItem.selectedId}" {onItemSelect}/>
{/if}
