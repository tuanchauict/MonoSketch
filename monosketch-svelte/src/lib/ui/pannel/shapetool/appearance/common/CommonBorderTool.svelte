<script lang="ts">
    import Tool from "./Tool.svelte";
    import CloudItem from "./CloudItem.svelte";
    import {CloudOption} from "../model";
    import RoundedCornerButton from "./RoundedCornerButton.svelte";
    import DashPattern from "./DashPattern.svelte";

    export let title: string;
    export let selectedId = "B1";
    export let cornerRounded: boolean;

    export let dashDash: number;
    export let dashGap: number;
    export let dashShift: number;

    export let onItemSelect: (id: string) => void;
    export let onCornerRounded: (rounded: boolean) => void;

    $: isCornerRoundable = selectedId === "B1"

    const options = [
        new CloudOption("B0", "\u00A0"),
        new CloudOption("B1", "─"),
        new CloudOption("B2", "━"),
        new CloudOption("B3", "═"),
    ]

    function onRoundedCornerButtonClick() {
        onCornerRounded(!cornerRounded);
    }
</script>

<Tool title={title} available={true}>
    <div class="border">
        <div class="cloud">
            {#each options as option}
                <CloudItem
                        id={option.id} selected={option.id === selectedId}
                        useDashBorder={option.useDashBorder} onSelect={onItemSelect}
                >
                    {option.title}
                </CloudItem>
            {/each}
        </div>

        {#if isCornerRoundable}
            <div class="rounded-corner">
                <RoundedCornerButton selected={cornerRounded} onClick={onRoundedCornerButtonClick}/>
            </div>
        {/if}
    </div>
    <DashPattern bind:dash={dashDash} bind:gap={dashGap} bind:shift={dashShift}/>
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
