<script lang="ts">
export let title: string;
export let onClick: () => void;
// Set this if you want to dismiss the menu when the item is clicked
export let dismiss: () => void = () => {};

function onItemClick() {
    onClick();
    dismiss();
}
</script>

<div on:click="{onItemClick}">
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
