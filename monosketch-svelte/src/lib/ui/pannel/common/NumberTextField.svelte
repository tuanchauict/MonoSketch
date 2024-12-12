<script lang="ts">
export let label: string;
export let value: number;
export let minValue: number | null = null;
export let isEnabled = true;
export let boundIncludesLabel = false;
</script>

<div class:child-bounds="{!boundIncludesLabel}">
    <label for="">{label}</label>
    <input
        type="number"
        bind:value
        disabled="{!isEnabled}"
        min="{minValue !== null ? minValue : ''}"
    />
</div>

<style lang="scss">
div {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 1px 2px 1px 8px;
    border: 1px solid transparent;
    border-radius: 2px;

    &:hover {
        outline: 1px solid var(--text-input-border-color);
    }

    &:focus-within {
        outline: 1.5px solid var(--text-input-border-focus-color);
    }

    &.child-bounds {
        padding: 1px 0;

        &:hover {
            outline: none;
        }

        &:focus-within {
            outline: none;
        }

        input[type='number'] {
            padding-left: 4px;
            padding-right: 4px;
            border-radius: 2px;
            margin-left: 10px;

            &:hover {
                outline: 1px solid var(--text-input-border-color);
            }

            &:focus-within {
                outline: 2px solid var(--text-input-border-focus-color);
            }
        }
    }
}

label {
    font-size: 12px;
    font-weight: 300;
    user-select: none;
    color: var(--text-input-label-color);
}

input[type='number'] {
    height: 24px;
    width: 100%;
    margin-left: 12px;
    border: none;
    color: var(--text-input-value-color);
    background: transparent;
    font-size: 12px;

    &:focus,
    &:active {
        outline: none;
    }
}

input[type='number'] {
    appearance: textfield;

    // Webkit browsers like Safari, Chrome, etc.
    &::-webkit-inner-spin-button,
    &::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
    }
}
</style>
