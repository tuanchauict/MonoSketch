<script lang="ts">
import { onMount } from 'svelte';

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
    tabindex="-1"
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
