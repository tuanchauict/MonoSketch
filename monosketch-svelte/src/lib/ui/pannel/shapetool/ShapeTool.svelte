<script lang="ts">
import ReorderTool from './reorder/ReorderTool.svelte';
import TransformTool from './transform/TransformTool.svelte';
import AppearanceTool from './appearance/AppearanceTool.svelte';
import TextTool from './text/TextTool.svelte';
import { getContext, onMount } from 'svelte';
import { AppContext } from '$app/app-context';
import { APP_CONTEXT } from '$mono/common/constant';
import { LifecycleOwner } from 'lib/libs/flow';
import Footer from "./Footer.svelte";

const appContext = getContext<AppContext>(APP_CONTEXT);
const lifecycleOwner = new LifecycleOwner();

let isVisible = true;

onMount(() => {
    lifecycleOwner.onStart();

    appContext.appUiStateManager.shapeFormatPanelVisibilityFlow.observe(lifecycleOwner, (value) => {
        isVisible = value;
    });
});
</script>

{#if isVisible}
    <div class="container">
        <div class="body">
            <ReorderTool />
            <TransformTool />
            <AppearanceTool />
            <TextTool />
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
