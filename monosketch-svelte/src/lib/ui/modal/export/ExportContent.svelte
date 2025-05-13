<!--
- Copyright (c) 2025, tuanchauict
-->
<script lang="ts">
    import CopyButton from "$ui/modal/export/CopyButton.svelte";

    export let text: string;

    let preElement: HTMLPreElement;
    let textArea: HTMLTextAreaElement;

    function copyContent() {
        if (!preElement || !textArea) {
            return;
        }

        textArea.value = preElement.innerText;
        textArea.select();
        document.execCommand('copy');
    }
</script>


<div class="content">
    <pre bind:this={preElement} contenteditable="true">{text}</pre>
    <textarea class="hidden" bind:this={textArea} value={text}></textarea>

    <CopyButton onClick={copyContent}/>
</div>

<style lang="scss">
    .content {
        height: calc(100% - 24px - 16px - 20px);
        color: var(--exporttext-content-color);
        position: relative;

        pre {
            width: 100%;
            height: 100%;
            padding: 32px 32px 64px;
            margin: 0;
            font-size: 13px;
            line-height: 18px;
            outline: none;
            overflow: auto;
            font-family: monospace;
        }
    }

    .hidden {
        display: none;
    }
</style>
