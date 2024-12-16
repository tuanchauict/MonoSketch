<script lang="ts">
import { getContext, onDestroy, onMount } from 'svelte';
import { AppContext } from '$app/app-context';
import { APP_CONTEXT } from '$mono/common/constant';
import { Flow, LifecycleOwner } from 'lib/libs/flow';
import Footer from "./Footer.svelte";
import ShapeToolBody from "$ui/pannel/shapetool/ShapeToolBody.svelte";
import { ShapeToolViewModel } from "$ui/pannel/shapetool/viewmodel/shape-tool-viewmodel";

const appContext = getContext<AppContext>(APP_CONTEXT);
const lifecycleOwner = new LifecycleOwner();

const viewModel = new ShapeToolViewModel(
    new Flow(new Set()), // TODO: Replace with real flow
    appContext.shapeManager.versionFlow,
    appContext.actionManager
);

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
            <ShapeToolBody {viewModel}/>
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
