<script lang="ts">
    import AppIcon from '../common/AppIcon.svelte';
    import { TargetBounds } from '$ui/modal/model';
    import type { Rect } from "$libs/graphics-geo/rect";
    import MainDropDown from "$ui/modal/menu/main-dropdown/MainDropDown.svelte";
    import KeyboardShortcutModal from "$ui/modal/keyboard-shortcut/KeyboardShortcutModal.svelte";
    import { MainMenuAction } from "$ui/modal/menu/main-dropdown/model";
    import { UiStatePayload } from "$mono/ui-state-manager/ui-state-payload";
    import { getAppContext } from "$mono/common/constant";

    const appContext = getAppContext();

    let targetView: HTMLElement | undefined;
    let targetBounds: Rect | undefined;

    let isKeyboardShortcutsVisible = false;

    function showDropdown(e: MouseEvent) {
        targetView = e.currentTarget as HTMLElement;
        updateTargetBounds();
    }

    function onDismiss() {
        targetView = undefined;
        targetBounds = undefined;
    }

    function updateTargetBounds() {
        targetBounds = targetView ? TargetBounds.fromElement(targetView) : undefined;
    }

    function onAction(action: MainMenuAction) {
        switch (action) {
            case MainMenuAction.IncreaseFontSize:
                appContext.appUiStateManager.updateUiState(UiStatePayload.ChangeFontSize(true));
                break;
            case MainMenuAction.DecreaseFontSize:
                appContext.appUiStateManager.updateUiState(UiStatePayload.ChangeFontSize(false));
                break;
            case MainMenuAction.ShowFormatPanel:
                appContext.appUiStateManager.updateUiState(UiStatePayload.ShapeToolVisibility(true));
                break;
            case MainMenuAction.HideFormatPanel:
                appContext.appUiStateManager.updateUiState(UiStatePayload.ShapeToolVisibility(false));
                break;
            case MainMenuAction.ShowKeyboardShortcut:
                isKeyboardShortcutsVisible = true;
        }
    }
</script>
<svelte:window on:resize="{updateTargetBounds}"/>

<AppIcon size="{20}" viewBoxSize="{20}" onClick="{showDropdown}">
    <path
            d="M5.2 9.6C5.2 10.4837 4.48366 11.2 3.6 11.2C2.71634 11.2 2 10.4837 2 9.6C2 8.71634 2.71634 8 3.6 8C4.48366 8 5.2 8.71634 5.2 9.6Z"
    ></path>
    <path
            d="M11.6 9.6C11.6 10.4837 10.8837 11.2 10 11.2C9.11634 11.2 8.4 10.4837 8.4 9.6C8.4 8.71634 9.11634 8 10 8C10.8837 8 11.6 8.71634 11.6 9.6Z"
    ></path>
    <path
            d="M18 9.6C18 10.4837 17.2837 11.2 16.4 11.2C15.5163 11.2 14.8 10.4837 14.8 9.6C14.8 8.71634 15.5163 8 16.4 8C17.2837 8 18 8.71634 18 9.6Z"
    ></path>
</AppIcon>

{#if targetBounds}
    <MainDropDown targetBounds="{targetBounds}" {onAction} {onDismiss}/>
{/if}

{#if isKeyboardShortcutsVisible}
    <KeyboardShortcutModal onDismiss="{() => isKeyboardShortcutsVisible = false}"/>
{/if}
