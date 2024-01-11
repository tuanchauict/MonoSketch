<script>
import ReorderTool from './reorder/ReorderTool.svelte';
import TransformTool from './transform/TransformTool.svelte';
import AppearanceTool from './appearance/AppearanceTool.svelte';
import TextTool from './text/TextTool.svelte';
</script>

<div class="container">
    <div class="body">
        <ReorderTool />
        <TransformTool />
        <AppearanceTool />
        <TextTool />
    </div>
    <div class="footer">Hello</div>
</div>

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

.footer {
    padding: 10px 8px;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
