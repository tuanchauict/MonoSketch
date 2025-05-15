<script lang="ts">
import ShortcutTable from './ShortcutTable.svelte';
import { shortcuts } from './model';
</script>

<section>
    <div class="col1">
        <ShortcutTable shortcuts="{shortcuts[0]}" />
    </div>
    <div class="col2">
        <ShortcutTable shortcuts="{shortcuts[1]}" />
    </div>
    <div class="col3">
        <ShortcutTable shortcuts="{shortcuts[2]}" />
    </div>
</section>

<style lang="scss">
section {
    display: flex;
    flex-direction: row;
    justify-content: center;
    align-items: flex-start;
    margin: 0 0 1.5rem 0;
    gap: 24px;
}
</style>
