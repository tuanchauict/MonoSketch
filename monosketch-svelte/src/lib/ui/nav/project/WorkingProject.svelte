<script lang="ts">
    import WorkingProjectView from './WorkingProjectView.svelte';
    import { LifecycleOwner } from '$libs/flow';
    import { onMount } from 'svelte';
    import { getAppContext } from "$mono/common/constant";
    import { getProjectDataViewModel } from "$ui/nav/project/constants";

    const appContext = getAppContext();
    const projectDataViewModel = getProjectDataViewModel();

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
