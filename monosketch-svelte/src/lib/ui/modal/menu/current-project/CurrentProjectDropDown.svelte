<script lang="ts">
import DropDown from '../common/DropDown.svelte';
import MenuItem from '../common/MenuItem.svelte';
import { modalViewModel } from '../../viewmodel';
import type { CurrentProjectModel } from './model';

export let model: CurrentProjectModel;

function onDismiss() {
    modalViewModel.currentProjectDropDownMenuTargetFlow.value = null;
}

function rename() {
    modalViewModel.renamingProjectModalStateFlow.value = model;
}

function saveAs() {
    console.log('saveAs');
}

function exportText() {
    console.log('exportText');
}
</script>

<DropDown targetBounds="{model.targetBounds}" {onDismiss} verticalOffsetPx="{6}">
    <MenuItem title="Rename" onClick="{rename}" dismiss="{onDismiss}" />
    <MenuItem title="Save As" onClick="{saveAs}" dismiss="{onDismiss}" />
    <MenuItem title="Export Text" onClick="{exportText}" dismiss="{onDismiss}" />
</DropDown>
