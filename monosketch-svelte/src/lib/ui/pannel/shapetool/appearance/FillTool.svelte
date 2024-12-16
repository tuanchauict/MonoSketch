<script lang="ts">
import Tool from './common/Tool.svelte';
import CloudItem from './common/CloudItem.svelte';
import type { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
import { getContext } from "svelte";
import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants";

let viewModel = getContext<ShapeToolViewModel>(SHAPE_TOOL_VIEWMODEL);

let selectedId = 'F0';

function onItemSelect(id: string) {
    selectedId = id;
}
</script>

<Tool title="Fill" available="{true}">
    <div>
        {#each viewModel.fillOptions as option}
            <CloudItem
                id="{option.id}"
                selected="{option.id === selectedId}"
                useDashBorder="{option.useDashBorder}"
                onSelect="{onItemSelect}">{option.name}</CloudItem
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
