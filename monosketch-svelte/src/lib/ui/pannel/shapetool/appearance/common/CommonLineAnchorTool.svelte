<script lang="ts">
    import Tool from './Tool.svelte';
    import CloudItem from './CloudItem.svelte';
    import { getShapeToolViewModel } from "$ui/pannel/shapetool/constants";

    export let title: string;
    export let selectedId: string | null;
    export let onItemSelect: (id: string | null) => void;

    let viewModel = getShapeToolViewModel();
</script>

<Tool {title} available="{true}">
    <div>
        <CloudItem
                id="{null}"
                selected="{ selectedId === null}"
                onSelect="{onItemSelect}">&nbsp;
        </CloudItem>
        {#each viewModel.headOptions as option}
            <CloudItem
                    id="{option.id}"
                    selected="{option.id === selectedId}"
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
