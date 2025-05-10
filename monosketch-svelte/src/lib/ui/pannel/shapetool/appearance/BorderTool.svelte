<script lang="ts">
    import CommonBorderTool from './common/CommonBorderTool.svelte';
    import { onDestroy, onMount } from "svelte";
    import { getShapeToolViewModel } from "$ui/pannel/shapetool/constants";
    import { LifecycleOwner } from "$libs/flow";
    import type { StrokeDashPattern } from "$ui/pannel/shapetool/viewmodel/models";
    import { OneTimeAction } from "$mono/action-manager/one-time-actions";

    let viewModel = getShapeToolViewModel();
    let lifecycleOwner = new LifecycleOwner();

    let selectedStrokeItem = viewModel.shapeBorderTypeFlow.value;
    let cornerRounded = viewModel.shapeBorderRoundedCornerFlow.value;

    let dashPattern: StrokeDashPattern | null = viewModel.shapeBorderDashTypeFlow.value ?? null;

    function onItemSelect(id: string | null) {
        viewModel.update(OneTimeAction.ChangeShapeBorderExtra({ newBorderStyleId: id, isEnabled: id !== null }));
    }

    function onCornerRoundedChange(value: boolean) {
        viewModel.update(OneTimeAction.ChangeShapeBorderCornerExtra(value));
    }

    function onDashPatternChange(dashPattern: StrokeDashPattern) {
        viewModel.update(OneTimeAction.ChangeShapeBorderDashPatternExtra(dashPattern));
    }

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.shapeBorderTypeFlow.observe(lifecycleOwner, (value) => {
            selectedStrokeItem = value;
        });

        viewModel.shapeBorderRoundedCornerFlow.observe(lifecycleOwner, (value) => {
            cornerRounded = value;
        });

        viewModel.shapeBorderDashTypeFlow.observe(lifecycleOwner, (value) => {
            dashPattern = value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>

{#if selectedStrokeItem && cornerRounded !== undefined && dashPattern !== null}
    <CommonBorderTool
            title="Border"
            {onItemSelect}
            selectedId="{selectedStrokeItem.isChecked ? selectedStrokeItem.selectedId : null}"
            {cornerRounded}
            onCornerRounded="{onCornerRoundedChange}"
            {dashPattern}
            {onDashPatternChange}
    />
{/if}
