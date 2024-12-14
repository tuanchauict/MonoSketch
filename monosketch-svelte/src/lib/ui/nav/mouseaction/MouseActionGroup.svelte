<script lang="ts">
import MouseActionButton from './MouseActionButton.svelte';
import {
    MouseActionToContentMap,
    MouseActionType,
    mouseActionTypes,
    RetainableActionTypeToMouseActionTypeMap
} from './model';
import { getContext, onDestroy, onMount } from "svelte";
import type { AppContext } from "$app/app-context";
import { APP_CONTEXT } from "$mono/common/constant";
import { LifecycleOwner } from "$libs/flow";

let selectedActionType: MouseActionType = MouseActionType.SELECTION;

const appContext = getContext<AppContext>(APP_CONTEXT);

const lifecycleOwner = new LifecycleOwner();

function onSelect(actionType: MouseActionType) {
    appContext.actionManager.setRetainableAction(MouseActionToContentMap[actionType].retainableActionType);
}

onMount(() => {
    lifecycleOwner.onStart();

    appContext.actionManager.retainableActionFlow.observe(lifecycleOwner, (value) => {
        selectedActionType = RetainableActionTypeToMouseActionTypeMap[value];
    });
});

onDestroy(() => {
    lifecycleOwner.onStop();
});
</script>

<div>
    {#each mouseActionTypes as type}
        <MouseActionButton {type} {onSelect} selected="{type === selectedActionType}" />
    {/each}
</div>

<style>
div {
    display: flex;
    flex-direction: row;
    align-items: center;
    gap: 6px;
    margin-left: 16px;
}
</style>
