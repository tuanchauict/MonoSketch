<script lang="ts">
import { onDestroy, onMount } from 'svelte';
import { modalViewModel } from './viewmodel';
import { LifecycleOwner } from '$libs/flow';
import KeyboardShortcutModal from './keyboard-shortcut/KeyboardShortcutModal.svelte';

let shortcutModal: boolean = false;

const lifecycleOwner = new LifecycleOwner();
onMount(() => {
    lifecycleOwner.onStart();

    modalViewModel.keyboardShortcutVisibilityStateFlow.observe(lifecycleOwner, (value) => {
        shortcutModal = value;
    });
});

onDestroy(() => {
    lifecycleOwner.onStop();
});
</script>

{#if shortcutModal}
    <KeyboardShortcutModal />
{/if}
