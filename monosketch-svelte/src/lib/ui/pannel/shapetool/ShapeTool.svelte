<script lang="ts">
    import { onDestroy, onMount, setContext } from 'svelte';
    import { getAppContext } from '$mono/common/constant';
    import { LifecycleOwner } from 'lib/libs/flow';
    import Footer from "./Footer.svelte";
    import ShapeToolBody from "$ui/pannel/shapetool/ShapeToolBody.svelte";
    import { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";
    import { SHAPE_TOOL_VIEWMODEL } from "$ui/pannel/shapetool/constants.js";

    const appContext = getAppContext();
    const lifecycleOwner = new LifecycleOwner();

    setContext(SHAPE_TOOL_VIEWMODEL, new ShapeToolViewModel(
        appContext.selectedShapeManager.selectedShapesFlow,
        appContext.shapeManager.versionFlow,
        appContext.actionManager
    ));

    let isVisible = true;

    onMount(() => {
        lifecycleOwner.onStart();

        appContext.appUiStateManager.shapeFormatPanelVisibilityFlow.observe(lifecycleOwner, (value) => {
            isVisible = value;
        });
    });

    onDestroy(() => {
        lifecycleOwner.onStop();
    });
</script>

{#if isVisible}
    <div class="container">
        <div class="body">
            <ShapeToolBody/>
        </div>
        <Footer/>
    </div>
{/if}

<style lang="scss">
    @import '$style/variables.scss';

    .container {
        display: flex;
        flex-direction: column;
        height: 100%;

        border-left: 1px solid var(--shapetool-main-divider-color);
        background: var(--shapetool-bg-color);

        font-family: $uiTextFont;
        font-size: 12px;
    }

    .body {
        height: 100%;
        overflow-y: scroll;
        display: flex;
        flex-direction: column;
    }
</style>
