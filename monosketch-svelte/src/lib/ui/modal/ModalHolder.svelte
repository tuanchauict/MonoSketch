<script lang="ts">
import { onDestroy, onMount } from 'svelte';
import { modalViewModel } from './viewmodel';
import { LifecycleOwner } from '$libs/flow';
import KeyboardShortcutModal from './keyboard-shortcut/KeyboardShortcutModal.svelte';
import type { RenameProjectModel } from './rename-project/model';
import RenameProjectModal from './rename-project/RenameProjectModal.svelte';
import type { ExistingProjectModel } from "$ui/modal/existing-project/model";
import ExistingProjectDialog from "$ui/modal/existing-project/ExistingProjectDialog.svelte";

let renamingProjectModel: RenameProjectModel | null = null;
let shortcutModal: boolean = false;

let existingProjectModel: ExistingProjectModel | null = null;

const lifecycleOwner = new LifecycleOwner();
onMount(() => {
    lifecycleOwner.onStart();

    modalViewModel.renamingProjectModalStateFlow.observe(lifecycleOwner, (value) => {
        renamingProjectModel = value;
    });

    modalViewModel.keyboardShortcutVisibilityStateFlow.observe(lifecycleOwner, (value) => {
        shortcutModal = value;
    });

    modalViewModel.existingProjectFlow.observe(lifecycleOwner, (value) => {
        existingProjectModel = value;
    });
});

onDestroy(() => {
    lifecycleOwner.onStop();
});
</script>

{#if renamingProjectModel}
    <RenameProjectModal model="{renamingProjectModel}" />
{/if}

{#if shortcutModal}
    <KeyboardShortcutModal />
{/if}

{#if existingProjectModel}
    <ExistingProjectDialog model="{existingProjectModel}" />
{/if}
