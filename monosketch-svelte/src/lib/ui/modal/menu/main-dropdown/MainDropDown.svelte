<script lang="ts">
    import NoBackgroundModal from "../../common/NoBackgroundModal.svelte";
    import {modalViewModel} from "../../viewmodel";
    import MenuItem from "./MenuItem.svelte";
    import FontAdjustmentItem from "./FontAdjustmentItem.svelte";

    export let target: HTMLElement;

    let left = target.offsetLeft;
    let top = target.offsetTop + target.offsetHeight + 7;
    let width = 180;

    function onDismiss() {
        modalViewModel.mainDropDownMenuTargetFlow.value = null;
    }

    function onFontSizeChange(isIncreased: boolean) {
        console.log(isIncreased)
    }

    function toggleFormatPanelVisibility() {
        // TODO: implement
        onDismiss();
    }

    function showKeyboardShortcuts() {
        onDismiss();
    }
</script>

<NoBackgroundModal {onDismiss} {left} {top} {width}>
    <div class="container">
        <FontAdjustmentItem onChange={onFontSizeChange}/>
        <MenuItem title="Hide Formal panel" onClick="{toggleFormatPanelVisibility}"/>
        <MenuItem title="Keyboard shortcuts" onClick="{showKeyboardShortcuts}"/>
    </div>
</NoBackgroundModal>

<style lang="scss">
    @import "../../../../style/variables.scss";

    .container {
        border-radius: 6px;
        background: var(--modal-container-bg);
        border: 1px solid var(--mainmenu-divider-color);
        padding: 6px 0;
        font-family: $monospaceFont;
        width: 100%;
        margin-right: 4px;
    }
</style>
