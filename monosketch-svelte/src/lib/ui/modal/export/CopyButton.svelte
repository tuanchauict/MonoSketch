<!--
  - Copyright (c) 2025, tuanchauict
  -->
<script lang="ts">
    import SvgIcon from "$ui/common/SvgIcon.svelte";

    export let onClick: () => void;
</script>

<button class="copy-btn" title="Copy" on:click={onClick}>
    <SvgIcon size={24}>
        <path d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
    </SvgIcon>
</button>

<style lang="scss">
    .copy-btn {
        position: absolute;
        left: 8px;
        bottom: 4px;
        padding: 8px;
        cursor: pointer;
        color: rgba(0, 0, 0, 0.3);
        background: none;
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;

        &:hover {
            color: rgba(0, 0, 0, 0.6);
        }

        &:active {
            color: rgba(0, 0, 0, 0.4);
        }

        &:focus-visible {
            outline: 2px solid #000;
            outline-offset: 2px;
        }
    }
</style>
