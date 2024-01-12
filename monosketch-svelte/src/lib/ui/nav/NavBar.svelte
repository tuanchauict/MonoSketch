<script>
import ToolbarContainer from './common/ToolbarContainer.svelte';
import ScrollModeButton from './right/ScrollModeButton.svelte';
import AppDropdownMenu from './right/AppDropdownMenu.svelte';
import ThemeButton from './right/ThemeButton.svelte';
import Brand from './brand/Brand.svelte';
import MouseActionGroup from './mouseaction/MouseActionGroup.svelte';
import ProjectManagerButton from './project/ProjectManagerButton.svelte';
import WorkingProject from './project/WorkingProject.svelte';
</script>

<div class="container">
    <div class="left">
        <Brand />
        <MouseActionGroup />
    </div>

    <ToolbarContainer>
        <ProjectManagerButton />
        <WorkingProject />
    </ToolbarContainer>

    <ToolbarContainer>
        <ScrollModeButton/>
        <ThemeButton />
        <AppDropdownMenu />
    </ToolbarContainer>
</div>

<style>
.container {
    display: flex;
    justify-content: space-between;
    width: 100%;
    height: 100%;

    color: white;
}

.left {
    display: flex;
    flex-direction: row;
    align-items: center;
}
</style>
