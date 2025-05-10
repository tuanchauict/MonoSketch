<!--
  - Copyright (c) 2025, tuanchauict
  -->

<script lang="ts">
    import { onMount } from 'svelte';

    export let initialText: string = "";
    export let left: number = 0;
    export let top: number = 0;
    export let width: number = 100;
    export let height: number = 100;

    export let onTextChange: (text: string) => void = () => {
    };
    export let onDismiss: () => void = () => {
    };

    let textAreaContainer: HTMLDivElement;
    let textArea: HTMLDivElement;
    let mounted = false;

    onMount(() => {
        textArea.focus();
        insertText(initialText);
        mounted = true;

        const handleClickOutside = (event: MouseEvent) => {
            if (mounted && textAreaContainer && !textAreaContainer.contains(event.target as Node)) {
                onDismiss();
            }
        };

        document.addEventListener('mousedown', handleClickOutside);

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    });

    function handleKeyDown(event: KeyboardEvent) {
        // Command+Enter or Escape to dismiss
        if ((event.key === 'Enter' && (event.metaKey || event.ctrlKey)) || event.key === 'Escape') {
            event.preventDefault();
            onDismiss();
        }
    }

    function handleInput() {
        const html = textArea.innerHTML.replace(/(^<div>|<\/div>|<br\/?>\s*$)/g, "");
        const lines = html.split("<div>");
        const converterDiv = document.createElement('div');

        const text = lines.map(line => {
            converterDiv.innerHTML = line;
            return converterDiv.innerText;
        }).join("\n");

        onTextChange(text);
    }

    function handlePaste(event: ClipboardEvent) {
        event.preventDefault();
        event.stopPropagation();

        const text = event.clipboardData?.getData('text/plain') || '';
        insertText(text);
    }

    function insertText(text: string) {
        document.execCommand('insertText', false, text);
    }
</script>
<div class="modal">
    <div class="modal-edit-text-container"
         style="left: {left}px; top: {top}px; width: {width}px; height: {height}px"
         bind:this={textAreaContainer}>
        <div class="modal-edit-text-area"
             bind:this={textArea}
             contenteditable={true}
             role="textbox"
             aria-multiline="true"
             tabindex="-1"
             on:input={handleInput}
             on:paste={handlePaste}
             on:keydown={handleKeyDown}>
        </div>
    </div>
</div>

<style lang="scss">
    .modal {
        position: fixed;
        left: var(--canvas-left);
        top: calc(var(--canvas-top) + var(--nav-height));
    }

    .modal-edit-text-container {
        position: absolute;
        background-color: transparent;
        color: var(--workspace-edittext-color);
    }

    .modal-edit-text-area {
        position: absolute;
        top: 0.5px;
        width: 100%;
        height: 100%;
        outline: none;

        font-family: var(--monospace-font);
        font-size: 13px;
        line-height: 17px;

        white-space: pre-wrap;
        letter-spacing: 0.2px;
    }
</style>
