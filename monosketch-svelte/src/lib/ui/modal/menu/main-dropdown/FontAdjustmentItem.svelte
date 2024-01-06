<script lang="ts">
export let onChange: (isIncreased: boolean) => void;

const increaseFont = () => onChange(true);
const decreaseFont = () => onChange(false);
</script>

<div class="container">
    <div class="item decrease" on:click="{decreaseFont}">A</div>
    <div class="item increase" on:click="{increaseFont}">A</div>
</div>

<style lang="scss">
.container {
    display: flex;
    flex-direction: row;
    align-items: center;
}

.item {
    width: 50%;
    height: 28px;
    flex-grow: 0;
    flex-shrink: 0;
    margin-bottom: 2px;
    color: var(--mainmenu-item-color);
    user-select: none;
    cursor: pointer;

    display: flex;
    align-items: flex-end;
    justify-content: center;

    &:hover {
        background: var(--mainmenu-item-hover-bg);
    }

    &:active {
        background: var(--mainmenu-item-action-hover-bg);
    }
}

.decrease {
    font-size: 14px;
    padding-bottom: 2px;
}

.increase {
    font-size: 18px;
}
</style>
