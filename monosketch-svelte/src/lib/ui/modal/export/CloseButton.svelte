<!--
  - Copyright (c) 2025, tuanchauict
  -->

<script lang="ts">
    import SvgIcon from "$ui/common/SvgIcon.svelte";

    export let onClick: () => void;
</script>

<button class="close-btn" on:click={onClick}>
    <SvgIcon size="{16}">
        <path d="M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z"/>
        <path d="M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z"/>
    </SvgIcon>
</button>

<style lang="scss">
    .close-btn {
        position: absolute;
        top: 8px;
        right: 8px;
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

        &:focus-visible {
            outline: 2px solid #000;
            outline-offset: 2px;
        }
    }
</style>
