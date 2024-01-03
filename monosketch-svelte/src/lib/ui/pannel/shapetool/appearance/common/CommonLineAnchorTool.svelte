<script lang="ts">
    import Tool from "./Tool.svelte";
    import CloudItem from "./CloudItem.svelte";
    import {lineAnchorOptions} from "../model";

    export let title: string;
    export let selectedId: string;

    function onItemSelect(id: string) {
        selectedId = id;
    }
</script>

<Tool title="{title}" available={true}>
    <div>
        {#each lineAnchorOptions as option}
            <CloudItem id={option.id} selected={option.id === selectedId}
                       useDashBorder={option.useDashBorder}
                       onSelect={onItemSelect}>{option.title}</CloudItem>
        {/each}
    </div>
</Tool>

<style lang="scss">
    div {
        display: flex;
        flex-wrap: wrap;
        gap: 7px;
    }
</style>
