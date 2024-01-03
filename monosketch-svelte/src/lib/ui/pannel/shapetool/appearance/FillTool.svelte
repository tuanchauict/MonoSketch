<script lang="ts">
    import Tool from "./common/Tool.svelte";
    import CloudItem from "./common/CloudItem.svelte";
    import {CloudOption} from "./model";

    let selectedId = "F0"

    const options = [
        new CloudOption("F0", "×", true),
        new CloudOption("F1", "\u00A0"),
        new CloudOption("F2", "█"),
        new CloudOption("F3", "▒"),
        new CloudOption("F4", "░"),
        new CloudOption("F5", "▚"),
    ]

    function onItemSelect(id: string) {
        selectedId = id;
    }
</script>

<Tool title="Fill" available={true}>
    <div>
        {#each options as option}
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
