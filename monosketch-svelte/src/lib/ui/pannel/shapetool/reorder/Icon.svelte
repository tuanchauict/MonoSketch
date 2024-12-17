<script lang="ts">
    import TooltipTarget from '$ui/modal/tooltip/TooltipTarget.svelte';
    import { Direction } from '$ui/modal/tooltip/model';

    export let title: string;
export let iconPath: string;
export let onClick: () => void;
</script>

<TooltipTarget text="{title}" direction="{Direction.TOP}">
    <button on:click="{onClick}">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="currentColor">
            <path d="{iconPath}"></path>
        </svg>
    </button>
</TooltipTarget>

<style lang="scss">
button {
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    user-select: none;

    padding: 8px;
    border-radius: 3px;
    background: none;
    border: none;
    color: inherit;

    &:hover {
        background: var(--shapetool-reorder-hover-bg);
    }
}
</style>
