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
    <div role="button" tabindex="0" class="content-container"
         on:click="{onItemClick}"
         on:keydown="{(e) => e.key === 'Enter' && onItemClick()}">
        <div class="icon" >
            <slot name="icon" />
        </div>
        <div class="content">
            <slot name="content" />
        </div>
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

.content-container {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: flex-start;
    flex-grow: 1;
    height: 100%;
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
