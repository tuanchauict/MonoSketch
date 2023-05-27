/*
 * Copyright (c) 2023, tuanchauict
 */

package mono.state.onetimeaction

import mono.actionmanager.OneTimeActionType
import mono.html.toolbar.view.keyboardshortcut.KeyboardShortcuts
import mono.ui.appstate.AppUiStateManager
import mono.ui.appstate.AppUiStateManager.UiStatePayload

internal class AppSettingActionHelper(
    private val uiStateManager: AppUiStateManager
) {
    fun handleAppSettingAction(action: OneTimeActionType.AppSettingAction) {
        when (action) {
            OneTimeActionType.AppSettingAction.ShowFormatPanel ->
                uiStateManager.updateUiState(UiStatePayload.ShapeToolVisibility(true))

            OneTimeActionType.AppSettingAction.HideFormatPanel ->
                uiStateManager.updateUiState(UiStatePayload.ShapeToolVisibility(false))

            OneTimeActionType.AppSettingAction.ShowKeyboardShortcuts ->
                KeyboardShortcuts.showHint()

            is OneTimeActionType.AppSettingAction.ChangeFontSize ->
                uiStateManager.updateUiState(UiStatePayload.ChangeFontSize(action.isIncreased))
        }
    }
}
