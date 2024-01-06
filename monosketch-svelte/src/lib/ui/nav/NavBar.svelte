<script>
import ToolbarContainer from './common/ToolbarContainer.svelte';
import ScrollModeButton from './right/ScrollModeButton.svelte';
import { ScrollMode } from './right/model';
import AppDropdownMenu from './right/AppDropdownMenu.svelte';
import ThemeButton from './right/ThemeButton.svelte';
import Brand from './brand/Brand.svelte';
import MouseActionGroup from './mouseaction/MouseActionGroup.svelte';
import ProjectManagerButton from './project/ProjectManagerButton.svelte';
import WorkingFile from './project/WorkingFile.svelte';
</script>

<div class="container">
    <div class="left">
        <Brand />
        <MouseActionGroup />
    </div>

    <ToolbarContainer>
        <ProjectManagerButton />
        <WorkingFile filename="Demo" />
    </ToolbarContainer>

    <ToolbarContainer>
        <ScrollModeButton scrollMode="{ScrollMode.BOTH}" />
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
