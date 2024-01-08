<script lang="ts">
import type { FileAction } from '../model';
import TooltipTarget from '../../tooltip/TooltipTarget.svelte';
import { Direction } from '../../tooltip/model';

export let action: FileAction;
export let onClick: (action: FileAction) => void;
export let tooltip: string | undefined = undefined;

function onActionClick() {
    onClick(action);
}
</script>

<!--Use div here instead of button to avoid the modal is dismissed when click-->
<!--TODO: Fix this-->
<div on:click="{onActionClick}">
    {#if tooltip}
        <TooltipTarget text="{tooltip}" direction="{Direction.TOP}" offsetVertical="{4}">
            <slot />
        </TooltipTarget>
    {:else}
        <slot />
    {/if}
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
