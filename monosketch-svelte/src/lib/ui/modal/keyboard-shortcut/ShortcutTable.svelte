<script lang="ts">
import ShortcutRow from './ShortcutRow.svelte';
import type { Shortcut } from './model';

export let shortcuts: Shortcut[];
</script>

<table>
    {#each shortcuts as shortcut}
        <ShortcutRow {shortcut} />
    {/each}
</table>
