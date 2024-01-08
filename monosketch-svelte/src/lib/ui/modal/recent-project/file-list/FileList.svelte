<script lang="ts">
import FileRow from './FileRow.svelte';
import { FileAction, type FileItem } from '../model';
import { onDestroy, onMount } from 'svelte';
import { LifecycleOwner } from '../../../../mono/flow';
import { projectDataViewModel } from '../viewmodel';

export let dismiss: () => void;

const lifecycleOwner = new LifecycleOwner();
let fileList: FileItem[] = [];
let openingId: string = '';
let deletingId: string = '';

onMount(() => {
    lifecycleOwner.onStart();

    projectDataViewModel.projectFlow.observe(lifecycleOwner, (list) => {
        fileList = list;
    });

    projectDataViewModel.openingProjectIdFlow.observe(lifecycleOwner, (id) => {
        openingId = id;
    });

    projectDataViewModel.deletingProjectIdFlow.observe(lifecycleOwner, (id) => {
        deletingId = id;
    });
});

onDestroy(() => {
    projectDataViewModel.cancelDeletingProject();

    lifecycleOwner.onStop();
});

function onAction(file: FileItem, action: FileAction) {
    switch (action) {
        case FileAction.Open:
            projectDataViewModel.openProject(file.id);
            console.log('open');
            dismiss();
            break;
        case FileAction.OpenInNewTab:
            console.log('open in new tab');
            dismiss();
            break;
        case FileAction.Remove:
            projectDataViewModel.confirmDeletingProject(file.id);
            break;
        case FileAction.RemoveConfirmed:
            projectDataViewModel.deleteProject(file.id);
            break;
        case FileAction.CancelRemove:
            projectDataViewModel.cancelDeletingProject();
            break;
    }
}
</script>

<div>
    {#each fileList as file (file.id)}
        <FileRow
            {file}
            opening="{file.id === openingId}"
            deleting="{file.id === deletingId}"
            {onAction}
        />
    {:else}
        <p>No files</p>
    {/each}
</div>

<style lang="scss">
div {
    width: 100%;
    height: 100%;
    max-height: 200px;
    overflow-y: auto;
    padding: 8px 0;
}

p {
    width: 100%;
    height: 40px;

    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--shapetool-indicator-color);
}
</style>
