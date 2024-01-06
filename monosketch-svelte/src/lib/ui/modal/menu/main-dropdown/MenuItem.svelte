<script lang="ts">
export let title: string;
export let onClick: () => void;
</script>

<div on:click="{onClick}">
    {title}
</div>

<style lang="scss">
div {
    padding: 5px 8px 5px 16px;
    margin: 0;
    cursor: pointer;
    overflow: hidden;
    color: var(--mainmenu-item-color);
    white-space: nowrap;
    font-size: 14px;
    line-height: 21px;

    &:hover {
        background: var(--mainmenu-item-hover-bg);
    }
}
</style>
