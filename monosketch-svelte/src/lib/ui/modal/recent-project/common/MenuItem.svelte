<script lang="ts">
export let danger = false;

export let onClick = () => {};

// Set this if you want to dismiss the menu when the item is clicked
export let dismiss: () => void = () => {};

function onItemClick() {
    onClick();
    dismiss();
}
</script>

<div class="row" class:normal="{!danger}" class:danger>
    <div class="icon"  on:click="{onItemClick}">
        <slot name="icon" />
    </div>
    <div class="content"  on:click="{onItemClick}">
        <slot name="content" />
    </div>
    <div class="actions">
        <slot name="actions" />
    </div>
</div>

<style lang="scss">
@import '../../../../style/variables.scss';

.row {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-between;

    height: 26px;
    padding: 0 6px;
    margin: 2px 8px 0;
    border-radius: 4px;
    cursor: pointer;

    color: var(--modal-content-color);
    font-family: $monospaceFont;
    font-size: 14px;

    &.normal {
        &:hover {
            background: var(--mainmenu-item-hover-bg);

            .actions {
                display: flex;
            }
        }

        .actions {
            display: none;
        }
    }

    &.danger {
        background: var(--danger-item-bg);
        color: white;
    }
}

.content {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    flex-grow: 1;
    height: 100%;
}

.icon {
    display: flex;
    width: 30px;
    height: 100%;
    align-items: center;
    justify-content: flex-start;
    opacity: 0.7;
}
</style>
