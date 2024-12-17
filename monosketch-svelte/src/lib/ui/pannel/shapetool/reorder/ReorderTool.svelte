<script lang="ts">
    import Section from '../../common/Section.svelte';
    import Icon from './Icon.svelte';
    import { ButtonContent, ChangeOrderTypes } from './model';
    import { getContext } from "svelte";
    import { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
    import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants";
    import { OneTimeAction } from "$mono/action-manager/one-time-actions";
    import type { ChangeOrderType } from "$mono/shape/command/shape-manager-commands";

    let viewModel = getContext<ShapeToolViewModel>(SHAPE_TOOL_VIEWMODEL);

    function onClick(icon: ChangeOrderType) {
        viewModel.update(OneTimeAction.ReorderShape(icon));
    }
</script>

<Section hasBorderTop="{false}">
    <div>
        {#each ChangeOrderTypes as button}
            <Icon iconPath="{ButtonContent[button].icon}" title="{ButtonContent[button].title}"
                  onClick="{() => onClick(button)}"/>
        {/each}
    </div>
</Section>

<style>
    div {
        display: flex;
        flex-direction: row;
        justify-content: space-around;
        align-items: center;
    }
</style>
