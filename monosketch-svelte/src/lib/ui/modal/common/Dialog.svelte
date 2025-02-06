<!--
  - Copyright (c) 2024, tuanchauict
  -->

<script lang="ts">
    import { fade, slide } from "svelte/transition";
    import type { AlertDialogModel } from "$ui/modal/common/AlertDialog";

    export let model: AlertDialogModel;

    let title = model.title || '';
    let message = model.message || '';
    let primaryAction = model.primaryAction;
    let secondaryAction = model.secondaryAction;
    let dismissOnClickingOutside = model.dismissOnClickingOutside ?? true;
    let onDismiss = model.onDismiss || (() => {});

    function handlePrimaryAction() {
        primaryAction?.onClick();
        onDismiss();
    }

    function handleSecondaryAction() {
        secondaryAction?.onClick();
        onDismiss();
    }

    function handleClickOutside(event: Event) {
        if (dismissOnClickingOutside && event.target === event.currentTarget) {
            onDismiss();
        }
    }
</script>
<div class="dialog-modal" role="button" tabindex="-1"
     transition:fade={{ duration: 200 }}
     on:click="{handleClickOutside}"
     on:keydown="{(e) => e.key === 'Escape' && handleClickOutside(e)}"
>
    <div class="modal-content" role="button" tabindex="-1"
            transition:slide={{ duration: 200 }}
         on:click|preventDefault|stopPropagation on:keydown>
        {#if title}
            <h2>{title}</h2>
        {/if}
        {#if message}
            <p class:no-title={!title}>{message}</p>
        {:else }
            <slot/>
        {/if}
        {#if primaryAction || secondaryAction}
            <div class="actions">
                {#if secondaryAction}
                    <button class="secondary" on:click="{handleSecondaryAction}">{secondaryAction.text}</button>
                {/if}
                {#if primaryAction}
                    <button class="primary" on:click="{handlePrimaryAction}">{primaryAction.text}</button>
                {/if}
            </div>
        {/if}
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
        padding: 16px;
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
        font-family: $monospaceFont;
    }

    button {
        margin-left: 8px;
        font-size: 14px;
        cursor: pointer;
        user-select: none;
        border-radius: 4px;
        border: 1px solid transparent;
        padding: 4px 10px;
        background: none;
        min-width: 50px;
        font-weight: bold;
        font-family: $monospaceFont;

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
