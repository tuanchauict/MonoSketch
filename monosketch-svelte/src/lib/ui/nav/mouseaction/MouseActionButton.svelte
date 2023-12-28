<script lang="ts">
    import {getIconPath, type MouseActionType} from "./model";

    export let type: MouseActionType;
    export let onSelect: (type: MouseActionType) => void;
    export let selected: boolean = false;

    function onClick() {
        onSelect(type);
    }
</script>

<div>
    <button class="action" class:selected={selected} on:click={onClick}>
        <svg viewBox="0 0 24 24" width="21" height="21" fill="currentColor">
            <path d="{getIconPath(type)}"/>
        </svg>
    </button>
</div>

<style lang="scss">
div {
    display: flex;
    justify-content: center;
    align-items: center;
}

button {
    background: none;

    display: flex;
    align-items: center;
    justify-content: center;
    width: 42px;
    height: 32px;
    border-radius: 6px;
    margin-left: 6px;
    border: 1px solid transparent;
    cursor: pointer;
    user-select: none;
    color:inherit;

    &:hover {
        background: var(--nav-action-hover-bg);
    }

    &.selected {
        border-color: var(--nav-action-selected-border);
    }
}
</style>
