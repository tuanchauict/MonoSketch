<script lang="ts">
    import MainDropDown from "./menu/main-dropdown/MainDropDown.svelte";
    import {onDestroy, onMount} from "svelte";
    import {modalViewModel} from "./viewmodel";
    import {LifecycleOwner} from "../../mono/flow";
    import type {TargetBounds} from "./model";
    import type {Tooltip} from "./menu/tooltip/model";
    import TooltipView from "./menu/tooltip/TooltipView.svelte";

    let mainDropDownTarget: (TargetBounds | null) = null;
    let tooltip: (Tooltip | null) = null;

    const lifecycleOwner = new LifecycleOwner();
    onMount(() => {
        lifecycleOwner.onStart();

        modalViewModel.mainDropDownMenuTargetFlow.observe(lifecycleOwner, (target) => {
            mainDropDownTarget = target;
        });

        modalViewModel.tooltipFlow.observe(lifecycleOwner, (value) => {
            tooltip = value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>

{#if mainDropDownTarget}
    <MainDropDown targetBounds={mainDropDownTarget}/>
{/if}

{#if tooltip}
    <TooltipView tooltip={tooltip}/>
{/if}
