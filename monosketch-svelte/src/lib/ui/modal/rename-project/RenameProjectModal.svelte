<script lang="ts">
import NoBackgroundModal from '../common/NoBackgroundModal.svelte';
import ProjectNameTextField from './ProjectNameTextField.svelte';
import { getContext } from 'svelte';
import type { Rect } from "$libs/graphics-geo/rect";
import type { ProjectDataViewModel } from "$ui/nav/project/project-data-viewmodel";
import { PROJECT_CONTEXT } from "$ui/nav/project/constants";

export let projectName: string;
export let targetBounds: Rect;

const projectDataViewModel = getContext<ProjectDataViewModel>(PROJECT_CONTEXT);

let name: string = projectName;

$: left = targetBounds.left;
$: top = targetBounds.top + targetBounds.height + 6;

function onDone() {
    if (name) {
        projectDataViewModel.setCurrentProjectName(name);
    }
    // Reset the renaming project id to empty string. This will close the modal.
    projectDataViewModel.setRenamingProject('');
}
</script>

<NoBackgroundModal onDismiss="{onDone}" {left} {top} width="{180}">
    <ProjectNameTextField bind:name {onDone}/>
</NoBackgroundModal>
