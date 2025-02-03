<script lang="ts">
import { onMount } from 'svelte';

export let name: string;
export let onDone: () => void;

let textInput: HTMLInputElement;

onMount(() => {
    setTimeout(() => {
        textInput.focus();
    }, 10);
});

function onKeydown(event: KeyboardEvent) {
    if (event.key === 'Enter') {
        // Prevent the default behavior of the Enter key to avoid submitting the form
        event.preventDefault();
        onDone();
    } else if (event.key === 'Escape') {
        name = '';
        onDone();
        event.preventDefault();
    }
}
</script>

<div>
    <input type="text" bind:value="{name}" bind:this="{textInput}" on:keydown="{onKeydown}"/>
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
