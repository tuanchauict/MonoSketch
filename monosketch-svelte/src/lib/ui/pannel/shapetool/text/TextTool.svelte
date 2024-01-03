<script lang="ts">
    import Section from "../../common/Section.svelte";
    import FormatIcon from "./FormatIcon.svelte";
    import {horizontalAlignmentTypes, TextAlignment, verticalAlignmentTypes} from "./model";

    let selectedHorizontalAlignment: TextAlignment = TextAlignment.HORIZONTAL_LEFT;
    let selectedVerticalAlignment: TextAlignment = TextAlignment.VERTICAL_TOP;

    function onHorizontalChange(type: TextAlignment) {
        selectedHorizontalAlignment = type;
    }

    function onVerticalChange(type: TextAlignment) {
        selectedVerticalAlignment = type;
    }
</script>

<Section title="TEXT">
    <div class="row">
        <span>Alignment</span>
        <div class="container">
            {#each horizontalAlignmentTypes as type}
                <FormatIcon {type} onChange={onHorizontalChange}
                            selected={selectedHorizontalAlignment === type}/>
            {/each}
        </div>
    </div>

    <div class="row">
        <span>Position</span>
        <div class="container">
            {#each verticalAlignmentTypes as type}
                <FormatIcon {type} onChange={onVerticalChange}
                            selected={selectedVerticalAlignment === type}/>
            {/each}
        </div>
    </div>
</Section>

<style lang="scss">
    $gap: 8px;
    .row {
        display: flex;
        flex-direction: row;
        align-items: center;
        margin-bottom: $gap;
    }

    .container {
        display: flex;
        flex-direction: row;
        gap: $gap;
    }

    span {
        width: 66px
    }
</style>
