<script lang="ts">
    import WorkingProjectView from './WorkingProjectView.svelte';
    import { LifecycleOwner } from '$libs/flow';
    import { getContext, onMount } from 'svelte';
    import type { ProjectDataViewModel } from "$ui/nav/project/project-data-viewmodel";
    import { PROJECT_CONTEXT } from "$ui/nav/project/constants";
    import { APP_CONTEXT } from "$mono/common/constant";
    import { AppContext } from "$app/app-context";

    const appContext = getContext<AppContext>(APP_CONTEXT);
    const projectDataViewModel = getContext<ProjectDataViewModel>(PROJECT_CONTEXT);

    let projectId = '';
    let projectName = '';

    onMount(() => {
        const lifecycleOwner = LifecycleOwner.start();
        projectDataViewModel.openingProjectFlow.observe(lifecycleOwner, (project) => {
            // TODO: make the flow mapping ignore undefined value as return type.
            projectId = project!.id;
            projectName = project!.name;

            appContext.browserManager.updateAppTitle(projectName);
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
