<script lang="ts">
    import MouseActionButton from "./MouseActionButton.svelte";
    import {MouseActionType, mouseActionTypes} from "./model";

    let selectedActionType: MouseActionType = MouseActionType.SELECTION;

    function onSelect(actionType: MouseActionType) {
        selectedActionType = actionType;
    }
</script>

<div>
    {#each mouseActionTypes as type}
        <MouseActionButton type={type} {onSelect} selected={type === selectedActionType}/>
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
