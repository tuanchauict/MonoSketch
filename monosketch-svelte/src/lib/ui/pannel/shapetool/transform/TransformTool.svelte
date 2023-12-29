<script lang="ts">
    import NumberTextField from "../../common/NumberTextField.svelte";
    import Section from "../../common/Section.svelte";

    let x = 0;
    let y = 0;
    let height = 0;
    let width = 0;

    $: console.log({x, y, height, width});
</script>

<Section title="TRANSFORM">
    <div class="grid">
        <div class="cell">
            <NumberTextField label="X" bind:value={x}/>
        </div>
        <div class="cell">
            <NumberTextField label="W" bind:value={width} minValue={1}/>
        </div>
        <div class="cell">
            <NumberTextField label="Y" bind:value={y}/>
        </div>
        <div class="cell">
            <NumberTextField label="H" bind:value={height} minValue={1}/>
        </div>
    </div>
</Section>

<style lang="scss">
    .grid {
        display: flex;
        flex-wrap: wrap;
    }

    .cell {
        width: 50%;
        padding: 0 8px 12px 0;

        &:nth-child(even) {
            padding: 0 0 12px 8px;
        }
    }
</style>
