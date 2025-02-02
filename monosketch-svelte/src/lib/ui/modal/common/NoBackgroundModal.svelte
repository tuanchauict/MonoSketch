<!--
Elements inside this modal should not block click event propagation.
This helps the modals identify if the click event was inside the modal or outside to
dismiss the modal correctly.
-->

<script lang="ts">
    import { onMount } from 'svelte';
    import Portal from "$ui/modal/common/Portal.svelte";

    export let onDismiss: () => void;
    export let left: number = 0;
    export let top: number = 0;
    export let width: number = 0;
    export let height: number | null = null;
    export let customStyle: string = '';

    let timeout: number | null = null;
    let checkbox: HTMLInputElement | null = null;
    $: adjustedLeft = left + width > window.innerWidth ? window.innerWidth - width : left;

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
</script>
<Portal>
    {#if customStyle}
        <div
                class="modal"
                role="button"
                tabindex="-1"
                on:click="{onClick}"
                on:keydown="{onKeyDown}"
                on:focusin="{onFocusIn}"
                on:focusout="{onFocusOut}"
                style="{customStyle}"
        >
            <input type="checkbox" bind:this="{checkbox}"/>
            <slot/>
        </div>
    {:else }
        <div
                class="modal"
                role="button"
                tabindex="-1"
                on:click="{onClick}"
                on:keydown="{onKeyDown}"
                on:focusin="{onFocusIn}"
                on:focusout="{onFocusOut}"
                style:left="{adjustedLeft}px"
                style:top="{top}px"
                style:width="{width}px"
                style:height="{height}px"
        >
            <input type="checkbox" bind:this="{checkbox}"/>
            <slot/>
        </div>
    {/if}
</Portal>
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
