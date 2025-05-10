<script lang="ts">
    import CommonBorderTool from './common/CommonBorderTool.svelte';
    import { getShapeToolViewModel } from "$ui/pannel/shapetool/constants";
    import { onDestroy, onMount } from "svelte";
    import { LifecycleOwner } from "$libs/flow";
    import { OneTimeAction } from "$mono/action-manager/one-time-actions";
    import type { StrokeDashPattern } from "$ui/pannel/shapetool/viewmodel/models";

    let viewModel = getShapeToolViewModel();
    let lifecycleOwner = new LifecycleOwner();

    let selectedStrokeItem = viewModel.lineStrokeTypeFlow.value;
    let cornerRounded = viewModel.lineStrokeRoundedCornerFlow.value;

    let dashPattern: StrokeDashPattern | null | undefined = viewModel.lineStrokeDashTypeFlow.value;

    function onItemSelect(id: string | null) {
        viewModel.update(OneTimeAction.ChangeLineStrokeExtra({ newStrokeStyleId: id, isEnabled: id !== null }));
    }

    function onCornerRoundedChange(value: boolean) {
        viewModel.update(OneTimeAction.ChangeLineStrokeCornerExtra(value));
    }

    function onDashPatternChange(dashPattern: StrokeDashPattern) {
        viewModel.update(OneTimeAction.ChangeLineStrokeDashPatternExtra(dashPattern));
    }

    onMount(() => {
        lifecycleOwner.onStart();

        viewModel.lineStrokeTypeFlow.observe(lifecycleOwner, (value) => {
            selectedStrokeItem = value;
        });

        viewModel.lineStrokeRoundedCornerFlow.observe(lifecycleOwner, (value) => {
            cornerRounded = value;
        });

        viewModel.lineStrokeDashTypeFlow.observe(lifecycleOwner, (value) => {
            dashPattern = value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>
{#if selectedStrokeItem && cornerRounded !== undefined && dashPattern !== undefined}
    <CommonBorderTool
            title="Stroke"
            {onItemSelect}
            selectedId="{selectedStrokeItem.isChecked ? selectedStrokeItem.selectedId : null}"
            {cornerRounded}
            onCornerRounded="{onCornerRoundedChange}"
            {dashPattern}
            {onDashPatternChange}
    />
{/if}
