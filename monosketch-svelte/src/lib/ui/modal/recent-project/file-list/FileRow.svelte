<script lang="ts">
import MenuItem from '../common/MenuItem.svelte';
import FileActions from './FileActions.svelte';
import FileIcon from './FileIcon.svelte';
import { FileAction, type FileItem } from '../model';

export let file: FileItem;
export let opening: boolean;
export let deleting: boolean;
export let onAction: (file: FileItem, action: FileAction) => void;

function openFile() {
    onAction(file, FileAction.Open);
}

function onActionClick(action: FileAction) {
    console.log('clicked', action);
    onAction(file, action);
}
</script>

<MenuItem onClick="{openFile}" danger="{deleting}">
    <FileIcon slot="icon" {opening} {deleting} />
    <span slot="content">{file.name}</span>
    <FileActions slot="actions" onAction="{onActionClick}" confirmingRemove="{deleting}" />
</MenuItem>
