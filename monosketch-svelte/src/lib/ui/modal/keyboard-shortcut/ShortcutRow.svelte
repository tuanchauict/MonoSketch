<script lang="ts">
import type { Shortcut } from './model';

export let shortcut: Shortcut;
</script>

<tr>
    <td>
        <span class="description">{shortcut.description}</span>
    </td>
    <td class="keys">
        {#each shortcut.keys as key}
            <span class="key" class:bold="{key.length === 1}">{key}</span>
        {/each}
    </td>
</tr>

<style lang="scss">
@import '../../../style/variables.scss';

.description {
    font-family: $monospaceFont;
    font-size: 0.8em;
    color: #666;
    margin-left: 0.5em;
}

.keys {
    display: flex;
    flex-direction: row;
    align-items: flex-end;
    justify-content: flex-end;
    margin: 0 4px;
}

.key {
    font-family: $monospaceFont;
    display: inline-block;
    text-align: center;

    padding: 3px 4px;
    min-width: 24px;
    border-radius: 4px;
    background: var(--keyboard-content-key-bg);
    border: 1px solid var(--keyboard-content-key-border-color);
    font-size: 13px;
    color: var(--keyboard-content-key-color);
    line-height: 1.2;
    margin-right: 4px;

    &.bold {
        font-weight: bold;
    }
}
</style>
