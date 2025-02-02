<script lang="ts">
    import WorkingProjectView from './WorkingProjectView.svelte';
    import { Flow, LifecycleOwner } from '$libs/flow';
    import { getContext, onMount } from 'svelte';
    import type { ProjectDataViewModel } from "$ui/nav/project/project-data-viewmodel";
    import { PROJECT_CONTEXT } from "$ui/nav/project/constants";

    const projectDataViewModel = getContext<ProjectDataViewModel>(PROJECT_CONTEXT);

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
    <WorkingProjectView {projectId} {projectName}/>
</div>

<style lang="scss">
    div {
        display: flex;
    }
</style>
