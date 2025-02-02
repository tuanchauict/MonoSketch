<script lang="ts">
import { onDestroy, onMount } from 'svelte';
import { modalViewModel } from './viewmodel';
import { LifecycleOwner } from '$libs/flow';
import KeyboardShortcutModal from './keyboard-shortcut/KeyboardShortcutModal.svelte';
import type { ExistingProjectModel } from "$ui/modal/existing-project/model";
import ExistingProjectDialog from "$ui/modal/existing-project/ExistingProjectDialog.svelte";

let shortcutModal: boolean = false;

let existingProjectModel: ExistingProjectModel | null = null;

const lifecycleOwner = new LifecycleOwner();
onMount(() => {
    lifecycleOwner.onStart();

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

{#if shortcutModal}
    <KeyboardShortcutModal />
{/if}

{#if existingProjectModel}
    <ExistingProjectDialog model="{existingProjectModel}" />
{/if}
