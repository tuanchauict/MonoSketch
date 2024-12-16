<script lang="ts">
    import Tool from './Tool.svelte';
    import CloudItem from './CloudItem.svelte';
    import RoundedCornerButton from './RoundedCornerButton.svelte';
    import DashPattern from './DashPattern.svelte';
    import type { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
    import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants";
    import { getContext } from "svelte";

    export let title: string;
    export let selectedId = 'B1';
    export let cornerRounded: boolean;

    export let dashDash: number;
    export let dashGap: number;
    export let dashShift: number;

    export let onItemSelect: (id: string) => void;
    export let onCornerRounded: (rounded: boolean) => void;

    let viewModel: ShapeToolViewModel = getContext(SHAPE_TOOL_VIEWMODEL);

    $: isCornerRoundable = selectedId === 'B1';

    function onRoundedCornerButtonClick() {
        onCornerRounded(!cornerRounded);
    }
</script>

<Tool {title} available="{true}">
    <div class="border">
        <div class="cloud">
            {#each viewModel.strokeOptions as option}
                <CloudItem
                        id="{option.id}"
                        selected="{option.id === selectedId}"
                        useDashBorder="{option.useDashBorder}"
                        onSelect="{onItemSelect}"
                >
                    {option.name}
                </CloudItem>
            {/each}
        </div>

        {#if isCornerRoundable}
            <div class="rounded-corner">
                <RoundedCornerButton
                        selected="{cornerRounded}"
                        onClick="{onRoundedCornerButtonClick}"
                />
            </div>
        {/if}
    </div>
    <DashPattern bind:dash="{dashDash}" bind:gap="{dashGap}" bind:shift="{dashShift}"/>
</Tool>

<style lang="scss">
    .border {
        display: flex;
        flex-direction: row;
        gap: 7px;
    }

    .cloud {
        display: flex;
        flex-wrap: wrap;
        gap: 7px;
    }

    .rounded-corner {
        display: flex;
        align-items: center;
        margin-left: 8px;
        padding-left: 8px;
        border-left: 1px solid var(--shapetool-section-divider-color);
    }
</style>
