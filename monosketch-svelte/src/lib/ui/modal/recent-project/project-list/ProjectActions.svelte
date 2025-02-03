<script lang="ts">
import ProjectActionView from './ProjectActionView.svelte';
import { ProjectAction } from '$ui/nav/project/model';
import OpenInNewTab from './icons/OpenInNewTab.svelte';
import Remove from './icons/Remove.svelte';
import RemoveConfirmation from './icons/RemoveConfirmation.svelte';
import CancelRemoval from './icons/CancelRemoval.svelte';

export let confirmingRemove: boolean = false;
export let onAction: (action: ProjectAction) => void;
</script>

<div>
    {#if !confirmingRemove}
        <ProjectActionView
            action="{ProjectAction.OpenInNewTab}"
            onClick="{onAction}"
            tooltip="Open in new tab"
        >
            <OpenInNewTab />
        </ProjectActionView>
        <ProjectActionView action="{ProjectAction.Remove}" onClick="{onAction}" tooltip="Delete">
            <Remove />
        </ProjectActionView>
    {:else}
        <ProjectActionView
            action="{ProjectAction.RemoveConfirmed}"
            onClick="{onAction}"
            tooltip="Confirm"
        >
            <RemoveConfirmation />
        </ProjectActionView>
        <ProjectActionView
            action="{ProjectAction.CancelRemove}"
            onClick="{onAction}"
            tooltip="Cancel"
        >
            <CancelRemoval />
        </ProjectActionView>
    {/if}
</div>

<style lang="scss">
div {
    display: flex;
    flex-direction: row;
    gap: 12px;
}
</style>
