<script lang="ts">
    import MouseActionButton from "./MouseActionButton.svelte";
    import {MouseActionType} from "./model";

    function onSelect(actionType: MouseActionType) {
        console.log(actionType);
    }
</script>

<div>
    <MouseActionButton type={MouseActionType.SELECTION} {onSelect}/>
    <MouseActionButton type={MouseActionType.ADD_RECTANGLE} {onSelect}/>
    <MouseActionButton type={MouseActionType.ADD_TEXT} {onSelect}/>
    <MouseActionButton type={MouseActionType.ADD_LINE} {onSelect}/>
</div>

<style>
    div {
        display: flex;
        flex-direction: row;
        align-items: center;
    }
</style>
