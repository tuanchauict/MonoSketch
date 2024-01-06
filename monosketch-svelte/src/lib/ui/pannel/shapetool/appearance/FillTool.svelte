<script lang="ts">
import Tool from './common/Tool.svelte';
import CloudItem from './common/CloudItem.svelte';
import { fillOptions } from './model';

let selectedId = 'F0';

function onItemSelect(id: string) {
    selectedId = id;
}
</script>

<Tool title="Fill" available="{true}">
    <div>
        {#each fillOptions as option}
            <CloudItem
                id="{option.id}"
                selected="{option.id === selectedId}"
                useDashBorder="{option.useDashBorder}"
                onSelect="{onItemSelect}">{option.title}</CloudItem
            >
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
