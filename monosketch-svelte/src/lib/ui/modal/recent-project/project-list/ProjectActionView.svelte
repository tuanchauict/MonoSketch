<script lang="ts">
import type { ProjectAction } from '$ui/nav/project/model';
import TooltipTarget from '../../tooltip/TooltipTarget.svelte';
import { TooltipDirection } from '../../tooltip/model';

export let action: ProjectAction;
export let onClick: (action: ProjectAction) => void;
export let tooltip: string | undefined = undefined;

function onActionClick() {
    onClick(action);
}
</script>

<button on:click="{onActionClick}" on:keydown="{(e) => e.key === 'Enter' && onActionClick()}">
    {#if tooltip}
        <TooltipTarget text="{tooltip}" direction="{TooltipDirection.TOP}" offsetVertical="{4}">
            <slot />
        </TooltipTarget>
    {:else}
        <slot />
    {/if}
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
