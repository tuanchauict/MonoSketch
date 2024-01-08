<script lang="ts">
import type { FileAction } from '../model';

export let action: FileAction;
export let onClick: (action: FileAction) => void;

function onActionClick(e: MouseEvent) {
    e.stopPropagation();
    onClick(action);
}
</script>

<button on:click="{onActionClick}">
    <slot />
</button>

<style lang="scss">
button {
    display: flex;
    background: none;
    border: none;
    outline: none;
    cursor: pointer;
    padding: 0;
    margin: 0;
    color: inherit;

    opacity: 0.7;

    &:hover {
        opacity: 1;
    }
}
</style>
