<script lang="ts">
import type { RenameProjectModel } from './model';
import NoBackgroundModal from '../common/NoBackgroundModal.svelte';
import { modalViewModel } from '../viewmodel';
import ProjectNameTextField from './ProjectNameTextField.svelte';
import { onMount } from 'svelte';
import { projectDataViewModel } from '../recent-project/viewmodel';

export let model: RenameProjectModel;

let name: string;

$: left = model.targetBounds.left;
$: top = model.targetBounds.top + model.targetBounds.height + 6;

function onDismiss() {
    if (name) {
        projectDataViewModel.setProjectName(model.id, name);
    }
    projectDataViewModel.setRenamingProject('');
    modalViewModel.renamingProjectModalStateFlow.value = null;
}

onMount(() => {
    const project = projectDataViewModel.getProject(model.id);
    if (project) {
        name = project.name;
    }
});
</script>

<NoBackgroundModal {onDismiss} {left} {top} width="{180}">
    <ProjectNameTextField bind:name />
</NoBackgroundModal>
