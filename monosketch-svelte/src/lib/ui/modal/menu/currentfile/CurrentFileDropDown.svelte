<script lang="ts">
import DropDown from '../common/DropDown.svelte';
import MenuItem from '../common/MenuItem.svelte';
import type { TargetBounds } from '../../model';
import { modalViewModel } from '../../viewmodel';

export let targetBounds: TargetBounds;

function onDismiss() {
    modalViewModel.currentFileDropDownMenuTargetFlow.value = null;
}

function rename() {
    console.log('rename');
}

function saveAs() {
    console.log('saveAs');
}

function exportText() {
    console.log('exportText');
}
</script>

<DropDown {targetBounds} {onDismiss} verticalOffsetPx="{6}">
    <MenuItem title="Rename" onClick="{rename}" dismiss="{onDismiss}" />
    <MenuItem title="Save As" onClick="{saveAs}" dismiss="{onDismiss}" />
    <MenuItem title="Export Text" onClick="{exportText}" dismiss="{onDismiss}" />
</DropDown>
