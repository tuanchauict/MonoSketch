<script lang="ts">
    export let selected: boolean;
    export let onClick: () => void;

    $: strokeWidth = selected ? 32 : 25;
</script>

<button class:selected={selected} on:click={onClick}>
    <svg width="26" height="26" viewBox="0 0 374 512" fill="none" stroke="currentColor">
        <path stroke-width="{strokeWidth}"
              d="M16 146V55c0-22 17-39 39-39h91M228 16h91c22 0 39 17 39 39v91M146 496H55c-22 0-39-17-39-39v-91M358 366v91c0 22-17 39-39 39h-91"/>
    </svg>
</button>

<style lang="scss">
    $border-color: var(--comp-option-cloud-border-color);
    $border-selected-color: var(--comp-option-cloud-border-selected-color);

    button {
        display: flex;
        padding: 3px 0;
        margin: 0;
        width: 26px;
        height: 32px;
        color: inherit;
        background: none;
        border: transparent;
        border-radius: 4px;
        cursor: pointer;
        opacity: 0.75;

        &:hover {
            outline: 1px solid var(--text-input-border-color);
            opacity: 1;
        }

        &.selected {
            opacity: 1;
            color: $border-selected-color;
        }
    }
</style>
