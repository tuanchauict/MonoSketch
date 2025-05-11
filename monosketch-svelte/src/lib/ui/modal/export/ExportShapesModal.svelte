<!--
  - Copyright (c) 2025, tuanchauict
  -->

<script lang="ts">
    import { onMount } from 'svelte';
    import CloseButton from "$ui/modal/export/CloseButton.svelte";
    import ExportContent from "$ui/modal/export/ExportContent.svelte";
    import Title from "$ui/modal/export/Title.svelte";

    export let text: string;
    export let onDismiss: () => void;

    let isVisible = false;

    $: console.log(isVisible);

    onMount(() => {
        // Delay the visibility change to allow CSS transition to take effect
        window.requestAnimationFrame(() => {
            isVisible = true;
        });
    });

    function onBackgroundClick(event: MouseEvent) {
        event.stopPropagation();
        event.preventDefault();
        dismiss();
    }

    function dismiss() {
        isVisible = false;
        setTimeout(() => {
            onDismiss();
        }, 300);
    }
</script>

<div class="modal" class:in={isVisible} class:out={!isVisible}>
    <div aria-label="Close modal" class="background" on:click={onBackgroundClick} role="presentation"></div>

    <div class="body">
        <CloseButton onClick="{dismiss}"/>
        <Title/>
        <ExportContent {text}/>
    </div>
</div>

<style lang="scss">
    .modal {
        position: fixed;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
        backdrop-filter: blur(2px);

        opacity: 0;
        transition: opacity 0.3s ease;

        &.in {
            opacity: 1;
        }

        &.out {
            opacity: 0;
        }
    }

    .background {
        background: var(--modal-background-bg);
        position: fixed;
        left: 0;
        right: 0;
        top: 0;
        bottom: 0;
    }

    .body {
        position: fixed;
        width: 100%;
        top: 100px;
        bottom: 0;

        display: flex;
        flex-direction: column;

        background: var(--exporttext-container-bg);
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);

        transform: translateY(50px);
        transition: transform 0.3s ease, opacity 0.2s ease-in-out;

        .in & {
            transform: translateY(0);
            opacity: 1;
        }

        .out & {
            transform: translateY(50px);
            opacity: 0;
        }
    }
</style>
