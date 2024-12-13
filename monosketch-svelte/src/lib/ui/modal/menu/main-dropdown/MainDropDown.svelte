<script lang="ts">
import MenuItem from '../common/MenuItem.svelte';
import FontAdjustmentItem from './FontAdjustmentItem.svelte';
import DropDown from '../common/DropDown.svelte';
import { getContext } from 'svelte';
import type { AppContext } from '$app/app-context';
import { APP_CONTEXT } from '$mono/common/constant';
import { modalViewModel } from '$ui/modal/viewmodel';
import type { Rect } from '$libs/graphics-geo/rect';
import {UiStatePayload} from "$mono/ui-state-manager/ui-state-payload";

export let targetBounds: Rect;

const appContext = getContext<AppContext>(APP_CONTEXT);

let isFormatPanelVisible = appContext.appUiStateManager.shapeFormatPanelVisibilityFlow.value;
$: toggleFormatPannelVisibilityText = isFormatPanelVisible
    ? 'Hide Format panel'
    : 'Show Format panel';

function onDismiss() {
    modalViewModel.mainDropDownMenuTargetFlow.value = null;
}

function onFontSizeChange(isIncreased: boolean) {
    appContext.appUiStateManager.updateUiState(UiStatePayload.ChangeFontSize(isIncreased));
}

function toggleFormatPanelVisibility() {
    appContext.appUiStateManager.updateUiState(UiStatePayload.ShapeToolVisibility(!isFormatPanelVisible));
}

function showKeyboardShortcuts() {
    modalViewModel.keyboardShortcutVisibilityStateFlow.value = true;
}
</script>

<DropDown {targetBounds} {onDismiss} verticalOffsetPx="{7}">
    <FontAdjustmentItem onChange="{onFontSizeChange}" />
    <MenuItem
        title="{toggleFormatPannelVisibilityText}"
        onClick="{toggleFormatPanelVisibility}"
        dismiss="{onDismiss}"
    />
    <MenuItem title="Keyboard shortcuts" onClick="{showKeyboardShortcuts}" dismiss="{onDismiss}" />
</DropDown>
