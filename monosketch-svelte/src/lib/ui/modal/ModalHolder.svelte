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

let mainDropDownTarget: TargetBounds | null = null;
let currentFileDropDownTarget: TargetBounds | null = null;
let tooltip: Tooltip | null = null;
let shortcutModal: boolean = false;

const lifecycleOwner = new LifecycleOwner();
onMount(() => {
    lifecycleOwner.onStart();

    modalViewModel.mainDropDownMenuTargetFlow.observe(lifecycleOwner, (target) => {
        mainDropDownTarget = target;
    });

    modalViewModel.currentFileDropDownMenuTargetFlow.observe(lifecycleOwner, (target) => {
        currentFileDropDownTarget = target;
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

{#if currentFileDropDownTarget}
    <CurrentFileDropDown targetBounds="{currentFileDropDownTarget}" />
{/if}

{#if tooltip}
    <TooltipView {tooltip} />
{/if}

{#if shortcutModal}
    <KeyboardShortcutModal />
{/if}
