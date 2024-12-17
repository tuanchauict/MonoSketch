<script lang="ts">
    import Tool from './Tool.svelte';
    import CloudItem from './CloudItem.svelte';
    import RoundedCornerButton from './RoundedCornerButton.svelte';
    import DashPattern from './DashPattern.svelte';
    import type { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
    import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants";
    import { getContext } from "svelte";
    import { PredefinedStraightStrokeStyle } from "$mono/shape/extra/predefined-styles";
    import type { StrokeDashPattern } from "$ui/pannel/shapetool/viewmodel/models";

    export let title: string;
    export let selectedId: string | null;

    export let cornerRounded: boolean | null;

    export let dashPattern: StrokeDashPattern | null;

    export let onItemSelect: (id: string | null) => void;
    export let onCornerRounded: (rounded: boolean) => void;
    export let onDashPatternChange: (dashPattern: StrokeDashPattern) => void;

    let viewModel: ShapeToolViewModel = getContext(SHAPE_TOOL_VIEWMODEL);

    function onRoundedCornerButtonClick() {
        onCornerRounded(!cornerRounded);
    }
</script>

<Tool {title} available="{true}">
    <div class="border">
        <div class="cloud">
            <!-- Disable-item -->
            <CloudItem
                    id="{null}"
                    selected="{selectedId === null}"
                    onSelect="{onItemSelect}">&nbsp;
            </CloudItem>
            {#each viewModel.strokeOptions as option}
                <CloudItem
                        id="{option.id}"
                        selected="{option.id === selectedId}"
                        onSelect="{onItemSelect}"
                >
                    {option.name}
                </CloudItem>
            {/each}
        </div>

        {#if PredefinedStraightStrokeStyle.isCornerRoundable(selectedId)}
            <div class="rounded-corner">
                <RoundedCornerButton
                        selected="{cornerRounded ?? false}"
                        onClick="{onRoundedCornerButtonClick}"
                />
            </div>
        {/if}
    </div>
    <DashPattern {dashPattern} onChange="{onDashPatternChange}"/>
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
