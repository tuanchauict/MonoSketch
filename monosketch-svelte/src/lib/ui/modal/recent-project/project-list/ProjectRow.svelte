<script lang="ts">
import MenuItem from '../common/MenuItem.svelte';
import ProjectActions from './ProjectActions.svelte';
import ProjectIcon from './ProjectIcon.svelte';
import { ProjectAction, type ProjectItem } from '../model';

export let project: ProjectItem;
export let opening: boolean;
export let deleting: boolean;
export let onAction: (item: ProjectItem, action: ProjectAction) => void;

function openProject() {
    onAction(project, ProjectAction.Open);
}

function onActionClick(action: ProjectAction) {
    onAction(project, action);
}
</script>

<MenuItem onClick="{openProject}" danger="{deleting}">
    <ProjectIcon slot="icon" {opening} {deleting} />
    <span slot="content">{project.name}</span>
    <ProjectActions slot="actions" onAction="{onActionClick}" confirmingRemove="{deleting}" />
</MenuItem>
