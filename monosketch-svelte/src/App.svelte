<script lang="ts">

    import NavBar from './lib/ui/nav/NavBar.svelte';
</script>
<nav>
    <NavBar/>
</nav>
<main>
    <div class="workspace"></div>
    <div class="right-panel"></div>
</main>

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

        background: var(--shapetool-bg-color);
    }
</style>
