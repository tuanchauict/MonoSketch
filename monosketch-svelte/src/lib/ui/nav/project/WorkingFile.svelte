<script lang="ts">
import WorkingFileView from './WorkingFileView.svelte';
import { projectDataViewModel } from '../../modal/recent-project/viewmodel';
import { Flow, LifecycleOwner } from '../../../mono/flow';
import { onDestroy, onMount } from 'svelte';
import { modalViewModel } from '../../modal/viewmodel';
import { TargetBounds } from '../../modal/model';

let filename = '';
let node: HTMLElement;

const lifecycleOwner = new LifecycleOwner();
const openingFileFlow = Flow.combine2(
    projectDataViewModel.openingProjectIdFlow,
    projectDataViewModel.renamingProjectIdFlow,
    (id) => projectDataViewModel.getProject(id),
);

const renamingFileFlow = projectDataViewModel.renamingProjectIdFlow.map((id) => {
    const project = projectDataViewModel.getProject(id);
    return project ? project : null;
});

onMount(() => {
    lifecycleOwner.onStart();
    openingFileFlow.observe(lifecycleOwner, (project) => {
        console.log('project', project);
        // TODO: make the flow mapping ignore undefined value as return type.
        filename = project!!.name;
    });

    renamingFileFlow.observe(lifecycleOwner, (project) => {
        if (!project) {
            return;
        }
        modalViewModel.renamingProjectModalStateFlow.value = {
            id: project!!.id,
            targetBounds: TargetBounds.fromElement(node),
        };
    });
});

onDestroy(() => {
    lifecycleOwner.onStop();
});
</script>

<div bind:this="{node}">
    <WorkingFileView {filename} />
</div>

<style lang="scss">
div {
    display: flex;
}
</style>
