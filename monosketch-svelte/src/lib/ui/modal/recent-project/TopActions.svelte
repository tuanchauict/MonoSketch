<script lang="ts">

import MenuItem from './common/MenuItem.svelte';
</script>

<div>
    <MenuItem >
        <span slot="icon">🍔</span>
        <span slot="content">New project</span>
    </MenuItem>
    <MenuItem >
        <span slot="icon">🍟</span>
        <span slot="content">Import from file</span>
    </MenuItem>
</div>

<style lang="scss">
div {
    border-bottom: 1px solid var(--mainmenu-divider-color);
    padding-bottom: 8px;
}
</style>
