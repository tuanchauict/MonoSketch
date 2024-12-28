<!--
Elements inside this modal should not block click event propagation.
This helps the modals identify if the click event was inside the modal or outside to
dismiss the modal correctly.
-->

<script lang="ts">
import { onMount } from 'svelte';

export let onSubmit: () => void = () => {};
export let onDismiss: () => void;
export let left: number = 0;
export let top: number = 0;
export let width: number = 0;
export let height: number | null = null;
export let style: string = '';

let timeout: number | null = null;
let checkbox: HTMLInputElement | null = null;

let displayedStyle = style ? style : createStyle();
onMount(() => {
    if (checkbox) {
        checkbox.focus();
    }
});

function onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Escape') {
        dismiss();
    } else if (event.key === 'Enter') {
        submit();
    }
}

function onClick() {
    if (!timeout) {
        return;
    }
    clearTimeout(timeout);
    timeout = null;

    // Ensure the modal is focused when clicked inside.
    if (document.hasFocus()) {
        checkbox?.focus();
    }
}

function onFocusIn() {
    if (timeout) {
        clearTimeout(timeout);

        timeout = null;
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

function submit() {
    checkbox = null;
    onSubmit();
    onDismiss();
}

function createStyle() {
    let adjustedLeft = left + width > window.innerWidth ? window.innerWidth - width : left;
    return [
        `left: ${adjustedLeft}px`,
        `top: ${top}px`,
        `width: ${width}px`,
        height ? `height: ${height}px` : null,
    ]
        .filter(Boolean)
        .join(';');
}
</script>

<div
    class="modal"
    role="button"
    tabindex="-1"
    on:click="{onClick}"
    on:keydown="{onKeyDown}"
    on:focusin="{onFocusIn}"
    on:focusout="{onFocusOut}"
    style="{displayedStyle}"
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
