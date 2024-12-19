<script lang="ts">
import NavBar from './lib/ui/nav/NavBar.svelte';
import ModalHolder from './lib/ui/modal/ModalHolder.svelte';
import ShapeTool from './lib/ui/pannel/shapetool/ShapeTool.svelte';
import Workspace from '$ui/workspace/Workspace.svelte';
</script>

<nav>
    <NavBar />
</nav>
<main>
    <div class="workspace">
        <Workspace />
    </div>
    <div class="right-panel">
        <ShapeTool />
    </div>
</main>

<ModalHolder />

<style lang="scss">
$nav-height: 48px;
$sharp-tool-width: 250px;

nav {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: $nav-height;

    background: var(--header-bg);
}

.workspace {
    position: fixed;
    top: $nav-height;
    left: 0;
    bottom: 0;
    width: 100%;

    background: var(--workspace-bg-color);
}

.right-panel {
    position: fixed;
    top: $nav-height;
    right: 0;
    bottom: 0;
    width: $sharp-tool-width;
}
</style>
