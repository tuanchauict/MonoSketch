<script lang="ts">
    import ProjectRow from './ProjectRow.svelte';
    import { ProjectAction, type ProjectItem } from '$ui/nav/project/model';
    import { onDestroy, onMount } from 'svelte';
    import { LifecycleOwner } from '$libs/flow';
    import { BrowserManager } from "$mono/window/browser-manager";
    import { getProjectDataViewModel } from "$ui/nav/project/constants";

    export let dismiss: () => void;

const projectDataViewModel = getProjectDataViewModel();

let projectList: ProjectItem[] = [];
let openingId: string = '';
let deletingId: string = '';

onMount(() => {
    const lifecycleOwner = LifecycleOwner.start();

    // Manually update the project list to ensure the list is up-to-date when the modal is opened
    projectDataViewModel.updateProjectList();
    projectDataViewModel.projectFlow.observe(lifecycleOwner, (list) => {
        projectList = list;
    });

    projectDataViewModel.openingProjectFlow.observe(lifecycleOwner, (project) => {
        openingId = project.id;
    });

    projectDataViewModel.deletingProjectIdFlow.observe(lifecycleOwner, (id) => {
        deletingId = id;
    });

    return () => {
        lifecycleOwner.onStop();
    };
});

onDestroy(() => {
    projectDataViewModel.cancelDeletingProject();
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
