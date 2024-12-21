<!--
  - Copyright (c) 2024, tuanchauict
  -->

<script lang="ts">
    export let title: string;
    export let content: string = "";

    export let confirmText: string = "Confirm";
    export let cancelText: string = "Cancel";
    export let onConfirm: () => void;
    export let onCancel: () => void;

    export let dismissOnClickingOutside: boolean = true;

    export let onDismiss: () => void;

    function handleConfirm() {
        onConfirm();
        onDismiss();
    }

    function handleCancel() {
        onCancel();
        onDismiss();
    }

    function handleClickOutside(event: Event) {
        if (dismissOnClickingOutside && event.target === event.currentTarget) {
            onDismiss();
        }
    }
</script>

<div class="dialog-modal" role="button" tabindex="-1"
     on:click="{handleClickOutside}"
     on:keydown="{(e) => e.key === 'Escape' && handleClickOutside(e)}"
>
    <div class="modal-content" role="button" tabindex="-1"
         on:click|preventDefault|stopPropagation on:keydown>
        {#if title}
            <h2>{title}</h2>
        {/if}
        {#if content}
            <p class:no-title={title.length === 0}>{content}</p>
        {:else }
            <slot />
        {/if}
        <div class="actions">
            {#if cancelText}
                <button class="secondary" on:click="{handleCancel}">{cancelText}</button>
            {/if}
            {#if confirmText}
                <button class="primary" on:click="{handleConfirm}">{confirmText}</button>
            {/if}
        </div>
    </div>
</div>

<style lang="scss">
    @import "../../../style/variables";

    .dialog-modal {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: var(--modal-background-bg);
    }

    .modal-content {
        background: var(--modal-container-bg);
        border: 1px solid var(--modal-container-bg);
        min-width: 350px;
        max-width: 450px;
        border-radius: 5px;
        padding: 12px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
        font-family: $monospaceFont;

        display: flex;
        flex-direction: column;
    }

    h2 {
        font-size: 19px;
        margin: 4px 8px 8px;
        color: var(--modal-title-color);
    }

    p {
        font-size: 14px;
        line-height: 1.4;
        margin: 0 8px 8px;
        color: var(--contentText);

        &.no-title {
            font-size: 16px;
        }
    }

    .actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 16px;
    }

    button {
        margin-left: 8px;
        font-size: 14px;
        cursor: pointer;
        user-select: none;
        border-radius: 4px;
        border: 1px solid transparent;
        padding: 4px 8px;
        background: none;

        &.primary {
            background: var(--primary-action-color);
            color: white;
        }

        &.secondary {
            border-color: var(--secondary-action-border-color);
            color: var(--secondary-action-text-color);
        }

        &:hover {
            &.primary {
                background: var(--primary-action-hover-color);
            }

            &.secondary {
                background-color: var(--secondary-action-hover-color);
                color: white;
                border-color: transparent;
            }
        }

        &:active {
            &.primary {
                background: var(--primary-action-pressed-color);
            }

            &.secondary {
                background-color: var(--secondary-action-pressed-color);
                color: white;
                border-color: transparent;
            }
        }
    }
</style>