<script lang="ts">
import { modalViewModel } from '../../viewmodel';
import MenuItem from '../common/MenuItem.svelte';
import FontAdjustmentItem from './FontAdjustmentItem.svelte';
import type { TargetBounds } from '../../model';
import DropDown from '../common/DropDown.svelte';

export let targetBounds: TargetBounds;

function onDismiss() {
    modalViewModel.mainDropDownMenuTargetFlow.value = null;
}

function onFontSizeChange(isIncreased: boolean) {
    console.log(isIncreased);
}

function toggleFormatPanelVisibility() {
    // TODO: implement
}

function showKeyboardShortcuts() {
    modalViewModel.keyboardShortcutVisibilityStateFlow.value = true;
}
</script>

<DropDown {targetBounds} {onDismiss} verticalOffsetPx="{7}">
    <FontAdjustmentItem onChange="{onFontSizeChange}" />
    <MenuItem
        title="Hide Formal panel"
        onClick="{toggleFormatPanelVisibility}"
        dismiss="{onDismiss}"
    />
    <MenuItem title="Keyboard shortcuts" onClick="{showKeyboardShortcuts}" dismiss="{onDismiss}" />
</DropDown>
