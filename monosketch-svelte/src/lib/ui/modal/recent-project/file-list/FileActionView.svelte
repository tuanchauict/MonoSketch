<script lang="ts">
import type { FileAction } from '../model';

export let action: FileAction;
export let onClick: (action: FileAction) => void;

function onActionClick() {
    onClick(action);
}
</script>

<!--Use div here instead of button to avoid the modal is dismissed when click-->
<!--TODO: Fix this-->
<div on:click="{onActionClick}">
    <slot />
</div>

<style lang="scss">
div {
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
