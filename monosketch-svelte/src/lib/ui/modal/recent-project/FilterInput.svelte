<script lang="ts">
import { onMount } from 'svelte';

export let value: string;

let input: HTMLInputElement;

onMount(() => {
    input.focus();
});
</script>

<div>
    <input type="text" placeholder="Filter by name" bind:value bind:this="{input}" />
</div>

<style lang="scss">
@import '../../../style/variables.scss';

div {
    display: flex;
    margin-bottom: 8px;
    padding: 0 6px;
    width: 100%;
}

input[type='text'] {
    width: 100%;
    padding: 6px 8px;
    border-radius: 3px;
    outline: none;
    border: 1px solid var(--text-input-border-color);
    background: none;
    color: var(--text-input-value-color);
    font-family: $monospaceFont;

    &:hover,
    &:focus {
        border-color: var(--text-input-border-focus-color);
    }
}
</style>
