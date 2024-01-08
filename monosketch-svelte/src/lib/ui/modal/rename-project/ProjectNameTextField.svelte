<script lang="ts">
import { onMount } from 'svelte';

export let name: string;

let textInput: HTMLInputElement;

onMount(() => {
    setTimeout(() => {
        textInput.focus();
    }, 10);
});
</script>

<div>
    <input type="text" bind:value="{name}" bind:this={textInput}/>
</div>

<style lang="scss">
@import '../../../style/variables.scss';

div {
    display: flex;
    width: 100%;
    border-radius: 4px;
    background-color: var(--modal-container-bg);
}

input[type='text'] {
    border-radius: 3px;
    font-family: $monospaceFont;
    padding: 4px 8px;
    border: 2px solid var(--text-input-border-color);

    &:focus {
        outline: none;
        border-color: var(--text-input-border-focus-color);
    }
}
</style>
