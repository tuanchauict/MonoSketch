<script lang="ts">
import FileActionView from './FileActionView.svelte';
import { FileAction } from '../model';
import OpenInNewTab from './icons/OpenInNewTab.svelte';
import Remove from './icons/Remove.svelte';
import RemoveConfirmation from './icons/RemoveConfirmation.svelte';
import CancelRemoval from './icons/CancelRemoval.svelte';

export let confirmingRemove: boolean = false;
export let onAction: (action: FileAction) => void;
</script>

<div>
    {#if !confirmingRemove}
        <FileActionView action="{FileAction.OpenInNewTab}" onClick="{onAction}">
            <OpenInNewTab />
        </FileActionView>
        <FileActionView action="{FileAction.Remove}" onClick="{onAction}">
            <Remove />
        </FileActionView>
    {:else}
        <FileActionView action="{FileAction.RemoveConfirmed}" onClick="{onAction}">
            <RemoveConfirmation />
        </FileActionView>
        <FileActionView action="{FileAction.CancelRemove}" onClick="{onAction}">
            <CancelRemoval />
        </FileActionView>
    {/if}
</div>

<style lang="scss">
div {
    display: flex;
    flex-direction: row;
    gap: 8px;
}
</style>
