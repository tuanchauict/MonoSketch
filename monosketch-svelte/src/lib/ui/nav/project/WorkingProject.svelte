<script lang="ts">
import WorkingProjectView from './WorkingProjectView.svelte';
import { projectDataViewModel } from '$ui/modal/recent-project/viewmodel';
import { Flow, LifecycleOwner } from '$libs/flow';
import { onMount } from 'svelte';

let projectId = '';
let projectName = '';

// TODO: the current flow of showing the working project info and renaming project is not intuitive. Fix it!

const openingProjectFlow = Flow.combine2(
    projectDataViewModel.openingProjectIdFlow,
    projectDataViewModel.renamingProjectIdFlow,
    (id) => projectDataViewModel.getProject(id),
);

const renamingProjectFlow = projectDataViewModel.renamingProjectIdFlow.map((id) => {
    const project = projectDataViewModel.getProject(id);
    return project ? project : null;
});

onMount(() => {
    const lifecycleOwner = LifecycleOwner.start();
    openingProjectFlow.observe(lifecycleOwner, (project) => {
        // TODO: make the flow mapping ignore undefined value as return type.
        projectId = project!.id;
        projectName = project!.name;
    });

    renamingProjectFlow.observe(lifecycleOwner, (project) => {
        if (!project) {
            return;
        }
        projectDataViewModel.setRenamingProject(project.id);
    });

    return () => {
        lifecycleOwner.onStop();
    };
});
</script>

<div>
    <WorkingProjectView {projectId} {projectName} />
</div>

<style lang="scss">
div {
    display: flex;
}
</style>
