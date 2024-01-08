<script lang="ts">
import MenuItem from '../common/MenuItem.svelte';
import FileActions from './FileActions.svelte';
import FileIcon from './FileIcon.svelte';
import type { FileAction } from '../model';

export let name: string;
export let opening: boolean;

function openFile() {
    console.log('open file');
}
function onAction(action: FileAction) {
    console.log('clicked', action);
}
</script>

<MenuItem onClick="{openFile}">
    <FileIcon slot="icon" {opening} />
    <span slot="content">{name}</span>
    <FileActions slot="actions" {onAction}/>
</MenuItem>
