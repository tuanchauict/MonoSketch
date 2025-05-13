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

<button on:click="{onItemClick}">
    {title}
</button>

<style lang="scss">
button {
    padding: 5px 8px 5px 16px;
    margin: 0;
    display: block;
    width: 100%;
    font-family: inherit;
    text-align: start;
    border: none;
    cursor: pointer;
    overflow: hidden;
    color: var(--mainmenu-item-color);
    background: none;
    white-space: nowrap;
    font-size: 14px;
    line-height: 21px;

    &:hover {
        background: var(--mainmenu-item-hover-bg);
    }
}
</style>
