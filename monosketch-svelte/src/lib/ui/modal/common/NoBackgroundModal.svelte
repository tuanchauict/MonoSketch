<script lang="ts">
import { onMount } from 'svelte';

export let onDismiss: () => void;
export let left: number;
export let top: number;
export let width: number;
export let height: number | null = null;

let adjustedLeft = left + width > window.innerWidth ? window.innerWidth - width : left;

let timeout: number | null = null;
let checkbox: HTMLInputElement | null = null;

let style = [
    `left: ${adjustedLeft}px`,
    `top: ${top}px`,
    `width: ${width}px`,
    height ? `height: ${height}px` : null,
]
    .filter(Boolean)
    .join(';');

onMount(() => {
    if (checkbox) {
        checkbox.focus();
    }
});

function onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Escape') {
        dismiss();
    }
}

function onFocusIn() {
    if (timeout) {
        clearTimeout(timeout);
    }
}

function onFocusOut() {
    if (document.hasFocus()) {
        timeout = setTimeout(dismiss, 20);
    }
}

function dismiss() {
    checkbox = null;
    onDismiss();
}
</script>

<div
    class="modal"
    tabindex="-1"
    on:keydown="{onKeyDown}"
    on:focusin="{onFocusIn}"
    on:focusout="{onFocusOut}"
    {style}
>
    <input type="checkbox" bind:this="{checkbox}" />
    <slot />
</div>

<style>
.modal {
    position: fixed;
    display: flex;
    justify-content: center;
    align-items: center;
}

input[type='checkbox'] {
    position: fixed;
    left: -1000px;
    width: 0;
    height: 0;
}
</style>
