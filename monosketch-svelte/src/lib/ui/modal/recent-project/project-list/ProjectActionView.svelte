<script lang="ts">
import type { ProjectAction } from '../model';
import TooltipTarget from '../../tooltip/TooltipTarget.svelte';
import { Direction } from '../../tooltip/model';

export let action: ProjectAction;
export let onClick: (action: ProjectAction) => void;
export let tooltip: string | undefined = undefined;

function onActionClick() {
    onClick(action);
}
</script>

<!--Use div here instead of button to avoid the modal is dismissed when click-->
<div role="button" tabindex="0" on:click="{onActionClick}" on:keydown="{(e) => e.key === 'Enter' && onActionClick()}">
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
