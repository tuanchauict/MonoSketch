<script lang="ts">
import MainDropDown from './menu/main-dropdown/MainDropDown.svelte';
import { onDestroy, onMount } from 'svelte';
import { modalViewModel } from './viewmodel';
import { LifecycleOwner } from '../../mono/flow';
import type { TargetBounds } from './model';
import type { Tooltip } from './tooltip/model';
import TooltipView from './tooltip/TooltipView.svelte';
import KeyboardShortcutModal from './keyboard-shortcut/KeyboardShortcutModal.svelte';
import CurrentFileDropDown from './menu/currentfile/CurrentFileDropDown.svelte';
import RecentProjectDialog from './recent-project/RecentProjectDialog.svelte';
import type { RenameProjectModel } from './rename-project/model';
import RenameProjectModal from './rename-project/RenameProjectModal.svelte';

let mainDropDownTarget: TargetBounds | null = null;
let currentProjectDropDownTarget: TargetBounds | null = null;
let isProjectManagementModalVisible: boolean = false;
let renamingProjectModel: RenameProjectModel | null = null;
let tooltip: Tooltip | null = null;
let shortcutModal: boolean = false;

const lifecycleOwner = new LifecycleOwner();
onMount(() => {
    lifecycleOwner.onStart();

    modalViewModel.mainDropDownMenuTargetFlow.observe(lifecycleOwner, (target) => {
        mainDropDownTarget = target;
    });

    modalViewModel.currentFileDropDownMenuTargetFlow.observe(lifecycleOwner, (target) => {
        currentProjectDropDownTarget = target;
    });

    modalViewModel.projectManagementVisibilityStateFlow.observe(lifecycleOwner, (value) => {
        isProjectManagementModalVisible = value;
    });

    modalViewModel.renamingProjectModalStateFlow.observe(lifecycleOwner, (value) => {
        renamingProjectModel = value;
    });

    modalViewModel.tooltipFlow.observe(lifecycleOwner, (value) => {
        tooltip = value;
    });

    modalViewModel.keyboardShortcutVisibilityStateFlow.observe(lifecycleOwner, (value) => {
        shortcutModal = value;
    });
});

onDestroy(() => {
    lifecycleOwner.onStop();
});
</script>

{#if mainDropDownTarget}
    <MainDropDown targetBounds="{mainDropDownTarget}" />
{/if}

{#if currentProjectDropDownTarget}
    <CurrentFileDropDown targetBounds="{currentProjectDropDownTarget}" />
{/if}

{#if isProjectManagementModalVisible}
    <RecentProjectDialog />
{/if}

{#if renamingProjectModel}
    <RenameProjectModal model="{renamingProjectModel}" />
{/if}

{#if tooltip}
    <TooltipView {tooltip} />
{/if}

{#if shortcutModal}
    <KeyboardShortcutModal />
{/if}
