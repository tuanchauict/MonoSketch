<script lang="ts">

export let selected: boolean;
export let iconPath: string;
export let onClick: () => void;

</script>

<button class:selected on:click="{onClick}">
    <svg width="20" height="14" viewBox="0 0 20 14" fill="currentColor">
        <path d="{iconPath}"></path>
    </svg>
</button>

<style lang="scss">
@import '../../../../style/variables.scss';

$border-color: var(--comp-option-cloud-border-color);
$border-selected-color: var(--comp-option-cloud-border-selected-color);

button {
    border: 1px solid $border-color;
    border-radius: 3px;
    background: none;
    padding: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    color: inherit;

    &:hover {
        border-color: transparent;
        outline: 1.5px solid $border-color;
    }

    &.selected {
        border-color: transparent;
        outline: 1.5px solid $border-selected-color;
    }
}
</style>
