<script lang="ts">
export let id: string;
export let selected: boolean;
export let onSelect: (id: string) => void;
// This makes the border of the option dashed instead of solid.
export let useDashBorder: boolean;

function onClick() {
    onSelect(id);
}
</script>

<button on:click="{onClick}" class:selected class:dash-border="{useDashBorder}">
    <slot />
</button>

<style lang="scss">
@import '../../../../../style/variables.scss';

$border-color: var(--comp-option-cloud-border-color);
$border-selected-color: var(--comp-option-cloud-border-selected-color);

button {
    font-size: 18px;
    font-family: $monospaceFont;

    display: flex;
    justify-content: center;
    align-items: center;
    border: 1px solid $border-color;
    border-radius: 3px;
    cursor: pointer;
    padding: 3px;
    height: 32px;
    color: var(--shapetool-tool-title-color);

    user-select: none;
    background: none;

    &:hover {
        border-color: transparent;
        outline: 1.5px solid $border-color;
    }

    &.selected {
        border-color: transparent;
        outline: 1.5px solid $border-selected-color;
        color: $border-selected-color;
    }

    &:first-child {
        margin-left: 0;
    }

    &:last-child {
        margin-right: 0;
    }
}

.dash-border {
    border-style: dashed;

    &:hover {
        outline-style: dashed;
    }

    &.selected {
        outline-style: dashed;
    }
}
</style>
