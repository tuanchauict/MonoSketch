<!--
  - Copyright (c) 2025, tuanchauict
  -->

<script lang="ts">
    import WorkingProject from "$ui/nav/project/WorkingProject.svelte";
    import ProjectManagerButton from "$ui/nav/project/ProjectManagerButton.svelte";
    import ToolbarContainer from "$ui/nav/common/ToolbarContainer.svelte";
    import { ProjectDataViewModel } from "$ui/nav/project/project-data-viewmodel";
    import { getContext, setContext } from "svelte";
    import { PROJECT_CONTEXT } from "$ui/nav/project/constants";
    import { APP_CONTEXT } from "$mono/common/constant";

    const projectDataViewModel = new ProjectDataViewModel(getContext(APP_CONTEXT));
    setContext(PROJECT_CONTEXT, projectDataViewModel);
</script>

<ToolbarContainer>
    <ProjectManagerButton/>
    <WorkingProject/>
</ToolbarContainer>
