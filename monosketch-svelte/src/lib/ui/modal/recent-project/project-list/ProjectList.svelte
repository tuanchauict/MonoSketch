<script lang="ts">
import ProjectRow from './ProjectRow.svelte';
import { ProjectAction, type ProjectItem } from '../model';
import { onDestroy, onMount } from 'svelte';
import { LifecycleOwner } from '$libs/flow';
import { projectDataViewModel } from '../viewmodel';
import {BrowserManager} from "$mono/window/browser-manager";

export let dismiss: () => void;

const lifecycleOwner = new LifecycleOwner();
let projectList: ProjectItem[] = [];
let openingId: string = '';
let deletingId: string = '';

onMount(() => {
    lifecycleOwner.onStart();

    projectDataViewModel.projectFlow.observe(lifecycleOwner, (list) => {
        projectList = list;
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

function onAction(item: ProjectItem, action: ProjectAction) {
    switch (action) {
        case ProjectAction.Open:
            projectDataViewModel.openProject(item.id);
            dismiss();
            break;
        case ProjectAction.OpenInNewTab:
            // Need to delay for a short time the action to ensure the modal is closed before opening the new tab
            setTimeout(() => BrowserManager.openInNewTab(item.id), 10);
            dismiss();
            break;
        case ProjectAction.Remove:
            projectDataViewModel.confirmDeletingProject(item.id);
            break;
        case ProjectAction.RemoveConfirmed:
            projectDataViewModel.deleteProject(item.id);
            break;
        case ProjectAction.CancelRemove:
            projectDataViewModel.cancelDeletingProject();
            break;
    }
}
</script>

<div>
    {#each projectList as project (project.id)}
        <ProjectRow
            {project}
            opening="{project.id === openingId}"
            deleting="{project.id === deletingId}"
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
