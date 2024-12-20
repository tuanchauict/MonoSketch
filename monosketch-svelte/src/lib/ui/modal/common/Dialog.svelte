<!--
  - Copyright (c) 2024, tuanchauict
  -->

<script lang="ts">
    export let title: string;
    export let content: string;

    export let confirmText: string = "Confirm";
    export let cancelText: string = "Cancel";
    export let onConfirm: () => void;
    export let onCancel: () => void;
    export let onDismiss: () => void;

    function handleConfirm() {
        onConfirm();
        onDismiss();
    }

    function handleCancel() {
        onCancel();
        onDismiss();
    }

</script>

<div class="modal" role="button" tabindex="-1"
     on:click="{onDismiss}"
     on:keydown="{(e) => e.key === 'Escape' && onDismiss()}"
>
    <div class="modal-content">
        {#if title}
            <h2>{title}</h2>
        {/if}
        {#if content}
            <p>{content}</p>
        {/if}
        <button on:click="{handleConfirm}">{confirmText}</button>
        <button on:click="{handleCancel}">{cancelText}</button>
    </div>
</div>

<style>
    .modal {
        display: flex;
        justify-content: center;
        align-items: center;
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.2);
    }

    .modal-content {
        background-color: white;
        padding: 20px;
        border-radius: 5px;
        text-align: center;
    }
</style>