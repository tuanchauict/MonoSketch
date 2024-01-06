<script lang="ts">
import { buttonContent, type ReorderType } from './model';

export let icon: ReorderType;
export let onClick: (icon: ReorderType) => void;
</script>

<button
    on:click="{() => {
        onClick(icon);
    }}"
>
    <svg width="18" height="18" viewBox="0 0 18 18" fill="currentColor">
        <path d="{buttonContent[icon].icon}"></path>
    </svg>
</button>

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
