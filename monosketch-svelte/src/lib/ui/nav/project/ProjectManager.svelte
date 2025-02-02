<!--
  - Copyright (c) 2025, tuanchauict
  -->

<script lang="ts">
    import WorkingProject from "$ui/nav/project/WorkingProject.svelte";
    import ProjectManagerButton from "$ui/nav/project/ProjectManagerButton.svelte";
    import ToolbarContainer from "$ui/nav/common/ToolbarContainer.svelte";
    import { ProjectDataViewModel } from "$ui/nav/project/project-data-viewmodel";
    import { setContext } from "svelte";
    import { PROJECT_CONTEXT } from "$ui/nav/project/constants";
    import { sampleProjectItems } from "$ui/modal/recent-project/model";

    const projectDataViewModel = new ProjectDataViewModel();
    setContext(PROJECT_CONTEXT, projectDataViewModel);

    projectDataViewModel.setProjectList(sampleProjectItems);
</script>

<ToolbarContainer>
    <ProjectManagerButton/>
    <WorkingProject/>
</ToolbarContainer>
