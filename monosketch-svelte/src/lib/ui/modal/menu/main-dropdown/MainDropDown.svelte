<script lang="ts">
    import MenuItem from '../common/MenuItem.svelte';
    import FontAdjustmentItem from './FontAdjustmentItem.svelte';
    import DropDown from '../common/DropDown.svelte';
    import { getAppContext } from '$mono/common/constant';
    import type { Rect } from '$libs/graphics-geo/rect';
    import { MainMenuAction } from "$ui/modal/menu/main-dropdown/model";

    export let targetBounds: Rect;
    export let onAction: (action: MainMenuAction) => void;
    export let onDismiss: () => void;

    const appContext = getAppContext();

    let isFormatPanelVisible = appContext.appUiStateManager.shapeFormatPanelVisibilityFlow.value;
    $: toggleFormatPannelVisibilityText = isFormatPanelVisible
            ? 'Hide Format panel'
            : 'Show Format panel';

    function onFontSizeChange(isIncreased: boolean) {
        if (isIncreased) {
            onAction(MainMenuAction.IncreaseFontSize);
        } else {
            onAction(MainMenuAction.DecreaseFontSize);
        }
    }

    function toggleFormatPanelVisibility() {
        if (isFormatPanelVisible) {
            onAction(MainMenuAction.HideFormatPanel);
        } else {
            onAction(MainMenuAction.ShowFormatPanel);
        }
    }

    function showKeyboardShortcuts() {
        onAction(MainMenuAction.ShowKeyboardShortcut);
    }
</script>

<DropDown {targetBounds} {onDismiss} verticalOffsetPx="{7}">
    <FontAdjustmentItem onChange="{onFontSizeChange}"/>
    <MenuItem
            title="{toggleFormatPannelVisibilityText}"
            onClick="{toggleFormatPanelVisibility}"
            dismiss="{onDismiss}"
    />
    <MenuItem title="Keyboard shortcuts" onClick="{showKeyboardShortcuts}" dismiss="{onDismiss}"/>
</DropDown>
