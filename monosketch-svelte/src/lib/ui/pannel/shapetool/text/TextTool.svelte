<script lang="ts">
    import Section from '../../common/Section.svelte';
    import FormatIcon from './FormatIcon.svelte';
    import { HorizontalAlignIconMap, VerticalAlignIconMap } from './model';
    import { TextAlign, TextHorizontalAlign, TextVerticalAlign } from "$mono/shape/extra/style";
    import { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
    import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants";
    import { getContext, onDestroy, onMount } from "svelte";
    import { LifecycleOwner } from "$libs/flow";
    import { OneTimeAction } from "$mono/action-manager/one-time-actions";

    let viewModel = getContext<ShapeToolViewModel>(SHAPE_TOOL_VIEWMODEL);
    let lifecycleOwner = new LifecycleOwner();

    let horizontalAlign: TextHorizontalAlign | null | undefined = viewModel.textAlignFlow.value?.horizontalAlign;
    let verticalAlign: TextVerticalAlign | null | undefined = viewModel.textAlignFlow.value?.verticalAlign;

    function onHorizontalChange(type: TextHorizontalAlign) {
        horizontalAlign = type;
        notifyChange();
    }

    function onVerticalChange(type: TextVerticalAlign) {
        verticalAlign = type;
        notifyChange();
    }

    function notifyChange() {
        if (!verticalAlign || !horizontalAlign) {
            return;
        }
        viewModel.update(OneTimeAction.TextAlignment({
            newHorizontalAlign: horizontalAlign,
            newVerticalAlign: verticalAlign
        }));
    }

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.textAlignFlow.observe(lifecycleOwner, (value) => {
            if (!value) {
                return;
            }
            horizontalAlign = value.horizontalAlign;
            verticalAlign = value.verticalAlign;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>

{#if horizontalAlign !== null && verticalAlign !== null}
    <Section title="TEXT">
        <div class="row">
            <span>Alignment</span>
            <div class="container">
                {#each TextAlign.ALL_HORIZONTAL_ALIGNS as type}
                    <FormatIcon
                            onClick="{() => onHorizontalChange(type)}"
                            iconPath="{HorizontalAlignIconMap[type]}"
                            selected="{horizontalAlign === type}"
                    />
                {/each}
            </div>
        </div>

        <div class="row">
            <span>Position</span>
            <div class="container">
                {#each TextAlign.ALL_VERTICAL_ALIGNS as type}
                    <FormatIcon
                            onClick="{() => onVerticalChange(type)}"
                            iconPath="{VerticalAlignIconMap[type]}"
                            selected="{verticalAlign === type}"
                    />
                {/each}
            </div>
        </div>
    </Section>
{/if}
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
        width: 66px;
    }
</style>
